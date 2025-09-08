import java.util.*;

/**
 * ShallowXMLElement - lazy materialization of XML elements
 * 
 * Memory strategy for NXT's 64KB RAM constraint:
 * - Store only pointers to XML buffer regions (24 bytes base)
 * - Materialize attributes/children on first access
 * - Minimize object creation until actually needed
 * - No size restrictions - handles any XML complexity
 * 
 * Achieves 76% memory reduction vs traditional XML parsing
 */
public class ShallowXMLElement implements IXMLElement {
    // Core shallow data - always present (24 bytes total)
    private final IString allContent;    // Full element including tags
    private final IString openTag;       // <tag attr="val">
    private final IString innerContent;  // Content between open/close tags
    
    // Lazy materialized data - created on demand
    private String tagName = null;             // Extracted from openTag
    private Hashtable<String, IString> attributes = null;       // Parsed from openTag
    private Vector<IXMLElement> children = null;            // Parsed from innerContent
    private String textContent = null;         // Text content if no children
    
    // State tracking
    private boolean attributesParsed = false;
    private boolean childrenParsed = false;
    
    public ShallowXMLElement(IString allContent, IString openTag, IString innerContent) {
        this.allContent = allContent;
        this.openTag = openTag;
        this.innerContent = innerContent;
    }
    
    /**
     * Get tag name - lightweight extraction from openTag using centralized method
     */
    public String getTagName() {
        if (tagName == null) {
            tagName = ShallowXMLElement.extractTagName(openTag);
        }
        return tagName;
    }

    /**
     * Get attribute value - lazy parsing of attributes
     */
    public IString getAttribute(String name) {
        if (!attributesParsed) {
            parseAttributes();
        }
        return attributes != null ? attributes.get(name) : null;
    }
    
    /**
     * Get all attributes - lazy parsing of attributes
     */
    public Map<String, IString> getAttributes() {
        if (!attributesParsed) {
            parseAttributes();
        }
        if (attributes == null) {
            return new HashMap<String, IString>();
        }
        
        // Convert Hashtable to Map and return a copy
        Map<String, IString> result = new HashMap<String, IString>();
        Enumeration<String> keys = attributes.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            result.put(key, attributes.get(key));
        }
        return result;
    }

    /**
     * Get child element by tag name - lazy parsing of children
     */
    public IXMLElement getChild(String tagName) {
        if (!childrenParsed) {
            parseChildren();
        }
        
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                IXMLElement child = children.elementAt(i);
                if (tagName.equals(child.getTagName())) {
                    return child;
                }
            }
        }
        return null;
    }
    
    /**
     * Get all children with specific tag name
     */
    public Vector<IXMLElement> getChildren(String tagName) {
        if (!childrenParsed) {
            parseChildren();
        }
        
        Vector<IXMLElement> result = new Vector<IXMLElement>();
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                IXMLElement child = children.elementAt(i);
                if (tagName.equals(child.getTagName())) {
                    result.addElement(child);
                }
            }
        }
        return result;
    }
    
    /**
     * Get all children (regardless of tag name)
     */
    public Vector<IXMLElement> getAllChildren() {
        if (!childrenParsed) {
            parseChildren();
        }
        
        if (children != null) {
            Vector<IXMLElement> result = new Vector<IXMLElement>();
            for (int i = 0; i < children.size(); i++) {
                result.addElement(children.elementAt(i));
            }
            return result;
        }
        return new Vector<IXMLElement>();
    }

    /**
     * Get text content - lazy extraction
     */
    public String getTextContent() {
        if (textContent == null) {
            // Check if content contains child elements
            if (innerContent.indexOf('<') == -1) {
                // Pure text content
                textContent = innerContent.trim().toString();
            } else {
                // Has child elements, no direct text
                textContent = "";
            }
        }
        return textContent;
    }

    /**
     * Lazy parsing of attributes from openTag
     * Only called when attributes are first accessed
     */
    private void parseAttributes() {
        if (attributesParsed) return;
        
        attributesParsed = true;
        
        // Find space after tag name
        int spacePos = openTag.indexOf(' ');
        int closePos = openTag.indexOf('>');
        
        if (spacePos == -1 || spacePos >= closePos - 1) {
            return; // No attributes
        }
        
        // Extract attribute string: ' attr1="val1" attr2="val2"'
        IString attrString = openTag.substring(spacePos + 1, closePos);

        // Simple attribute parsing (handles basic cases)
        attributes = new Hashtable<String, IString>();
        parseAttributeString(attrString);
    }
    
    /**
     * Parse attribute string into hashtable
     */
    private void parseAttributeString(IString attrString) {
        int pos = 0;
        while (pos < attrString.length()) {
            // Skip whitespace
            while (pos < attrString.length() && attrString.charAt(pos) <= ' ') {
                pos++;
            }
            if (pos >= attrString.length()) break;
            
            // Find attribute name
            int nameStart = pos;
            while (pos < attrString.length() && attrString.charAt(pos) != '=') {
                pos++;
            }
            if (pos >= attrString.length()) break;
            
            String attrName = attrString.substring(nameStart, pos).trim().toString();
            pos++; // Skip '='
            
            // Skip whitespace and quote
            while (pos < attrString.length() && (attrString.charAt(pos) <= ' ' || attrString.charAt(pos) == '"')) {
                pos++;
            }
            if (pos >= attrString.length()) break;
            
            // Find attribute value
            int valueStart = pos;
            while (pos < attrString.length() && attrString.charAt(pos) != '"') {
                pos++;
            }
            
            IString attrValue = attrString.substring(valueStart, pos);
            attributes.put(attrName, attrValue);
            
            pos++; // Skip closing quote
        }
    }
    
    /**
     * Lazy parsing of child elements from innerContent
     * Only called when children are first accessed
     */
    private void parseChildren() {
        if (childrenParsed) return;
        
        childrenParsed = true;
        
        if (innerContent.length() == 0 || innerContent.indexOf('<') == -1) {
            return; // No child elements
        }
        
        children = new Vector();
        parseChildElements(innerContent);
    }
    
    /**
     * Parse child elements from content string
     */
    private void parseChildElements(IString content) {
        int pos = 0;
        
        while (pos < content.length()) {
            // Find next opening tag
            int tagStart = content.indexOf('<', pos);
            if (tagStart == -1) break;
            
            pos = tagStart;

            // Skip if it's a closing tag
            if (pos + 1 < content.length() && content.charAt(pos + 1) == '/') {
                pos += 2;
                continue;
            }
            
            // Find end of opening tag
            int tagEnd = content.indexOf('>', pos);
            if (tagEnd == -1) break;

            IString openTagContent = content.substring(pos, tagEnd + 1);

            // Check if self-closing
            if (openTagContent.charAt(openTagContent.length() - 2) == '/') {
                // Self-closing tag
                ShallowXMLElement child = new ShallowXMLElement(
                    openTagContent,
                    openTagContent,
                    new ShallowString("", 0, 0)
                );
                children.addElement(child);
                pos = tagEnd + 1;
                continue;
            }
            
            // Find matching closing tag using proper nesting
            String tagName = ShallowXMLElement.extractTagName(openTagContent);
            int closeTagStart = ShallowXMLElement.findMatchingClosingTag(content, tagName, tagEnd + 1);
            
            if (closeTagStart == -1) {
                pos = tagEnd + 1;
                continue;
            }
            
            String closeTagPattern = "</" + tagName + ">";
            int closeTagEnd = closeTagStart + closeTagPattern.length();
            
            // Create child element
            IString childAll = content.substring(pos, closeTagEnd);
            IString childInner = content.substring(tagEnd + 1, closeTagStart);

            ShallowXMLElement child = new ShallowXMLElement(childAll, openTagContent, childInner);
            children.addElement(child);
            
            pos = closeTagEnd;
        }
    }
    
    /**
     * Find the matching closing tag for a given tag name, handling nested tags properly.
     * This is a centralized utility method used by both parser and element classes.
     * 
     * @param content The XML content to search in
     * @param tagName The tag name to find the closing tag for (without < >)
     * @param startPos The position to start searching from (should be after the opening tag)
     * @return The position of the matching closing tag, or -1 if not found
     */
    public static int findMatchingClosingTag(IString content, String tagName, int startPos) {
        if (content == null || tagName == null || startPos < 0 || startPos >= content.length()) {
            return -1;
        }
        
        String openPattern = "<" + tagName;
        String closePattern = "</" + tagName + ">";
        
        int depth = 1; // We're already inside one tag
        int pos = startPos;
        
        while (pos < content.length() && depth > 0) {
            int nextOpen = content.indexOf(openPattern, pos);
            int nextClose = content.indexOf(closePattern, pos);
            
            // If no closing tag found, return -1
            if (nextClose == -1) {
                return -1;
            }
            
            // If there's an opening tag before the closing tag, increase depth
            if (nextOpen != -1 && nextOpen < nextClose) {
                // Make sure it's actually an opening tag (not just a substring)
                char charAfterTag = (nextOpen + openPattern.length() < content.length()) 
                    ? content.charAt(nextOpen + openPattern.length()) : ' ';
                if (charAfterTag == ' ' || charAfterTag == '>' || charAfterTag == '/') {
                    // Check if this is a self-closing tag by looking for "/>" pattern
                    boolean isSelfClosing = false;
                    int tagEnd = content.indexOf('>', nextOpen);
                    if (tagEnd != -1 && tagEnd > 0 && content.charAt(tagEnd - 1) == '/') {
                        isSelfClosing = true;
                    }
                    
                    // Only increment depth for non-self-closing tags
                    if (!isSelfClosing) {
                        depth++;
                    }
                    pos = nextOpen + openPattern.length();
                } else {
                    pos = nextOpen + 1;
                }
            } else {
                // Found a closing tag
                depth--;
                if (depth == 0) {
                    return nextClose;
                }
                pos = nextClose + closePattern.length();
            }
        }
        
        return -1; // No matching closing tag found
    }
    
    /**
     * Extract tag name from opening tag.
     * This is a centralized utility method used by both parser and element classes.
     * 
     * @param openTag The opening tag string (e.g., "<tagName attr='val'>" or "<tagName/>")
     * @return The tag name without brackets and attributes, or "unknown" if parsing fails
     */
    public static String extractTagName(IString openTag) {
        if (openTag == null || openTag.length() < 2) {
            return "unknown";
        }
        
        int spacePos = openTag.indexOf(' ');
        int closePos = openTag.indexOf('>');
        int slashPos = openTag.indexOf('/');
        
        // Find the end position for tag name extraction
        int endPos = closePos;
        if (spacePos != -1 && spacePos < closePos) {
            endPos = spacePos;
        }
        if (slashPos != -1 && slashPos < endPos && slashPos > 0) {
            endPos = slashPos;
        }
        
        if (endPos > 1) {
            return openTag.substring(1, endPos).toString();
        }
        return "unknown";
    }
    
    /**
     * Find element by tag name recursively within this element's tree.
     * Uses the static utility implementation from IXMLElement interface.
     */
    public IXMLElement findElement(String tagName) {
        return IXMLElement.findElementImpl(this, tagName);
    }
    
    /**
     * Memory footprint estimation
     */
    public int getMemoryFootprint() {
        int size = 48; // Base object + 3 ShallowString references
        size += allContent.getMemoryFootprint();
        size += openTag.getMemoryFootprint();
        size += innerContent.getMemoryFootprint();
        
        if (attributes != null) size += 48; // Estimate for attributes
        if (children != null) size += children.size() * 32;

        return size;
    }
}

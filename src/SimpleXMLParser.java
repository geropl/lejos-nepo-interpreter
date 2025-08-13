import java.io.*;
import java.util.*;

/**
 * Simple XML parser for NEPO programs
 * 
 * This is a lightweight XML parser designed specifically for NEPO XML structure
 * since leJOS NXT doesn't include standard XML parsing libraries.
 */
public class SimpleXMLParser {
    
    // Helper methods for String operations not available in leJOS
    private static boolean stringEndsWith(String str, String suffix) {
        if (str.length() < suffix.length()) return false;
        return str.substring(str.length() - suffix.length()).equals(suffix);
    }
    
    private static boolean stringStartsWith(String str, String prefix) {
        if (str.length() < prefix.length()) return false;
        return str.substring(0, prefix.length()).equals(prefix);
    }
    
    private static boolean stringContains(String str, String substring) {
        return str.indexOf(substring) != -1;
    }
    
    private static String[] stringSplit(String str, String delimiter) {
        Vector parts = new Vector();
        int start = 0;
        int pos = str.indexOf(delimiter);
        
        while (pos != -1) {
            if (pos > start) {
                parts.addElement(str.substring(start, pos));
            }
            start = pos + delimiter.length();
            pos = str.indexOf(delimiter, start);
        }
        
        if (start < str.length()) {
            parts.addElement(str.substring(start));
        }
        
        String[] result = new String[parts.size()];
        for (int i = 0; i < parts.size(); i++) {
            result[i] = (String) parts.elementAt(i);
        }
        return result;
    }
    
    private static String stringTrim(String str) {
        int start = 0;
        int end = str.length();
        
        while (start < end && str.charAt(start) <= ' ') {
            start++;
        }
        
        while (end > start && str.charAt(end - 1) <= ' ') {
            end--;
        }
        
        return str.substring(start, end);
    }

    /**
     * Simple XML element representation
     */
    public static class XMLElement {
        public String tagName;
        public Hashtable attributes;
        public Vector children;
        public String textContent;
        
        public XMLElement(String tagName) {
            this.tagName = tagName;
            this.attributes = new Hashtable();
            this.children = new Vector();
            this.textContent = "";
        }
        
        public String getAttribute(String name) {
            return (String) attributes.get(name);
        }
        
        public void setAttribute(String name, String value) {
            attributes.put(name, value);
        }
        
        public void addChild(XMLElement child) {
            children.addElement(child);
        }
        
        public XMLElement getChild(String tagName) {
            for (int i = 0; i < children.size(); i++) {
                XMLElement child = (XMLElement) children.elementAt(i);
                if (tagName.equals(child.tagName)) {
                    return child;
                }
            }
            return null;
        }
        
        public Vector getChildren(String tagName) {
            Vector result = new Vector();
            for (int i = 0; i < children.size(); i++) {
                XMLElement child = (XMLElement) children.elementAt(i);
                if (tagName.equals(child.tagName)) {
                    result.addElement(child);
                }
            }
            return result;
        }
        
        public XMLElement getChildWithAttribute(String tagName, String attrName, String attrValue) {
            Vector children = getChildren(tagName);
            for (int i = 0; i < children.size(); i++) {
                XMLElement child = (XMLElement) children.elementAt(i);
                if (attrValue.equals(child.getAttribute(attrName))) {
                    return child;
                }
            }
            return null;
        }
    }
    
    /**
     * Parse NEPO XML from string
     */
    public static XMLElement parseXML(String xmlContent) {
        try {
            // Remove XML declaration and whitespace
            xmlContent = stringTrim(xmlContent);
            if (stringStartsWith(xmlContent, "<?xml")) {
                int endDecl = xmlContent.indexOf("?>");
                if (endDecl != -1) {
                    xmlContent = stringTrim(xmlContent.substring(endDecl + 2));
                }
            }

            return parseElement(xmlContent, 0).element;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Parse XML from file
     */
    public static XMLElement parseFile(String filename) {
        try {
            File file = new File(filename);
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            
            String content = new String(buffer);
            return parseXML(content);

        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Parse result container
     */
    private static class ParseResult {
        XMLElement element;
        int nextIndex;
        
        ParseResult(XMLElement element, int nextIndex) {
            this.element = element;
            this.nextIndex = nextIndex;
        }
    }
    
    /**
     * Parse a single XML element
     */
    private static ParseResult parseElement(String xml, int startIndex) {
        // Find opening tag
        int tagStart = xml.indexOf('<', startIndex);
        if (tagStart == -1) return null;
        
        int tagEnd = xml.indexOf('>', tagStart);
        if (tagEnd == -1) return null;
        
        String tagContent = xml.substring(tagStart + 1, tagEnd);
        
        // Check for self-closing tag
        boolean selfClosing = stringEndsWith(tagContent, "/");
        if (selfClosing) {
            tagContent = stringTrim(tagContent.substring(0, tagContent.length() - 1));
        }
        
        // Parse tag name and attributes - split on whitespace
        String[] parts = stringSplit(tagContent, " ");
        String tagName = parts[0];
        
        XMLElement element = new XMLElement(tagName);
        
        // Parse attributes
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            int eqIndex = part.indexOf('=');
            if (eqIndex != -1) {
                String attrName = part.substring(0, eqIndex);
                String attrValue = part.substring(eqIndex + 1);
                // Remove quotes
                if (stringStartsWith(attrValue, "\"") && stringEndsWith(attrValue, "\"")) {
                    attrValue = attrValue.substring(1, attrValue.length() - 1);
                }
                element.setAttribute(attrName, attrValue);
            }
        }
        
        if (selfClosing) {
            return new ParseResult(element, tagEnd + 1);
        }
        
        // Parse content and children
        int contentStart = tagEnd + 1;
        int currentIndex = contentStart;
        
        while (currentIndex < xml.length()) {
            // Look for next tag or closing tag
            int nextTag = xml.indexOf('<', currentIndex);
            if (nextTag == -1) break;
            
            // Check if it's a closing tag
            if (xml.charAt(nextTag + 1) == '/') {
                // Found closing tag
                int closeEnd = xml.indexOf('>', nextTag);
                String closeTagName = xml.substring(nextTag + 2, closeEnd);
                if (tagName.equals(closeTagName)) {
                    // Extract text content if any
                    String textContent = stringTrim(xml.substring(contentStart, nextTag));
                    if (textContent.length() > 0 && !stringContains(textContent, "<")) {
                        element.textContent = textContent;
                    }
                    return new ParseResult(element, closeEnd + 1);
                }
            } else {
                // Parse child element
                ParseResult childResult = parseElement(xml, nextTag);
                if (childResult != null) {
                    element.addChild(childResult.element);
                    currentIndex = childResult.nextIndex;
                } else {
                    currentIndex = nextTag + 1;
                }
            }
        }
        
        return new ParseResult(element, xml.length());
    }
}

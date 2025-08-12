import java.io.*;
import java.util.*;

/**
 * Simple XML parser for NEPO programs
 * 
 * This is a lightweight XML parser designed specifically for NEPO XML structure
 * since leJOS NXT doesn't include standard XML parsing libraries.
 */
public class SimpleXMLParser {
    
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
            xmlContent = xmlContent.trim();
            if (xmlContent.startsWith("<?xml")) {
                int endDecl = xmlContent.indexOf("?>");
                if (endDecl != -1) {
                    xmlContent = xmlContent.substring(endDecl + 2).trim();
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
            FileInputStream fis = new FileInputStream(filename);
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
        boolean selfClosing = tagContent.endsWith("/");
        if (selfClosing) {
            tagContent = tagContent.substring(0, tagContent.length() - 1).trim();
        }
        
        // Parse tag name and attributes
        String[] parts = tagContent.split("\\s+");
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
                if (attrValue.startsWith("\"") && attrValue.endsWith("\"")) {
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
                    String textContent = xml.substring(contentStart, nextTag).trim();
                    if (textContent.length() > 0 && !textContent.contains("<")) {
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

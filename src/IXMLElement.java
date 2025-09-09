import java.util.*;

/**
 * Interface for lazy-materialized XML elements with memory optimization
 */
public interface IXMLElement {
    String getTagName();
    IString getAttribute(String name);
    Map<String, IString> getAttributes();
    IXMLElement getChild(String tagName);
    Vector<IXMLElement> getChildren(String tagName);
    Vector<IXMLElement> getAllChildren();
    String getTextContent();
    int getMemoryFootprint();
    
    /**
     * Find element by tag name recursively within this element's tree.
     * @param tagName The tag name to search for
     * @return First matching element or null if not found
     */
    IXMLElement findElement(String tagName);
    
    /**
     * Find element by type attribute value recursively within this element's tree.
     * @param typeValue The type attribute value to search for
     * @return First matching element or null if not found
     */
    IXMLElement findElementByTypeAttr(String typeValue);
    
    /**
     * Static utility implementation of findElement that can be reused by all implementations.
     * Performs breadth-first search for an element with the specified tag name.
     * 
     * @param parent The parent element to search within
     * @param tagName The tag name to search for
     * @return First matching element or null if not found
     */
    static IXMLElement findElementImpl(IXMLElement parent, String tagName) {
        // Use a queue for breadth-first search
        Vector<IXMLElement> queue = new Vector<>();
        queue.add(parent);
        
        while (!queue.isEmpty()) {
            IXMLElement current = queue.elementAt(0);
            queue.removeElementAt(0);
            
            // Check if this element matches
            if (tagName.equals(current.getTagName())) {
                return current;
            }
            
            // Add all children to the queue for next level processing
            Vector<IXMLElement> children = current.getAllChildren();
            for (int i = 0; i < children.size(); i++) {
                queue.add(children.elementAt(i));
            }
        }
        
        return null; // Not found
    }
    
    /**
     * Static utility implementation of findElementByTypeAttr that can be reused by all implementations.
     * Performs breadth-first search for an element with the specified type attribute value.
     * 
     * @param parent The parent element to search within
     * @param typeValue The type attribute value to search for
     * @return First matching element or null if not found
     */
    static IXMLElement findElementByTypeAttrImpl(IXMLElement parent, String typeValue) {
        Vector<IXMLElement> queue = new Vector<>();
        queue.add(parent);
        
        while (!queue.isEmpty()) {
            IXMLElement current = queue.elementAt(0);
            queue.removeElementAt(0);
            
            // Check if this element has the target type attribute
            IString typeAttr = current.getAttribute("type");
            if (typeAttr != null && typeValue.equals(typeAttr.toString())) {
                return current;
            }
            
            // Add all children to the queue for next level processing
            Vector<IXMLElement> children = current.getAllChildren();
            for (int i = 0; i < children.size(); i++) {
                queue.add(children.elementAt(i));
            }
        }
        
        return null; // Not found
    }
}

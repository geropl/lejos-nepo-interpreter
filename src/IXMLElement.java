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
     * Static utility implementation of findElement that can be reused by all implementations.
     * Performs recursive depth-first search for an element with the specified tag name.
     * 
     * @param parent The parent element to search within
     * @param tagName The tag name to search for
     * @return First matching element or null if not found
     */
    static IXMLElement findElementImpl(IXMLElement parent, String tagName) {
        if (tagName.equals(parent.getTagName())) {
            return parent;
        }
        
        Vector<IXMLElement> children = parent.getAllChildren();
        for (int i = 0; i < children.size(); i++) {
            IXMLElement found = findElementImpl(children.elementAt(i), tagName);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}

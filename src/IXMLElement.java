import java.util.*;

/**
 * Interface for lazy-materialized XML elements with memory optimization
 */
public interface IXMLElement {
    String getTagName();
    IString getAttribute(String name);
    IXMLElement getChild(String tagName);
    Vector<IXMLElement> getChildren(String tagName);
    Vector<IXMLElement> getAllChildren();
    String getTextContent();
    int getMemoryFootprint();
}

/**
 * Interface for memory-optimized XML parsing operations
 */
public interface IXMLParser {
    IXMLElement parseFile(String filename);
    IXMLElement parseXML(String xmlContent);
}

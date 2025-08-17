/**
 * Interface for memory-efficient string operations using offset/length approach
 */
public interface IString {
    int length();
    char charAt(int index);
    IString trim();

    boolean startsWith(String prefix);
    int indexOf(char c);
    int indexOf(String str);

    IString substring(int start);
    IString substring(int start, int end);
    
    boolean equals(Object obj);
    boolean equals(String str);

    String toString();

    int getMemoryFootprint();
}

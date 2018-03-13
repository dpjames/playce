/* Generated By:JavaCC: Do not edit this line. Provider.java Version 7.0 */
/* JavaCCOptions:KEEP_LINE_COLUMN=true */
package org.semanticweb.owlapi.krss1.parser;


import java.io.IOException;
@SuppressWarnings("all")
interface Provider {
    /**
     * Reads characters into an array
     * @param buffer  Destination buffer
     * @param offset   Offset at which to start storing characters
     * @param length   The maximum possible number of characters to read
     * @return The number of characters read, or -1 if all read
     * @exception  IOException
     */
    int read(char buffer[], int offset, int length) throws IOException;
    
    /**
     * Closes the stream and releases any system resources associated with
     * it.
     * @exception IOException
     */
     void close() throws IOException;
}
/* JavaCC - OriginalChecksum=90309369ad95f2156e0ba8a86f2b8a6b (do not edit this line) */

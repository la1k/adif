package no.la1k.adif;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 *
 */
public class ADIFReader {
    private Reader in;

    public ADIFReader(Reader input) throws IOException {
        in = input;
    }
    
    public ADIFReader(String fileName) throws IOException {
        this(new FileReader(fileName));
    }

    /**
     * Retrieve the next ADIF record.
     *
     * @return ADIF record, null if end-of-file.
     */
    public ADIFRecord next() throws ADIFException {
        return ADIFRecord.read(in);
    }
}

package no.la1k.adif;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 *
 *
 */
public class ADIFWriter {
    private static final String END_OF_HEADER = "<eoh>";
    private final PrintWriter pw;
    public ADIFWriter (Writer out, String header) throws IOException {
        pw = new PrintWriter(out);
        writeHeader(header);
    }

    private void writeHeader(String header) throws IOException {
        if (header != null) {
            pw.print(header);
            pw.println(END_OF_HEADER);
        }
    }

    public void write (ADIFRecord record) throws IOException {
        record.write(pw);
    }
}

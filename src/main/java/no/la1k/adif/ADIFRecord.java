package no.la1k.adif;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of an ADIF record.
 *
 * An ADIF record consists of a set of ADIF field, and
 * is terminated with an END_OF_RECORD field.
 */
public class ADIFRecord {
    private static final String END_OF_RECORD = "<eor>\n";

    public static ADIFRecord read(Reader in) throws ADIFException {
        ADIFRecord record = new ADIFRecord();
        for (;;) {
            ADIFField field = ADIFField.read(in);
            if (field == null)
                throw new ADIFException("Malformed record. Expected field or <eor>.");
            if ("EOR".equalsIgnoreCase(field.getName()))
                return record;
            record.add(field);
        }
    }

    public ADIFField getField (String name) {
        if (name == null) return null;
        for (ADIFField field : fields) {
            if (name.equalsIgnoreCase(field.getName())) {
                return field;
            }
        }
        return null;
    }

    public ADIFField getField (int index) {
        if (index >= fields.size())
            return null;
        return fields.get(index);
    }
    
    public int getFieldCount() {
        return fields.size();
    }

    
    
    private List<ADIFField> fields = new ArrayList<>();
    
    public void add(ADIFField field) {
        fields.add(field);
    }
    
    public void write(Writer out) throws IOException {
        for (ADIFField field : fields) {
            field.write(out);
        }
        out.write(END_OF_RECORD);
    }
}

package no.la1k.adif;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representation of an ADIF field.
 *
 * An ADIF field is a single part of a logbook entry, like a
 * call sign, date, time, etc.
 *
 * A field consists of a mandatory field name, the length of the
 * field value, and optionally a field data typer identifier,
 * separated by a colon character, and enclosed in '<' and '>'.
 * It is followed by the (optional) field value.
 *   Example: "<call:6:s>LA1BFA "
 *
 * Note that the data type is usually omitted for "well-known"
 * fields (i.e those specified in the ADIF specification).
 */
public class ADIFField {

    public static ADIFField create(String name) {
        return new ADIFField (name, null, null);
    }

    public static ADIFField create(String name, String value) {
        return new ADIFField (name, null, value);
    }
    
    public static ADIFField create(String name, String type, String value) {
        return new ADIFField (name, type, value);
    }

    public static ADIFField read(Reader in) throws ADIFException {
        return parse(in);
    }

    private String recordName;
    private String type;
    private String value;
    
    public ADIFField (String name, String type, String value) {
        this.recordName = name;
        this.type = type;
        this.value = value;
    }

    public void write (Writer out) throws IOException {
        final StringBuilder sb = new StringBuilder();
        final int length = value.length();

        sb.append('<')
          .append(recordName)
          .append(':')
          .append(length);

        if (type != null) {
            sb.append(':').append(type);
        }
        sb.append(">");
        if (value != null) {
            sb.append(value);
        }
        sb.append(' ');
        out.write(sb.toString());
    }

    public String getName() {
        return recordName;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    private static ADIFField parse(Reader buf) throws ADIFException {
        StringBuilder name = new StringBuilder();
        StringBuilder length = new StringBuilder();
        StringBuilder type = new StringBuilder();
        StringBuilder value = new StringBuilder();
        
        int state = 0;
        int fieldlen = 0;
        
        /*
         * FSM parser.
         *  state = 0 : 
         *      look for start-of-record '<'.  Only whitespace allowed
         *      goes to state = 1 when start-of-record found
         *  state = 1 :
         *      get the field name 
         *      a colon goes to state=2
         *      a end-of-record ('>') returns the record
         * state = 2 :
         *      get the field value length.  Only digits allowed
         *      a colon goes to state = 3
         *      a end-of-record goes to state = 4
         * state = 3 :
         *      get the field  type
         *      alnum characters allowed
         *      end-of-record goes to state = 4
         * state = 4 :
         *      get the field value
         */
        try {
            while (true) {
                int c;
                switch (state) {
                    case 0 :
                        c = buf.read();
                        if (c == -1)
                            return null; // null record
                        if (Character.isWhitespace((char) c))
                            break;
                        if (c == '<') {
                            state = 1;
                            break;
                        }
                        throw new ADIFException ("Invalid character '" 
                            + (char) c 
                            + "'. Excepted '<'."); 
                    case 1:
                        c = buf.read();
                        if (c == ':') {
                            state = 2;
                        } else if (c == '>') {
                            state = 4;
                        } else if (c == '_' || Character.isLetterOrDigit((char) c)) {
                            name.append((char) c);
                        } else if (c == -1) {
                            throw new ADIFException("Unexpected end-of-file encountered while reading ADIF record.");
                        } else {
                            throw new ADIFException("Invalid character '" + (char) c + "'.");
                        }
                        break;
                    case 2:
                        c = buf.read();
                        if (c == ':') {
                            state = 3;
                        } else if (c == '>') {
                            state = 4;
                        } else if (Character.isDigit((char) c)) {
                            length.append((char) c);
                        } else if (c == -1) {
                            throw new ADIFException("Unexpected end-of-file encountered while reading ADIF record.");
                        } else {
                            throw new ADIFException("Invalid character '" + (char) c + "'.");
                        }
                        break;
                    case 3:
                        c = buf.read();
                        if (c == '>') {
                            state = 4;
                        } else if (Character.isLetterOrDigit((char) c)) {
                            type.append((char) c);
                        } else {
                            throw new ADIFException("Invalid character '" + (char) c + "'.");
                        }
                        break;
                    case 4:
                        state = 5;
                        if (length.length() > 0) {
                            try {
                                fieldlen = Integer.parseInt(length.toString());
                            } catch (NumberFormatException e) {
                                throw new ADIFException("Can't parse field length '" + length + "'.", e);
                            }
                        }
                        
                    case 5:
                        --fieldlen;
                        if (fieldlen >= 0) {
                            c = buf.read();
                            if (c == -1) {
                                throw new ADIFException("Unexpected end-of-file encountered while reading ADIF record.");
                            }
                            value.append((char) c);
                        } else {
                            return create(name.toString(), type.toString(), value.toString());
                        }
                        break;
                }
            }
        } catch (IOException e) {
            throw new ADIFException ("Malformed field.", e);
        }            
    }
    
}

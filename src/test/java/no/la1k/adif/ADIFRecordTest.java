package no.la1k.adif;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

/**
 *
 *
 */
public class ADIFRecordTest {
    @Test
    public void simpleRecordBuilderTest () throws IOException {
        ADIFRecord record = new ADIFRecord();
        record.add(ADIFField.create("call", "la1bfa"));
        record.add(ADIFField.create("date", "20131212"));
        record.add(ADIFField.create("sent_rst", "599"));
        record.add(ADIFField.create("rcvd_rst", "559"));
        
        StringWriter out = new StringWriter();
        record.write(out);
        final String masterString = "<call:6>la1bfa <date:8>20131212 <sent_rst:3>599 <rcvd_rst:3>559 <eor>\n";
        assertEquals(out.toString(), masterString);
    }
    
    @Test
    public void recordReaderTest () throws ADIFException {
        final String rec1 = "<call:6>WN4AZY<band:3>20M<mode:4>RTTY<qso_date:8>19960513<time_on:4>1305<eor>";
        ADIFRecord record = ADIFRecord.read(new StringReader(rec1));
        assertEquals("WN4AZY", record.getField("call").getValue());
        assertNotEquals("LA1K", record.getField("call").getValue());
        assertEquals("19960513", record.getField("qso_date").getValue());
        assertEquals("1305", record.getField("time_on").getValue());

        final String rec2 = "<call:6>WN4AZY <band:3>20M <mode:4>RTTY <qso_date:8>19960513 <time_on:4>1305<eor>";
        record = ADIFRecord.read(new StringReader(rec2));
        assertEquals("WN4AZY", record.getField("call").getValue());
        assertNotEquals("LA1K", record.getField("call").getValue());
        assertEquals("19960513", record.getField("qso_date").getValue());
        assertEquals("1305", record.getField("time_on").getValue());
        
    }
}

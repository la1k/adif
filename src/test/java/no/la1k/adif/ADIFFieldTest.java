package no.la1k.adif;

import java.io.IOException;
import java.io.StringReader;
import org.junit.Test;
import static org.junit.Assert.fail;



/**
 *
 */
public class ADIFFieldTest {

    @Test
    public void basicFieldReaderTest() throws ADIFException {
        // test that no exceptions are thrown here
        read("<eor>");
        read("  <eor>   ");
        read("<call:6>la1bfa");
        read("   <call:6>la1bfa   ");
        read("<call:6:s>la1bfa");
        read("<call:6:s>la1bfa    ");
        // verify that these throw exceptions
        //malread("<>");
        malread("<eor");
        //malread("<call::>");
    }
    
    private ADIFField read(String s) throws ADIFException {
        StringReader r = new StringReader(s);
        ADIFField f = ADIFField.read(r);
        return f;
    }
    
    private void malread(String s){
        try {
            StringReader r = new StringReader(s);
            ADIFField f = ADIFField.read(r);
            fail("No Exception thrown");
        } catch (ADIFException e) {
            // Success
        }
    }

    
}

package nl.geozet.openls;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import nl.geozet.common.StringConstants;
import nl.geozet.openls.client.OpenLSClient;
import nl.geozet.openls.databinding.openls.GeocodeRequest;
import nl.geozet.openls.databinding.openls.GeocodeResponse;
import nl.geozet.openls.parser.OpenLSRequestParser;

import org.junit.Before;
import org.junit.Test;

/**
 * Testcases voor {@link OpenLSClient}.
 * 
 * @author strampel@atlis.nl
 * @author mprins
 */
public class OpenLSClientIntegrationTest {
    /** test subject. */
    private OpenLSClient openLSClient = null;

    @Before
    public void setUp() throws Exception {
        this.openLSClient = new OpenLSClient();
    }

    /**
     * Test open ls get. test methode voor
     * {@link OpenLSClient#doGetOpenLSRequest(String, Map)}
     */
    @Test
    public void testDoGetOpenLSRequest() {
        final Map<String, String> openLSParams = new TreeMap<String, String>();
        // final String url = "http://geoserver.nl/geocoder/NLaddress.aspx";
        final String url = "http://geodata.nationaalgeoregister.nl/geocoder/Geocoder";
        openLSParams.put(StringConstants.OPENLS_REQ_PARAM_SEARCH.code,
                "hengelo");
        // openLSParams.put("UID", "put your key here");
        openLSParams.put(StringConstants.OPENLS_REQ_PARAM_REQUEST.code,
                StringConstants.OPENLS_REQ_VALUE_GEOCODE.code);
        // openLSParams.put("search", "hengelo");
        final GeocodeResponse gcr = this.openLSClient.doGetOpenLSRequest(url,
                openLSParams);
        assertNotNull(gcr);
        assertTrue(gcr.getGeocodeResponseListSize() > 0);
    }

    /**
     * Test open ls post. Test methode voor
     * {@link OpenLSClient#doPostOpenLSRequest(String, GeocodeRequest)}
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    public void testDoPostOpenLSRequest() throws java.io.IOException {
        final String requestString = this
                .readFileAsString("/samplerequests/samplerequest.xml");
        final OpenLSRequestParser rp = new OpenLSRequestParser();
        final GeocodeRequest gcreq = rp.parseOpenLSRequest(requestString);
        final GeocodeResponse gcr = this.openLSClient.doPostOpenLSRequest(
                // "http://geoserver.nl/geocoder/NLaddress.aspx?UID=<put your key here>",
                "http://geodata.nationaalgeoregister.nl/geocoder/Geocoder",
                gcreq);
        assertNotNull(gcr);
        assertTrue(gcr.getGeocodeResponseListSize() == 0);
    }

    /**
     * Test open ls free form post such as openrouteservice.org. Test methode
     * voor {@link OpenLSClient#doPostOpenLSRequest(String, Map) }
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    public void testDoPostOpenLSRequestFreeForm() throws java.io.IOException {
        final String url = "http://www.openrouteservice.org/php/OpenLSLUS_Geocode.php";
        final Map<String, String> openLSParams = new TreeMap<String, String>();
        openLSParams.put(StringConstants.OPENLS_REQ_PARAM_REQUEST.code,
                StringConstants.OPENLS_REQ_VALUE_GEOCODE.code);
        openLSParams.put("FreeFormAdress", "hengelo");
        openLSParams.put("MaxResponse", "3");
        final GeocodeResponse gcr = this.openLSClient.doPostOpenLSRequest(url,
                openLSParams);
        assertNotNull(gcr);
        assertTrue(gcr.getGeocodeResponseListSize() > 0);
    }

    /**
     * Read file as string.
     * 
     * @param filePath
     *            the file path
     * @return inhoude van de file als string
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private String readFileAsString(String filePath) throws java.io.IOException {
        final URL url = this.getClass().getResource(filePath);
        final File file = new File(url.getFile());
        final byte[] buffer = new byte[(int) file.length()];
        final BufferedInputStream f = new BufferedInputStream(
                new FileInputStream(file));
        f.read(buffer);
        return new String(buffer);
    }

}

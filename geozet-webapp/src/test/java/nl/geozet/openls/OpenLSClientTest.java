package nl.geozet.openls;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;
import nl.geozet.common.StringConstants;
import nl.geozet.openls.client.OpenLSClient;
import nl.geozet.openls.databinding.openls.GeocodeRequest;
import nl.geozet.openls.databinding.openls.GeocodeResponse;
import nl.geozet.openls.parser.OpenLSRequestParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Testcases voor {@link OpenLSClient}.
 * 
 * @author strampel@atlis.nl
 */
public class OpenLSClientTest extends TestCase {

    private OpenLSClient openLSClient = null;

    @Override
    @Before
    protected void setUp() throws Exception {
        this.openLSClient = new OpenLSClient();
    }

    @Override
    @After
    protected void tearDown() throws Exception {
        this.openLSClient = null;
    }

    /**
     * Test open ls get. test methode voor
     * {@link OpenLSClient#doGetOpenLSRequest(String, Map)}
     */
    @Test
    public void testDoGetOpenLSRequest() {
        final String url = "http://geoserver.nl/geocoder/NLaddress.aspx";
        // final String url = "http://10.10.2.52:8080/GeocoderService/Geocoder";
        final Map<String, String> openLSParams = new TreeMap<String, String>();
        openLSParams.put("UID", "put your key here");
        openLSParams.put(StringConstants.OPENLS_REQ_PARAM_REQUEST.code,
                StringConstants.OPENLS_REQ_VALUE_GEOCODE.code);
        // openLSParams.put(StringConstants.OPENLS_REQ_PARAM_SEARCH.code,
        // "hengelo");
        openLSParams.put("search", "hengelo");
        final GeocodeResponse gcr = this.openLSClient.doGetOpenLSRequest(url,
                openLSParams);
        assertNotNull(gcr);
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
        final GeocodeResponse gcr = this.openLSClient
                .doPostOpenLSRequest(
                        "http://geoserver.nl/geocoder/NLaddress.aspx?UID=<put your key here>",
                        gcreq);
        assertNotNull(gcr);
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

package nl.geozet.openls.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * TastCase voor {@link OpenLSClientAddress}.
 * 
 * @author prinsmc@nminlnv.nl
 */
public class OpenLSClientAddressTest {

    /** The invalid adres. */
    private OpenLSClientAddress invalidAdres;

    /** The valid adres. */
    private OpenLSClientAddress validAdres;
    /** The valid adres. */
    private OpenLSClientAddress provincieAdres;
    /** The valid adres. */
    private OpenLSClientAddress gemeenteAdres;

    /** The Constant X. */
    private static final String X = "219173";

    /** The Constant Y. */
    private static final String Y = "451862";

    /** The Constant PLAATS. */
    private static final String PLAATS = "hengelo gld";

    /** The Constant GEM. */
    private static final String GEM = "bronckhorst";

    /** The Constant PROV. */
    private static final String PROV = "gelderland";

    /** The Constant STRAAT. */
    private static final String STRAAT = "een straat";

    /**
     * Sets the up.
     * 
     * @throws Exception
     *             the exception
     */
    @Before
    public void setUp() throws Exception {
        this.validAdres = new OpenLSClientAddress();
        this.validAdres.setCountrySubdivision(PROV);
        this.validAdres.setMunicipality(GEM);
        this.validAdres.setMunicipalitySubdivision(PLAATS);
        this.validAdres.setStreetName(STRAAT);
        this.validAdres.setxCoord(X);
        this.validAdres.setyCoord(Y);

        this.invalidAdres = new OpenLSClientAddress();

        this.provincieAdres = new OpenLSClientAddress();
        this.provincieAdres.setCountrySubdivision(PROV);
        this.provincieAdres.setxCoord(X);
        this.provincieAdres.setyCoord(Y);

        this.gemeenteAdres = new OpenLSClientAddress();
        this.gemeenteAdres.setMunicipality(GEM);
        this.gemeenteAdres.setxCoord(X);
        this.gemeenteAdres.setyCoord(Y);
    }

    /**
     * Tear down.
     * 
     * @throws Exception
     *             the exception
     */
    @After
    public void tearDown() throws Exception {
        this.invalidAdres = null;
        this.validAdres = null;
    }

    /**
     * Test voor {@link OpenLSClientAddress#getAddressString()}.
     */
    @Test
    public void testGetAddressString() {
        assertEquals("toString en getAddressString geven niet dezelfde output",
                this.validAdres.toString(), this.validAdres.getAddressString());
        // plaats
        String expected = STRAAT + " - " + PLAATS
                + OpenLSClientAddress.APPEND_PLAATS;
        assertEquals(expected, this.validAdres.getAddressString());

        // gemeente
        expected = GEM + OpenLSClientAddress.APPEND_GEMEENTE;
        assertEquals(expected, this.gemeenteAdres.getAddressString());

        // provincie
        expected = PROV + OpenLSClientAddress.APPEND_PROVINCIE;
        assertEquals(expected, this.provincieAdres.getAddressString());
    }

    /**
     * Test voor {@link OpenLSClientAddress#isValidClientAddress()}.
     */
    @Test
    public void testIsValidClientAddress() {
        assertTrue(this.validAdres.isValidClientAddress());
        assertFalse(this.invalidAdres.isValidClientAddress());

    }

    // @Test
    // public void testGetxCoord() {
    // }
    //
    // @Test
    // public void testGetyCoord() {
    // }
    //
    // @Test
    // public void testGetPostalCode() {
    // }
    //
    // @Test
    // public void testGetStreetName() {
    // }
    //
    // @Test
    // public void testGetStreetNumber() {
    // }
    //
    // @Test
    // public void testGetCountrySubdivision() {
    // }
    //
    // @Test
    // public void testGetMunicipality() {
    // }
    //
    // @Test
    // public void testGetMunicipalitySubdivision() {
    // }
    //
    // @Test
    // public void testSetxCoord() {
    // }
    //
    // @Test
    // public void testSetyCoord() {
    // }
    //
    // @Test
    // public void testSetPostalCode() {
    // }
    //
    // @Test
    // public void testSetStreetName() {
    // }
    //
    // @Test
    // public void testSetStreetNumber() {
    // }
    //
    // @Test
    // public void testSetCountrySubdivision() {
    // }
    //
    // @Test
    // public void testSetMunicipality() {
    // }
    //
    // @Test
    // public void testSetMunicipalitySubdivision() {
    // }

    /**
     * Test voor {@link OpenLSClientAddress#OpenLSClientAddress()} constructor.
     */
    @Test
    public void testOpenLSClientAddress() {
        final OpenLSClientAddress cli = new OpenLSClientAddress();
        assertFalse(cli.getAddressString().length() > 0);
        assertFalse(cli.isValidClientAddress());
    }

    /**
     * Test voor
     * {@link OpenLSClientAddress#OpenLSClientAddress(String, String, String, String, String, String, String, String)}
     */
    @Test
    public void testOpenLSClientAddressStringStringStringStringStringStringStringString() {
        final OpenLSClientAddress cli = new OpenLSClientAddress(X, Y, "",
                STRAAT, "", PROV, GEM, PLAATS);
        assertTrue(cli.getAddressString().length() > 0);
        assertTrue(cli.isValidClientAddress());

    }

    /**
     * Test voor {@link OpenLSClientAddress#toString()}.
     */
    @Test
    public void testToString() {
        assertEquals("toString en getAddressString geven niet dezelfde output",
                this.validAdres.getAddressString(), this.validAdres.toString());

    }

}

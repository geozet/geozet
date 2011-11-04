package nl.geozet.openls.client;

import org.apache.log4j.Logger;

/**
 * de Class OpenLSClientAddress is een "DAO" achtige voor een OpenLS adres.
 * 
 * @author strampel@atlis.nl
 * @author prinsmc@minlnv.nl
 */
public class OpenLSClientAddress {
    /**
     * de string plaats tussen haakjes. * {@value}
     */
    public static final String APPEND_PLAATS = " (plaats)";

    /**
     * de string provincie tussen haakjes. * {@value}
     */
    public static final String APPEND_PROVINCIE = " (provincie)";

    /**
     * de string gemeente tussen haakjes. * {@value}
     */
    public static final String APPEND_GEMEENTE = " (gemeente)";

    /** onze LOGGER. */
    private static final Logger LOGGER = Logger
            .getLogger(OpenLSClientAddress.class);
    /** x coordinaat voor dit adres. */
    private String xCoord = "";
    /** y coordinaat voor dit adres. */
    private String yCoord = "";
    /** postcode voor dit adres. */
    private String postalCode = "";
    /** straat voor dit adres. */
    private String streetName = "";
    /** huisnummer voor dit adres. */
    private String streetNumber = "";
    /** "provincie" voor dit adres. */
    private String countrySubdivision = "";
    /** "gemeente" voor dit adres. */
    private String municipality = "";
    /** "plaatsnaam" voor dit adres. */
    private String municipalitySubdivision = "";

    /**
     * Instantiates a new open ls client address.
     */
    public OpenLSClientAddress() {
    }

    /**
     * Instantiates a new open ls client address.
     * 
     * @param xCoord
     *            de x coord
     * @param yCoord
     *            de y coord
     * @param postalCode
     *            de postcode
     * @param streetName
     *            de straatnaam
     * @param streetNumber
     *            het huisnummer
     * @param countrySubdivision
     *            de provincie
     * @param municipality
     *            de gemeente
     * @param municipalitySubdivision
     *            de plaatsnaam
     */
    public OpenLSClientAddress(String xCoord, String yCoord, String postalCode,
            String streetName, String streetNumber, String countrySubdivision,
            String municipality, String municipalitySubdivision) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.postalCode = postalCode;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.countrySubdivision = countrySubdivision;
        this.municipality = municipality;
        this.municipalitySubdivision = municipalitySubdivision;
    }

    /**
     * geeft het adres als string in de vorm straat - postcode - stad - gemeente
     * - provincie.
     * 
     * @return de address string
     * @see #toString()
     * @todo StringBuilder gebruiken; eerst unit test (af)maken
     */
    public String getAddressString() {
        String result = "";
        result += this.streetName;
        String spacer = result.equals("") ? "" : " - ";

        result += result.equals("") ? ""
                : (!this.streetNumber.equals("") ? spacer + this.streetNumber
                        : "");

        result += !this.postalCode.equals("") ? spacer + this.postalCode : "";

        spacer = result.equals("") ? "" : " - ";
        result += !this.municipalitySubdivision.equals("") ? spacer
                + this.municipalitySubdivision + APPEND_PLAATS : "";

        spacer = result.equals("") ? "" : " - ";
        if (this.municipalitySubdivision.equals("")) {
            result += !this.municipality.equals("") ? spacer
                    + this.municipality + APPEND_GEMEENTE : "";
        }

        spacer = result.equals("") ? "" : " - ";
        if (this.municipality.equals("")) {
            result += !this.countrySubdivision.equals("") ? spacer
                    + this.countrySubdivision + APPEND_PROVINCIE : "";
        }

        LOGGER.debug(result);
        return result;
    }

    /**
     * Gets de "provincie" voor dit adres.
     * 
     * @return de "provincie" voor dit adres
     */
    public String getCountrySubdivision() {
        return this.countrySubdivision;
    }

    /**
     * Gets de "gemeente" voor dit adres.
     * 
     * @return de "gemeente" voor dit adres
     */
    public String getMunicipality() {
        return this.municipality;
    }

    /**
     * Gets de "plaatsnaam" voor dit adres.
     * 
     * @return de "plaatsnaam" voor dit adres
     */
    public String getMunicipalitySubdivision() {
        return this.municipalitySubdivision;
    }

    /**
     * de postcode voor dit adres.
     * 
     * @return de postcode voor dit adres
     */
    public String getPostalCode() {
        return this.postalCode;
    }

    /**
     * Gets de straat voor dit adres.
     * 
     * @return de straat voor dit adres
     */
    public String getStreetName() {
        return this.streetName;
    }

    /**
     * Gets de huisnummer voor dit adres.
     * 
     * @return de huisnummer voor dit adres
     */
    public String getStreetNumber() {
        return this.streetNumber;
    }

    /**
     * Gets de x coord.
     * 
     * @return de x coord
     */
    public String getxCoord() {
        return this.xCoord;
    }

    /**
     * Gets de y coord.
     * 
     * @return de y coord
     */
    public String getyCoord() {
        return this.yCoord;
    }

    /**
     * controleert of dit een geldig adres is.
     * 
     * @return true, if is valid client address
     */
    public boolean isValidClientAddress() {
        if (this.xCoord.equals("")) { return false; }
        if (this.yCoord.equals("")) { return false; }
        if (this.postalCode.equals("") && this.streetName.equals("")
                && this.countrySubdivision.equals("")
                && this.municipality.equals("")
                && this.municipalitySubdivision.equals("")) { return false; }
        return true;
    }

    /**
     * Sets de "provincie" voor dit adres.
     * 
     * @param countrySubdivision
     *            de new "provincie" voor dit adres
     */
    public void setCountrySubdivision(String countrySubdivision) {
        this.countrySubdivision = countrySubdivision;
    }

    /**
     * Sets de "gemeente" voor dit adres.
     * 
     * @param municipality
     *            de new "gemeente" voor dit adres
     */
    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    /**
     * Sets de "plaatsnaam" voor dit adres.
     * 
     * @param municipalitySubdivision
     *            de new "plaatsnaam" voor dit adres
     */
    public void setMunicipalitySubdivision(String municipalitySubdivision) {
        this.municipalitySubdivision = municipalitySubdivision;
    }

    /**
     * Sets de postcode voor dit adres.
     * 
     * @param postalCode
     *            de new postcode voor dit adres
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Sets de straat voor dit adres.
     * 
     * @param streetName
     *            de new straat voor dit adres
     */
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    /**
     * Sets de huisnummer voor dit adres.
     * 
     * @param streetNumber
     *            de new huisnummer voor dit adres
     */
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     * Sets de x coord.
     * 
     * @param xcoord
     *            de new x coord
     */
    public void setxCoord(String xcoord) {
        this.xCoord = xcoord;
    }

    /**
     * Sets de y coord.
     * 
     * @param ycoord
     *            de new y coord
     */
    public void setyCoord(String ycoord) {
        this.yCoord = ycoord;
    }

    /**
     * geeft het adres als string.
     * 
     * @return de string
     * @see #getAddressString()
     */
    @Override
    public String toString() {
        return this.getAddressString();
    }
}

package nl.geozet.common;

/**
 * Numerieke constanten constanten voor de GEOZET viewer applicatie. Analoog aan
 * {@code Geozet.config.zoomScale}.
 * 
 * @see StringConstants
 * @author prinsmc@minlnv.nl
 * @since 1.5
 * @note bevat de constanten en default waarden voor de applicatie
 */
public enum NumberConstants {
    /** straal voor een adres zoekresultaat, waarde in meter. */
    OPENLS_ZOOMSCALE_ADRES(150),
    /** straal voor een postcode zoekresultaat, waarde in meter. */
    OPENLS_ZOOMSCALE_POSTCODE(500),
    /** straal voor een plaats zoekresultaat, waarde in meter. */
    OPENLS_ZOOMSCALE_PLAATS(3000),
    /** straal voor een gemeente zoekresultaat, waarde in meter. */
    OPENLS_ZOOMSCALE_GEMEENTE(10000),
    /** straal voor een provincie zoekresultaat, waarde in meter. */
    OPENLS_ZOOMSCALE_PROVINCIE(50000),
    /** straal voor een default zoekresultaat, waarde in meter. */
    OPENLS_ZOOMSCALE_STANDAARD(1500),
    /**
     * default relevante oppervlakte factor.
     * 
     * @see StringConstants#SERVLETCONFIG_WFS_RELEVANTIE_OPPERVLAKTE_FACTOR
     */
    DEFAULT_VLAKGERICHT_RELEVANTIE_OPPERVLAKTE_FACTOR(0.025),
    /** default aantal features voor een WFS request. */
    DEFAULT_MAX_FEATURES(500),
    /** default aantal items per pagina, 10. */
    DEFAULT_ITEMS_PER_PAGINA(10);

    /** de waarde van dit object. */
    private final Number number;

    /**
     * constructor.
     * 
     * @param number
     *            the number
     */
    NumberConstants(Number number) {
        this.number = number;
    }

    /**
     * String value van dit object.
     * 
     * @return de waarde van dit object als string
     * @see java.lang.Enum#toString()
     * @see java.lang.Number#toString()
     */
    @Override
    public String toString() {
        return this.number.toString();
    }

    /**
     * Int value van dit object.
     * 
     * @return de waarde van dit object als integer
     * @see java.lang.Number#intValue()
     */
    public int intValue() {
        return this.number.intValue();
    }

    /**
     * Double value van dit object.
     * 
     * @return de waarde van dit object als double
     * @see java.lang.Number#doubleValue()
     */
    public double doubleValue() {
        return this.number.doubleValue();
    }
}

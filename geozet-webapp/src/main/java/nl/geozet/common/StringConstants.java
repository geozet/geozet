package nl.geozet.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * De enum StringConstants bevat alle string constanten voor de GEOZET viewer
 * applicatie.
 * 
 * @author prinsmc@minlnv.nl
 * @author strampel@atlis.nl
 * @since 1.5
 * @note bevat de constanten en default waarden voor de applicatie
 */
public enum StringConstants {

    /** request param naam voor straal. */
    REQ_PARAM_STRAAL("straal"),
    /** request param naam voor xcoord. */
    REQ_PARAM_XCOORD("xcoord"),
    /** request param naam voor ycoord. */
    REQ_PARAM_YCOORD("ycoord"),
    /** request param naam voor filter. */
    REQ_PARAM_FILTER("filter"),
    /** request param naam voor adres. */
    REQ_PARAM_ADRES("adres"),
    /** request param naam voor gevonden adres. */
    REQ_PARAM_GEVONDEN("gevonden"),
    /** request param naam voor pagina offset. */
    REQ_PARAM_PAGEOFFSET("offset"),
    /** request param naam voor feature ID. */
    REQ_PARAM_FID("fid"),
    /**
     * request param naam voor coreonly optie. De enige waarde waar naar wordt
     * gekeken is "true".
     */
    REQ_PARAM_COREONLY("coreonly"),
    /**
     * request param naam voor filter optie. De enige waarde waar naar wordt
     * gekeken is "true".
     */
    REQ_PARAM_EXPLICITUSEFILTER("usefilter"),
    /** request param naam voor bounding box. */
    REQ_PARAM_BBOX("bbox"),
    /** request param naam voor response format. */
    REQ_PARAM_RESPONSE_FORMAT("format"),

    /** openls request param naam voor request. */
    OPENLS_REQ_PARAM_REQUEST("Request"),
    /** openls request param naam voor request value. */
    OPENLS_REQ_VALUE_GEOCODE("geocode"),
    /** openls request param naam voor search. */
    OPENLS_REQ_PARAM_SEARCH("zoekterm"),
    /**
     * naam van de servlet config param voor de WFS timeout,.
     */
    SERVLETCONFIG_WFS_TIMEOUT("wfstimout"),
    /**
     * naam van de servlet config param voor het maximum aantal op te vragen
     * features per request.
     */
    SERVLETCONFIG_WFS_MAXFEATURES("wfsmaxfeatures"),
    /**
     * naam van de servlet config param voor de WFS capabilities url, niet
     * configureerbaar.
     */
    CONFIG_PARAM_WFS_CAPABILITIES_URL("wfscapabilitiesurl"),
    /**
     * naam van de servlet config param voor de OpenLS server url, niet
     * configureerbaar.
     */
    SERVLETCONFIG_OPENLS_SERVER_URL("openlsserverurl"),
    /**
     * naam van de servlet config param voor de relevantie oppervlakte, niet
     * configureerbaar.
     * 
     * @see NumberConstants#DEFAULT_VLAKGERICHT_RELEVANTIE_OPPERVLAKTE_FACTOR
     */
    SERVLETCONFIG_WFS_RELEVANTIE_OPPERVLAKTE_FACTOR("relevantiefactor"),
    /**
     * centrum AOI.
     */
    SERVLETCONFIG_WFS_OPPERVLAKTE_FACTOR_CENTRUM("centrum_gebied"),
    /**
     * midden AOI.
     */
    SERVLETCONFIG_WFS_OPPERVLAKTE_FACTOR_MIDDEN("midden_gebied"),
    /**
     * rand AOI.
     */
    SERVLETCONFIG_WFS_OPPERVLAKTE_FACTOR_RAND("rand_gebied"),

    /** naam van de WFS feature klasse. */
    CONFIG_PARAM_WFS_TYPENAME("typename"),
    /** param naam voor items op een pagina. */
    CONFIG_PARAM_PAGINALENGTE("paginalengte"),
    /** param naam voor core-resources. */
    CONFIG_PARAM_RESOURCENAME("resourcebundle"),
    /** param naam voor de bekendmakingen/zoek servlet mapping. */
    CONFIG_PARAM_BEKENDMAKINGENSERVLET("bekendmakingen_url-pattern"),
    /** param naam voor de bekendmaking/detail servlet mapping. */
    CONFIG_PARAM_BEKENDMAKINGSERVLET("bekendmaking_url-pattern"),
    /** param naam voor de vlakgericht bekendmakingen servlet mapping. */
    CONFIG_PARAM_VLAKBEKENDMAKINGSERVLET("bekendmakingvlak_url-pattern"),

    /** param naam voor de locatie servlet mapping. */
    CONFIG_PARAM_LOCATIESERVLET("locatie_url-pattern"),
    /** param naam voor de locatie servlet mapping. */
    CONFIG_PARAM_GEOZETSERVLET("geozet_url-pattern"),
    /**
     * Naam van feature attribuut met de catagorie waarde, niet configureerbaar.
     */
    FILTER_CATEGORIE_NAAM("categorie"),

    /** naam van de UserData attribuut voor afstand. */
    AFSTAND_NAAM("afstand"),

    /* begin datamodel mapping */
    /**
     * naam van de feature attribuut voor title.
     * 
     * @see #featureAttribuutNamen
     */
    FEATURE_ATTR_NAAM_TITEL("titel"),
    /**
     * naam van de feature attribuut voor beschrijving.
     * 
     * @see #featureAttribuutNamen
     */
    FEATURE_ATTR_NAAM_DESCRIPTION("beschrijving"),
    /**
     * naam van de feature attribuut voor onderwerp.
     * 
     * @see #featureAttribuutNamen
     */
    FEATURE_ATTR_NAAM_ONDERWERP("onderwerp"),
    /**
     * naam van de feature attribuut voor url.
     * 
     * @see #featureAttribuutNamen
     */
    FEATURE_ATTR_NAAM_URL("url"),
    /**
     * naam van de feature attribuut voor plaats.
     * 
     * @see #featureAttribuutNamen
     */
    FEATURE_ATTR_NAAM_PLAATS("plaats"),
    /**
     * naam van de feature attribuut voor straat.
     * 
     * @see #featureAttribuutNamen
     */
    FEATURE_ATTR_NAAM_STRAAT("straat"),
    /**
     * naam van de feature attribuut voor datum.
     * 
     * @see #featureAttribuutNamen
     */
    FEATURE_ATTR_NAAM_DATUM("datum"),
    /**
     * naam van de feature attribuut voor overheid.
     * 
     * @see #featureAttribuutNamen
     */
    FEATURE_ATTR_NAAM_OVERHEID("overheid"),
    /**
     * naam van de feature attribuut voor thema.
     * 
     * @see #featureAttribuutNamen
     */
    FEATURE_ATTR_NAAM_CATEGORIE("categorie"),
    /**
     * naam van de feature attribuut voor postcodehuisnummer.
     * 
     * @see #featureAttribuutNamen
     */
    FEATURE_ATTR_NAAM_POSTCODEHUISNUMMER("postcodehuisnummer"),

    /**
     * naam van de feature attribuut voor oppervlakte van vlakgerichte
     * bekendmakingen.
     */
    FEATURE_ATTR_NAAM_VLAKGERICHT_OPPERVLAKTE("oppervlak"),

    /**
     * Aansluit spcificaties url key voor provincie.
     * 
     * @see #urlKeys()
     */
    URL_KEY_PROVINCIE("provincie"),
    /**
     * Aansluit spcificaties url key voor gemeente.
     * 
     * @see #urlKeys()
     */
    URL_KEY_GEMEENTE("gemeente"),
    /**
     * Aansluit spcificaties url key voor plaats.
     * 
     * @see #urlKeys()
     */
    URL_KEY_PLAATS("plaats"),
    /**
     * Aansluit spcificaties url key voor wijk.
     * 
     * @see #urlKeys()
     */
    URL_KEY_WIJK("wijk"),
    /**
     * Aansluit spcificaties url key voor buurt.
     * 
     * @see #urlKeys()
     */
    URL_KEY_BUURT("buurt"),
    /**
     * Aansluit spcificaties url key voor postcode.
     * 
     * @see #urlKeys()
     */
    URL_KEY_POSTCODE("postcode"),
    /**
     * Aansluit spcificaties url key voor straat.
     * 
     * @see #urlKeys()
     */
    URL_KEY_STRAAT("straat"),
    /**
     * Aansluit spcificaties url key voor huisnummer.
     * 
     * @see #urlKeys()
     */
    URL_KEY_HUISNUMMER("huisnummer"),
    /**
     * Aansluit spcificaties url key voor straal.
     * 
     * @see #urlKeys()
     */
    URL_KEY_STRAAL("straal");

    /** De code (waarde) van dit object. */
    public final String code;

    /**
     * Maakt een nieuw object aan met de gegeven code waarde.
     * 
     * @param code
     *            de code voor dit object
     * @see #code
     */
    StringConstants(String code) {
        this.code = code;
    }

    /**
     * Lijst met alle code namen (immutable).
     * 
     * @return De lijst met alle code namen.
     */
    public static List<String> codeNamen() {
        final List<String> codeNames = new ArrayList<String>();
        for (final StringConstants strConst : StringConstants.values()) {
            codeNames.add(strConst.code);
        }
        return Collections.unmodifiableList(codeNames);
    }

    /**
     * Lijst met alleen de feature attribuut namen (immutable).
     * 
     * @return De lijst met feature attribuut namen.
     */
    public static List<String> featureAttribuutNamen() {
        return Collections.unmodifiableList(Arrays.asList(
                FEATURE_ATTR_NAAM_OVERHEID.code, FEATURE_ATTR_NAAM_TITEL.code,
                FEATURE_ATTR_NAAM_ONDERWERP.code, FEATURE_ATTR_NAAM_URL.code,
                FEATURE_ATTR_NAAM_PLAATS.code, FEATURE_ATTR_NAAM_DATUM.code,
                FEATURE_ATTR_NAAM_STRAAT.code,
                FEATURE_ATTR_NAAM_CATEGORIE.code,
                FEATURE_ATTR_NAAM_DESCRIPTION.code,
                FEATURE_ATTR_NAAM_POSTCODEHUISNUMMER.code));
    }

    /**
     * Lijst met alleen de url key namen (immutable) voor de aansluit
     * spcificaties; let op de volgorde; deze is geoptimaliseerd om een zo
     * logisch mogelijke adresindicatie te maken; dus eerste adres, dan
     * postcode, dan plaats enz..
     * 
     * @return De lijst met url key namen.
     */
    public static List<String> urlKeys() {
        return Collections.unmodifiableList(Arrays.asList(URL_KEY_STRAAT.code,
                URL_KEY_HUISNUMMER.code, URL_KEY_POSTCODE.code,
                URL_KEY_BUURT.code, URL_KEY_WIJK.code, URL_KEY_PLAATS.code,
                URL_KEY_GEMEENTE.code, URL_KEY_PROVINCIE.code,
                URL_KEY_STRAAL.code));
    }

    /**
     * Geeft de code van dit object terug. Analoog van {@link #code}
     * 
     * @return de code
     * @see #code
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.code;
    }
}

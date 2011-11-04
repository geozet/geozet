package nl.geozet.openls.servlet;

import static nl.geozet.common.NumberConstants.OPENLS_ZOOMSCALE_GEMEENTE;
import static nl.geozet.common.NumberConstants.OPENLS_ZOOMSCALE_PLAATS;
import static nl.geozet.common.NumberConstants.OPENLS_ZOOMSCALE_PROVINCIE;
import static nl.geozet.common.NumberConstants.OPENLS_ZOOMSCALE_STANDAARD;
import static nl.geozet.common.StringConstants.FILTER_CATEGORIE_NAAM;
import static nl.geozet.common.StringConstants.OPENLS_REQ_PARAM_REQUEST;
import static nl.geozet.common.StringConstants.OPENLS_REQ_PARAM_SEARCH;
import static nl.geozet.common.StringConstants.OPENLS_REQ_VALUE_GEOCODE;
import static nl.geozet.common.StringConstants.REQ_PARAM_ADRES;
import static nl.geozet.common.StringConstants.REQ_PARAM_COREONLY;
import static nl.geozet.common.StringConstants.REQ_PARAM_GEVONDEN;
import static nl.geozet.common.StringConstants.REQ_PARAM_STRAAL;
import static nl.geozet.common.StringConstants.REQ_PARAM_XCOORD;
import static nl.geozet.common.StringConstants.REQ_PARAM_YCOORD;
import static nl.geozet.common.StringConstants.SERVLETCONFIG_OPENLS_SERVER_URL;
import static nl.geozet.common.StringConstants.URL_KEY_BUURT;
import static nl.geozet.common.StringConstants.URL_KEY_GEMEENTE;
import static nl.geozet.common.StringConstants.URL_KEY_HUISNUMMER;
import static nl.geozet.common.StringConstants.URL_KEY_PLAATS;
import static nl.geozet.common.StringConstants.URL_KEY_POSTCODE;
import static nl.geozet.common.StringConstants.URL_KEY_PROVINCIE;
import static nl.geozet.common.StringConstants.URL_KEY_STRAAT;
import static nl.geozet.common.StringConstants.URL_KEY_WIJK;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.geozet.common.DataCategorieen;
import nl.geozet.common.HttpWrappedRequest;
import nl.geozet.common.ServletBase;
import nl.geozet.common.StringConstants;
import nl.geozet.openls.client.OpenLSClient;
import nl.geozet.openls.client.OpenLSClientAddress;
import nl.geozet.openls.client.OpenLSClientUtil;
import nl.geozet.openls.databinding.openls.Address;
import nl.geozet.openls.databinding.openls.Building;
import nl.geozet.openls.databinding.openls.GeocodeRequest;
import nl.geozet.openls.databinding.openls.GeocodeResponse;
import nl.geozet.openls.databinding.openls.OpenLSConstants;
import nl.geozet.openls.databinding.openls.Place;
import nl.geozet.openls.databinding.openls.PostalCode;
import nl.geozet.openls.databinding.openls.Street;
import nl.geozet.openls.databinding.openls.StreetAddress;

import org.apache.log4j.Logger;

/**
 * OpenLSServlet is een OLS client voor de Geozet applicatie. In het geval een
 * zoekactie een locatie oplevert wordt het request gelijk doorgestuurd naar de
 * {@link nl.geozet.wfs.WFSClientServlet WFSClientServlet}
 * 
 * @author strampel@atlis.nl
 * @author prinsmc@minlnv.nl
 * @since 1.6
 * @since Servlet API 2.5
 * @note zoeken en tonen van een locatie
 */
public class OpenLSServlet extends ServletBase {
    /** generated serialVersionUID. */
    private static final long serialVersionUID = -6545660249959378114L;

    /** onze LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(OpenLSServlet.class);

    /** de Open LS server url. */
    private String openLSServerUrl;

    /** De open ls client die het echte werk doet. */
    private transient OpenLSClient openLSClient;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.openLSClient = new OpenLSClient();
        // init params inlezen en controleren
        this.openLSServerUrl = config
                .getInitParameter(SERVLETCONFIG_OPENLS_SERVER_URL.code);
        if (this.openLSServerUrl == null) {
            LOGGER.fatal("config param " + SERVLETCONFIG_OPENLS_SERVER_URL
                    + " is null.");
            throw new ServletException("config param "
                    + SERVLETCONFIG_OPENLS_SERVER_URL + " is null.");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        String zoekAdres = request.getParameter(REQ_PARAM_ADRES.code);
        if ((zoekAdres.length() < 1)
                || (zoekAdres.equalsIgnoreCase(this._RESOURCES
                        .getString("KEY_INTRO_VOORINGEVULD")))) {
            // als er geen tekst is ingevuld (of de tekst is hetzelfde als die
            // in de resource bundle)
            this.renderHTMLResults(request, response, null);
            response.flushBuffer();
            return;
        }
        // opknippen in de bekende stukjes voor "aansluit specs ingang"
        if (zoekAdres.startsWith("/")) {
            // lopende / eraf knippen
            zoekAdres = zoekAdres.substring(1);
        }
        final String params[] = zoekAdres.split("/");
        LOGGER.debug("gevraagde params: " + Arrays.toString(params));

        // hashtable met zoektermen en evt. straal
        String straal = null;
        final HashMap<String, String> paramTable = new HashMap<String, String>();
        if (params.length > 1) {
            for (int i = 0; i < params.length; i = i + 2) {
                paramTable.put(params[i].toLowerCase(), params[i + 1]);
                LOGGER.debug("toevoegen: " + params[i] + ":" + params[i + 1]);
            }
            // proberen straal uit request te halen
            if (paramTable.containsKey(REQ_PARAM_STRAAL.code)) {
                straal = paramTable.remove(REQ_PARAM_STRAAL.code);
            }

            final StringBuilder sb = new StringBuilder();
            for (final String key : StringConstants.urlKeys()) {
                if (paramTable.containsKey(key)) {
                    sb.append(paramTable.get(key)).append(" ");
                }
            }
            zoekAdres = sb.substring(0, sb.length() - 1).toString();
        }

        final Map<String, String> openLSParams = new TreeMap<String, String>();

        openLSParams.put(OPENLS_REQ_PARAM_REQUEST.code,
                OPENLS_REQ_VALUE_GEOCODE.code);

        openLSParams.put(OPENLS_REQ_PARAM_SEARCH.code, zoekAdres);

        final GeocodeResponse gcr;
        if (!paramTable.isEmpty()) {
            // voor als we kunnen POSTen; vooralsnog werkt dat alleen met de
            // complexe urls van de aansluit specs
            gcr = this.openLSClient.doPostOpenLSRequest(this.openLSServerUrl,
                    this.createGeocodeRequest(paramTable));
        } else {
            gcr = this.openLSClient.doGetOpenLSRequest(this.openLSServerUrl,
                    openLSParams);
        }
        final List<OpenLSClientAddress> addrl = OpenLSClientUtil
                .getOpenLSClientAddressList(gcr);

        if (addrl.size() == 1) {
            // er is maar 1 adres match gevonden
            final OpenLSClientAddress addr = addrl.get(0);
            // request parameters voor vervolg opbouwen
            final Map<String, String[]> extraParams = new HashMap<String, String[]>();

            extraParams.put(REQ_PARAM_XCOORD.code,
                    new String[] { this.formatCoord(addr.getxCoord()) });
            extraParams.put(REQ_PARAM_YCOORD.code,
                    new String[] { this.formatCoord(addr.getyCoord()) });
            extraParams
                    .put(REQ_PARAM_STRAAL.code,
                            new String[] { (null == straal) ? OPENLS_ZOOMSCALE_STANDAARD
                                    .toString() : straal });
            extraParams.put(REQ_PARAM_GEVONDEN.code,
                    new String[] { addr.getAddressString() });
            extraParams.put(FILTER_CATEGORIE_NAAM.code,
                    DataCategorieen.keysAsArray());
            // doorgeven aan bekendmakingen servlet voor zoekactie
            final HttpServletRequest wrappedRequest = new HttpWrappedRequest(
                    request, extraParams);
            final RequestDispatcher rd = this.getServletContext()
                    .getRequestDispatcher("/" + this._BEKENDMAKINGEN);
            rd.forward(wrappedRequest, response);
        } else {
            // er zijn 0 of meer dan 1 adressen gevonden
            this.renderHTMLResults(request, response, addrl);
            response.flushBuffer();
        }
    }

    /**
     * zorgt ervoor dat eventuele doubles als integer worden gerenderd. Als het
     * niet lukt wordt de oorspronkelijke waarde teruggeven.
     * 
     * @param coord
     *            een coordinaat waarde
     * @return de waarde in integer formaat
     */
    private String formatCoord(String coord) {
        /** coordinaten formatter. */
        final DecimalFormat fmt = new DecimalFormat("###");
        try {
            // formatting als int
            return "" + (fmt.parse(coord).intValue());
        } catch (final ParseException e) {
            LOGGER.warn("Fout tijden parsen van cooridnaat: " + coord, e);
            return coord;
        } catch (final NullPointerException e) {
            LOGGER.warn("Fout tijden parsen van cooridnaat: " + coord, e);
            return coord;
        }
    }

    /**
     * maakt een geocode request om te posten naar de service.
     * 
     * @param data
     *            Map met zoekdata uit de url
     * @return zoekobject
     */
    private GeocodeRequest createGeocodeRequest(Map<String, String> data) {
        final GeocodeRequest gcr = new GeocodeRequest();
        final Address adres = new Address();
        if (data.containsKey(URL_KEY_PROVINCIE.code)) {
            final Place p = new Place();
            p.setPlace(data.get(URL_KEY_PROVINCIE.code));
            p.setType(OpenLSConstants.PLACE_TYPE_COUNTRYSUBDIVISION);
            adres.addPlace(p);
        }
        if (data.containsKey(URL_KEY_GEMEENTE.code)) {
            final Place p = new Place();
            p.setPlace(data.get(URL_KEY_GEMEENTE.code));
            p.setType(OpenLSConstants.PLACE_TYPE_MUNICIPALITY);
            adres.addPlace(p);
        }
        if (data.containsKey(URL_KEY_PLAATS.code)) {
            final Place p = new Place();
            p.setPlace(data.get(URL_KEY_PLAATS.code));
            p.setType(OpenLSConstants.PLACE_TYPE_MUNICIPALITYSUBDIVISION);
            adres.addPlace(p);
        }
        if (data.containsKey(URL_KEY_WIJK.code)) {
            final Place p = new Place();
            p.setPlace(data.get(URL_KEY_WIJK.code));
            p.setType(OpenLSConstants.PLACE_TYPE_MUNICIPALITYSUBDIVISION);
            adres.addPlace(p);
        }
        if (data.containsKey(URL_KEY_BUURT.code)) {
            final Place p = new Place();
            p.setPlace(data.get(URL_KEY_BUURT.code));
            p.setType(OpenLSConstants.PLACE_TYPE_MUNICIPALITYSUBDIVISION);
            adres.addPlace(p);
        }
        if (data.containsKey(URL_KEY_POSTCODE.code)) {
            final PostalCode p = (new PostalCode());
            p.setPostalCode(data.get(URL_KEY_POSTCODE.code));
            adres.setPostalCode(p);
        }
        if (data.containsKey(URL_KEY_STRAAT.code)) {
            final StreetAddress sa = new StreetAddress();
            final Street s = new Street();
            s.setStreet(data.get(URL_KEY_STRAAT.code));
            sa.setStreet(s);
            if (data.containsKey(URL_KEY_HUISNUMMER.code)) {
                final Building b = new Building();
                b.setNumber(data.get(URL_KEY_HUISNUMMER.code));
                sa.setBuilding(b);
            }
            adres.setStreetAddress(sa);
        }
        gcr.addAddress(adres);
        return gcr;
    }

    /**
     * Renderen van de features in html formaat, wordt alleen aangeroepen in het
     * geval de lijst met adressen meer of minder dan 1 object bevat..
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @param addrl
     *            Een lijst met adressen, de lengte van de lijst is (voor deze
     *            applicatie) nul of groter dan een, in het geval null dan is er
     *            geen zoekter ingevuld geweest.
     * @throws IOException
     *             als er een schrijffout optreedt
     * @throws ServletException
     *             the servlet exception
     */
    private void renderHTMLResults(HttpServletRequest request,
            HttpServletResponse response, List<OpenLSClientAddress> addrl)
            throws IOException, ServletException {
        // response headers instellen
        response.setContentType("text/html; charset=UTF-8");

        // header inhaken
        final RequestDispatcher header = this.getServletContext()
                .getRequestDispatcher("/WEB-INF/locatielijst_begin.jsp");
        if (header != null) {
            header.include(request, response);
        }

        final StringBuilder sbResult = new StringBuilder();
        StringBuilder sb;

        if (addrl == null) {
            // niks ingevuld geweest om naar te zoeken
            sbResult.append("<p class=\"geozetError\">")
                    .append(this._RESOURCES.getString("KEY_FOUT_GEEN_INPUT"))
                    .append("</p>\n");
        } else if (addrl.isEmpty()) {
            // niks gevonden
            sbResult.append("<p class=\"geozetError\">")
                    .append(this._RESOURCES
                            .getString("KEY_FOUT_LOCATIE_NIET_GEVONDEN"))
                    .append("</p>\n");
        } else {
            // meer dan 1 gevonden
            sbResult.append("<p class=\"geozetError\">")
                    .append(this._RESOURCES
                            .getString("KEY_FOUT_MEERDERELOCATIES_GEVONDEN"))
                    .append("</p>\n");

            sbResult.append(this._RESOURCES.getString("KEY_FOUT_BEDOELDE_U"));

            sbResult.append("<ul class=\"geozetRecommends\">\n");

            // door lijst adressen heen gaan
            for (final OpenLSClientAddress addr : addrl) {
                // samenstellen URL
                sb = new StringBuilder();
                sb.append(this._BEKENDMAKINGEN).append("?");

                sb.append(REQ_PARAM_XCOORD).append("=")
                        .append(this.formatCoord(addr.getxCoord()))
                        .append("&amp;");
                sb.append(REQ_PARAM_YCOORD).append("=")
                        .append(this.formatCoord(addr.getyCoord()))
                        .append("&amp;");
                // straal
                String straal;
                final String reqstraal = request
                        .getParameter(REQ_PARAM_STRAAL.code);
                LOGGER.debug("request straal is: " + reqstraal);
                if (reqstraal == null) {
                    // als de straal niet opgegeven is proberen op basis van de
                    // OLS hit te bepalen wat de staal zou moeten zijn
                    if (addr.getAddressString().contains(
                            OpenLSClientAddress.APPEND_PLAATS)) {
                        // plaats
                        straal = OPENLS_ZOOMSCALE_PLAATS.toString();
                    } else if (addr.getAddressString().contains(
                            OpenLSClientAddress.APPEND_GEMEENTE)) {
                        // gemeente
                        straal = OPENLS_ZOOMSCALE_GEMEENTE.toString();
                    } else if (addr.getAddressString().contains(
                            OpenLSClientAddress.APPEND_PROVINCIE)) {
                        // provincie
                        straal = OPENLS_ZOOMSCALE_PROVINCIE.toString();
                    } else {
                        // default
                        straal = OPENLS_ZOOMSCALE_STANDAARD.toString();
                    }
                } else {
                    straal = reqstraal;
                }
                sb.append(REQ_PARAM_STRAAL).append("=").append(straal)
                        .append("&amp;");

                // sb.append(REQ_PARAM_ADRES).append("=")
                // .append(addr.getAddressString()).append("&amp;");
                sb.append(REQ_PARAM_ADRES).append("=")
                        .append(request.getParameter(REQ_PARAM_ADRES.code))
                        .append("&amp;");

                sb.append(REQ_PARAM_GEVONDEN).append("=")
                        .append(addr.getAddressString());
                // coreonly alleen als die aanwezig is
                if (null != request.getParameter(REQ_PARAM_COREONLY.code)) {
                    sb.append("&amp;")
                            .append(REQ_PARAM_COREONLY)
                            .append("=")
                            .append(request
                                    .getParameter(REQ_PARAM_COREONLY.code));
                }
                // de URL is uiteindelijke HTML/hyperlink output
                sbResult.append("<li><a href=\"").append(sb.toString())
                        .append("\">");
                sbResult.append(addr.getAddressString()).append("</a></li>\n");
            }
            sbResult.append(" </ul>");
        }
        final PrintWriter out = response.getWriter();
        out.print(sbResult);
        out.flush();
        // footer inhaken
        final RequestDispatcher footer = this.getServletContext()
                .getRequestDispatcher("/WEB-INF/locatielijst_einde.jsp");
        if (footer != null) {
            footer.include(request, response);
        }
    }
}

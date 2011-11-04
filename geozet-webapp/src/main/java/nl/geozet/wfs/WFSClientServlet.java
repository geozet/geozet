package nl.geozet.wfs;

import static nl.geozet.common.NumberConstants.DEFAULT_MAX_FEATURES;
import static nl.geozet.common.NumberConstants.OPENLS_ZOOMSCALE_STANDAARD;
import static nl.geozet.common.StringConstants.AFSTAND_NAAM;
import static nl.geozet.common.StringConstants.CONFIG_PARAM_WFS_CAPABILITIES_URL;
import static nl.geozet.common.StringConstants.CONFIG_PARAM_WFS_TYPENAME;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_CATEGORIE;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_ONDERWERP;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_STRAAT;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_TITEL;
import static nl.geozet.common.StringConstants.FILTER_CATEGORIE_NAAM;
import static nl.geozet.common.StringConstants.REQ_PARAM_EXPLICITUSEFILTER;
import static nl.geozet.common.StringConstants.REQ_PARAM_FID;
import static nl.geozet.common.StringConstants.REQ_PARAM_FILTER;
import static nl.geozet.common.StringConstants.REQ_PARAM_GEVONDEN;
import static nl.geozet.common.StringConstants.REQ_PARAM_PAGEOFFSET;
import static nl.geozet.common.StringConstants.REQ_PARAM_STRAAL;
import static nl.geozet.common.StringConstants.REQ_PARAM_XCOORD;
import static nl.geozet.common.StringConstants.REQ_PARAM_YCOORD;
import static nl.geozet.common.StringConstants.SERVLETCONFIG_WFS_MAXFEATURES;
import static nl.geozet.common.StringConstants.SERVLETCONFIG_WFS_TIMEOUT;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.geozet.common.AfstandComparator;
import nl.geozet.common.ServletBase;

import org.apache.log4j.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.factory.Hints;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * WFSClientServlet. Een WFS client voor de GEOZET Core versie viewer.
 * 
 * @author prinsmc@minlnv.nl
 * @since 1.6
 * @since GeoTools 2.7
 * @since Servlet API 2.5
 * @note zoeken en tonen van punt bekendmakingen
 */
public class WFSClientServlet extends ServletBase {

    /** generated serialVersionUID. */
    private static final long serialVersionUID = -1293974305859874046L;

    /** log4j logger. */
    private static final Logger LOGGER = Logger
            .getLogger(WFSClientServlet.class);

    /**
     * DataStore interface van de WFS.
     */
    private transient DataStore data = null;

    /**
     * connection parameters voor de bekendmakingen WFS.
     */
    private final Map<String, Object> connectionParameters = new HashMap<String, Object>();
    /** Geometry factory. */
    private GeometryFactory geometryFactory;

    /**
     * SimpleFeatureType schema wordt uit de WFS gehaald.
     */
    protected transient SimpleFeatureType schema;

    /**
     * Simple featuresource.
     */
    protected transient SimpleFeatureSource source;

    /** type name. */
    protected String typeName;

    /**
     * (non-Javadoc).
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        double xcoord;
        double ycoord;
        double straal;
        try {
            // request params uitlezen voor het zoeken
            xcoord = Double
                    .valueOf(request.getParameter(REQ_PARAM_XCOORD.code));
            ycoord = Double
                    .valueOf(request.getParameter(REQ_PARAM_YCOORD.code));
            straal = Double
                    .valueOf((null == request
                            .getParameter(REQ_PARAM_STRAAL.code) ? OPENLS_ZOOMSCALE_STANDAARD
                            .toString() : request
                            .getParameter(REQ_PARAM_STRAAL.code)));
        } catch (final NullPointerException e) {
            LOGGER.error(
                    "Een van de vereiste parameters werd niet in het request gevonden.",
                    e);
            // eventueel :
            // LOGGER.debug("Naar de dispatcher.");
            // RequestDispatcher rd = getServletContext().getRequestDispatcher(
            // "/" + this._GEOZET);
            // evt. nog een foutmelding in de request stoppen
            // rd.forward(request, response);
            // return;
            throw new ServletException(
                    "Een van de vereiste parameters werd niet in het request gevonden.",
                    e);
        } catch (final NumberFormatException e) {
            LOGGER.error(
                    "Een van de vereiste parameters kon niet geparsed worden als Double.",
                    e);
            // eventueel :
            // LOGGER.debug("Naar de dispatcher.");
            // RequestDispatcher rd = getServletContext().getRequestDispatcher(
            // "/" + this._GEOZET);
            // evt. nog een foutmelding in de request stoppen
            // rd.forward(request, response);
            // return;
            throw new ServletException(
                    "Een van de vereiste parameters kon niet geparsed worden als Double.",
                    e);
        }
        LOGGER.debug("request params:" + xcoord + ":" + ycoord + " straal:"
                + straal);
        // uitlezen subset bekendmakingen/categorieen en bewust filtergebruik,
        // beide mogen null zijn
        final String[] fString = request
                .getParameterValues(REQ_PARAM_FILTER.code);
        final String filterUsed = request
                .getParameter(REQ_PARAM_EXPLICITUSEFILTER.code);
        if ((null == fString) && (null != filterUsed)
                && filterUsed.equalsIgnoreCase("true")) {
            LOGGER.debug("Er is expres gezocht met een leeg filter");
            // als filterUsed==true en fString==null dan is er bewust een leeg
            // filter gekozen, per definitie zijn er dan geen resultaten
            // output resultaat als html
            this.renderHTMLResults(request, response,
                    new Vector<SimpleFeature>());
        } else {
            LOGGER.debug("Er is wordt gezocht met een filter");
            // filter maken
            final Filter filter = this.maakFilter(xcoord, ycoord, straal,
                    fString);
            // ophalen van de bekendmakingen
            final Vector<SimpleFeature> results = this.ophalenBekendmakingen(
                    filter, xcoord, ycoord);
            // output resultaat als html
            this.renderHTMLResults(request, response, results);
        }
        response.flushBuffer();
    }

    /**
     * Initilaisatie op basis van de configuratie. init params inlezen en
     * controleren en init geotools
     * 
     * @see "http://docs.codehaus.org/display/GEOTDOC/WFS+Plugin"
     * @param config
     *            the <code>ServletConfig</code> object that contains
     *            configutation information for this servlet
     * @throws ServletException
     *             if an exception occurs that interrupts the servlet's normal
     *             operation
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        LOGGER.debug("opstarten servlet");
        super.init(config);
        // wfs capablities
        final String capsUrl = this.getServletContext().getInitParameter(
                CONFIG_PARAM_WFS_CAPABILITIES_URL.code);
        if (capsUrl == null) {
            LOGGER.fatal("config param " + CONFIG_PARAM_WFS_CAPABILITIES_URL
                    + " is null.");
            throw new ServletException("config param "
                    + CONFIG_PARAM_WFS_CAPABILITIES_URL + " is null.");
        }
        this.connectionParameters.put(
                "WFSDataStoreFactory:GET_CAPABILITIES_URL", capsUrl);
        this.typeName = config.getInitParameter(CONFIG_PARAM_WFS_TYPENAME.code);
        if (this.typeName == null) {
            LOGGER.fatal("Config param " + CONFIG_PARAM_WFS_TYPENAME
                    + " is null.");
            throw new ServletException("Config param "
                    + CONFIG_PARAM_WFS_TYPENAME + " is null.");
        }
        LOGGER.debug("typeName is: " + this.typeName);
        // wfs timeout
        final String wfsTimeout = config
                .getInitParameter(SERVLETCONFIG_WFS_TIMEOUT.code);
        int timeout;
        try {
            timeout = Integer.valueOf(wfsTimeout);
        } catch (final Exception e) {
            timeout = 5;
        }
        LOGGER.info("WFS timeout voor servlet: " + this.getServletName()
                + " ingesteld op: " + timeout + " sec.");
        this.connectionParameters.put(WFSDataStoreFactory.TIMEOUT.key,
                (timeout * 1000));
        // wfs max features
        final String wfsMaxFeat = config
                .getInitParameter(SERVLETCONFIG_WFS_MAXFEATURES.code);
        int maxFeat;
        try {
            maxFeat = Integer.valueOf(wfsMaxFeat);
        } catch (final Exception e) {
            maxFeat = DEFAULT_MAX_FEATURES.intValue();
        }
        LOGGER.info("WFS max. features voor servlet: " + this.getServletName()
                + " ingesteld op: " + maxFeat + "");
        this.connectionParameters.put(WFSDataStoreFactory.MAXFEATURES.key,
                maxFeat);
        // wfs buffer size
        this.connectionParameters.put(WFSDataStoreFactory.BUFFER_SIZE.key, 20);
        // prefer GET voor noodgevallen
        // this.connectionParameters.put(WFSDataStoreFactory.PROTOCOL.key,
        // Boolean.FALSE);

        // verbinding met de wfs server maken
        try {
            this.data = DataStoreFinder.getDataStore(this.connectionParameters);
            this.schema = this.data.getSchema(this.typeName);
            LOGGER.debug("Schema Attributen: "
                    + this.schema.getAttributeCount());
            this.source = this.data.getFeatureSource(this.typeName);
            LOGGER.debug("Metadata bounds: " + this.source.getBounds());

            final Hints hints = new Hints(Hints.CRS, CRS.decode("EPSG:28992"));
            this.geometryFactory = JTSFactoryFinder.getGeometryFactory(hints);
        } catch (final IOException e) {
            LOGGER.fatal(
                    "Verbinding met de WFS is mislukt. Controleer de configuratie en herstart de applicatie.",
                    e);
            throw new ServletException(
                    "Verbinding met de WFS server is mislukt.", e);
        } catch (final NoSuchAuthorityCodeException e) {
            LOGGER.fatal("De gevraagde CRS autoriteit is niet gevonden.", e);
            throw new ServletException(
                    "De gevraagde CRS autoriteit is niet gevonden.", e);
        } catch (final FactoryException e) {
            LOGGER.fatal(
                    "Gevraagde GeoTools factory voor CRS is niet gevonden.", e);
            throw new ServletException(
                    "Gevraagde GeoTools factory voor CRS is niet gevonden.", e);
        }
        LOGGER.debug("schema info: " + this.schema);
    }

    /**
     * Opruimen en sluiten van verbindingen.
     * 
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy() {
        this.data.dispose();
        this.data = null;
        this.schema = null;
        this.source = null;
        this.geometryFactory = null;
    }

    /**
     * Maak zoek filter.
     * 
     * @param xcoord
     *            x coordinaat van het de zoeklocatie, niet null
     * @param ycoord
     *            y coordinaat van het de zoeklocatie, niet null
     * @param straal
     *            de straal, in meter, waarbinnen we bekendmakingen gaan
     *            ophalen, niet null
     * @param categorieen
     *            lijst met categorieën van bekendmakingen, mag null zijn
     * @return het aangemaakte WFS filter
     * @throws ServletException
     *             servlet exception wordt geworpen als er een fout optreed in
     *             het maken van het {@code Filter} object
     */
    private Filter maakFilter(double xcoord, double ycoord, double straal,
            String[] categorieen) throws ServletException {

        Filter filter;
        final StringBuilder filterString = new StringBuilder();
        // cirkel filter maken
        filterString.append("DWITHIN("
                + this.schema.getGeometryDescriptor().getLocalName()
                + ", POINT(" + xcoord + " " + ycoord + "), " + straal
                /*
                 * LET OP: door een bug in GeoTools/GeoServer is de afstand
                 * uiteindelijk altijd in mapping units.
                 */
                + ", meters)");
        // uitbreiden van filter met categorieen bekendmakingen
        if (categorieen != null) {
            filterString.append(" AND (");
            for (int i = 0; i < categorieen.length; i++) {
                filterString.append(FILTER_CATEGORIE_NAAM + "='");
                filterString.append(categorieen[i]);
                filterString.append("'");
                if (i < categorieen.length - 1) {
                    filterString.append(" OR ");
                }
            }
            filterString.append(")");
        }
        LOGGER.debug("CQL voor filter is: " + filterString);

        try {
            filter = CQL.toFilter(filterString.toString());
        } catch (final CQLException e) {
            LOGGER.error("CQL Fout in de query voor de WFS.", e);
            throw new ServletException("CQL Fout in de query voor de WFS.", e);
        }
        return filter;
    }

    /**
     * Ophalen bekendmakingen bij de WFS.
     * 
     * @param filter
     *            the filter
     * @param xcoord
     *            x coordinaat van het de zoeklocatie
     * @param ycoord
     *            y coordinaat van het de zoeklocatie
     * @return Een vector met daarin de bekendmakingen gesorteerd op en
     *         aangerijkt met de afstand naar het zoekpunt
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected Vector<SimpleFeature> ophalenBekendmakingen(Filter filter,
            double xcoord, double ycoord) throws ServletException, IOException {
        // query maken
        final Query query = new Query();
        try {
            query.setCoordinateSystem(CRS.decode("EPSG:28992"));
            query.setTypeName(this.typeName);
            query.setFilter(filter);
            query.setPropertyNames(Query.ALL_NAMES);
            query.setHandle("GEOZET-handle");
        } catch (final NoSuchAuthorityCodeException e) {
            LOGGER.fatal("De gevraagde CRS autoriteit is niet gevonden.", e);
            throw new ServletException(
                    "De gevraagde CRS autoriteit is niet gevonden.", e);
        } catch (final FactoryException e) {
            LOGGER.fatal(
                    "Gevraagde GeoTools factory voor CRS is niet gevonden.", e);
            throw new ServletException(
                    "Gevraagde GeoTools factory voor CRS is niet gevonden.", e);
        }

        // data ophalen
        final SimpleFeatureCollection features = this.source.getFeatures(query);
        LOGGER.debug("Er zijn " + features.size() + " features opgehaald.");

        // zoekpunt maken voor afstandberekening
        final Point p = this.geometryFactory.createPoint(new Coordinate(xcoord,
                ycoord));

        double afstand = -1d;
        final Vector<SimpleFeature> bekendmakingen = new Vector<SimpleFeature>();

        final SimpleFeatureIterator iterator = features.features();
        try {
            while (iterator.hasNext()) {
                // voor iedere feature de afstand bepalen tussen p en geometrie
                // van de feature
                final SimpleFeature feature = iterator.next();
                LOGGER.debug("Opgehaalde feature: " + feature);
                afstand = p.distance((Geometry) feature
                        .getDefaultGeometryProperty().getValue());
                feature.getUserData().put(AFSTAND_NAAM, afstand);
                bekendmakingen.add(feature);
            }
        } finally {
            iterator.close();
        }
        // sorteren op afstand
        Collections.sort(bekendmakingen, new AfstandComparator());
        LOGGER.debug("Er zijn " + bekendmakingen.size()
                + " features gesorteerd.");
        return bekendmakingen;
    }

    /**
     * Renderen van de features in html formaat.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @param results
     *            vector met SimpleFeature objecten aangereijkt met afstand in
     *            de userdata
     * @throws IOException
     *             als er een schrijffout optreedt
     * @throws ServletException
     *             the servlet exception
     */
    protected void renderHTMLResults(HttpServletRequest request,
            HttpServletResponse response, Vector<SimpleFeature> results)
            throws IOException, ServletException {
        // response headers instellen
        response.setContentType("text/html; charset=UTF-8");
        response.setBufferSize(8192);

        // header inhaken
        final RequestDispatcher header = this.getServletContext()
                .getRequestDispatcher("/WEB-INF/zoekresultaat_begin.jsp");
        if (header != null) {
            header.include(request, response);
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(this._RESOURCES.getString("KEY_BEKENDMAKINGEN_TITEL"));
        sb.append("<dl class=\"geozetResults\">");

        sb.append("<dt class=\"first\">")
                .append(this._RESOURCES
                        .getString("KEY_BEKENDMAKINGEN_GEVONDEN"))
                .append("</dt>");

        sb.append("<dd>")
                .append(results.size())
                .append(" ")
                .append(this._RESOURCES
                        .getString("KEY_BEKENDMAKINGEN_RESULTATEN"))
                .append("</dd>");

        sb.append("<dt>")
                .append(this._RESOURCES.getString("KEY_BEKENDMAKINGEN_GEZOCHT"))
                .append(" ").append("</dt>");
        sb.append("<dd>").append(request.getParameter(REQ_PARAM_GEVONDEN.code))
                .append("</dd>");
        sb.append("</dl>");

        if (results.size() < 1) {
            sb.append("<p>")
                    .append(this._RESOURCES
                            .getString("KEY_BEKENDMAKINGEN_NIETSGEVONDEN"))
                    .append("</p>\n");
        }

        final String paginering = this.buildPageList(results.size(), request);
        sb.append(paginering);
        if (results.size() > 0) {
            // geen lege OL
            sb.append("<ol id=\"geozetResultsList\">");
        }

        final DecimalFormat fmtKilometer = new DecimalFormat("#.#");
        final DecimalFormat fmtMeter = new DecimalFormat("#");

        // String qString = request.getQueryString();
        final String qString = this.buildQueryString(request, "");

        // uitlezen offset == begin van lijst, doorgaan tot offset +
        // ctxPageItems of max results
        int currentOffset;
        try {
            currentOffset = Integer.parseInt(request
                    .getParameter(REQ_PARAM_PAGEOFFSET.code));
        } catch (final NumberFormatException e) {
            currentOffset = 0;
        }
        final int renderItems = (currentOffset + this.itemsPerPage > results
                .size() ? results.size() : currentOffset + this.itemsPerPage);

        for (int index = currentOffset; index < renderItems; index++) {
            final SimpleFeature f = results.get(index);
            sb.append("<li>");
            sb.append("<a href=\"").append(this._BEKENDMAKINGDETAIL)
                    .append("?").append(REQ_PARAM_FID).append("=")
                    .append(f.getID()).append(qString).append("\">");
            sb.append("<strong>")
                    .append(this.featureAttribuutCheck(f
                            .getAttribute(FEATURE_ATTR_NAAM_TITEL.code)))
                    .append("</strong>");
            sb.append("</a>");
            sb.append(this.featureAttribuutCheck(f
                    .getAttribute(FEATURE_ATTR_NAAM_ONDERWERP.code)));
            sb.append(" (")
                    .append(this.featureAttribuutCheck(f
                            .getAttribute(FEATURE_ATTR_NAAM_CATEGORIE.code)))
                    .append(")");
            // adres
            sb.append("<span>locatie: ").append(
                    this.featureAttribuutCheck(f
                            .getAttribute(FEATURE_ATTR_NAAM_STRAAT.code)));
            // plaatsnaam??
            // sb.append(", ").append(
            // this.featureAttribuutCheck(this.featureAttribuutCheck(f
            // .getAttribute(FEATURE_ATTR_NAAM_PLAATS.code))));

            // afstand tussen haakjes
            // LET OP: dit gaat ervan uit dat de eenheid voor de dataset meter
            // is
            final double afstand = Double.valueOf(f.getUserData()
                    .get(AFSTAND_NAAM).toString());
            if (afstand >= 1000) {
                /* afstand is een kilometer of meer */
                sb.append(" (").append(fmtKilometer.format(afstand / 1000))
                        .append(" km)");
            } else {
                sb.append(" (").append(fmtMeter.format(afstand)).append(" m)");
            }
            sb.append("</span>");
            sb.append("</li>");

        }
        if (results.size() > 0) {
            // geen lege OL
            sb.append("</ol>");
        }
        sb.append(paginering);

        // vlakgericht link
        sb.append(this._RESOURCES.getString("KEY_BEKENDMAKINGEN_OVERIG_TITEL"));
        sb.append("<p><a href=\"");
        sb.append(this._BEKENDMAKINGENVLAK)
                .append("?")
                .append(this.buildQueryString(request,
                        REQ_PARAM_PAGEOFFSET.code));
        sb.append("\">");
        sb.append(this._RESOURCES.getString("KEY_BEKENDMAKINGEN_OVERIG"));
        sb.append("</a></p>");

        final PrintWriter out = response.getWriter();
        out.print(sb);
        out.flush();

        // footer aanhaken
        final RequestDispatcher footer = this.getServletContext()
                .getRequestDispatcher("/WEB-INF/zoekresultaat_einde.jsp");
        if (footer != null) {
            footer.include(request, response);
        }
    }
}

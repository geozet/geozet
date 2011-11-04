package nl.geozet.wfs;

import static nl.geozet.common.NumberConstants.DEFAULT_VLAKGERICHT_RELEVANTIE_OPPERVLAKTE_FACTOR;
import static nl.geozet.common.NumberConstants.OPENLS_ZOOMSCALE_STANDAARD;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_OVERHEID;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_TITEL;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_URL;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_VLAKGERICHT_OPPERVLAKTE;
import static nl.geozet.common.StringConstants.REQ_PARAM_BBOX;
import static nl.geozet.common.StringConstants.REQ_PARAM_PAGEOFFSET;
import static nl.geozet.common.StringConstants.REQ_PARAM_RESPONSE_FORMAT;
import static nl.geozet.common.StringConstants.REQ_PARAM_STRAAL;
import static nl.geozet.common.StringConstants.REQ_PARAM_XCOORD;
import static nl.geozet.common.StringConstants.REQ_PARAM_YCOORD;
import static nl.geozet.common.StringConstants.SERVLETCONFIG_WFS_OPPERVLAKTE_FACTOR_CENTRUM;
import static nl.geozet.common.StringConstants.SERVLETCONFIG_WFS_OPPERVLAKTE_FACTOR_MIDDEN;
import static nl.geozet.common.StringConstants.SERVLETCONFIG_WFS_OPPERVLAKTE_FACTOR_RAND;
import static nl.geozet.common.StringConstants.SERVLETCONFIG_WFS_RELEVANTIE_OPPERVLAKTE_FACTOR;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

/**
 * WFS client voor het opzoeken en tonen van de vlakgerichte bekendmakingen.
 * DEze servlet wordt ook door de Javascript client gebruikt vanwege de
 * complexiteit van de queries, hiervoor wordt dan output in json formaat
 * gemaakt.
 * 
 * @author prinsmc@minlnv.nl
 * @since 1.6
 * @since GeoTools 2.7
 * @since Servlet API 2.5
 * @note zoeken en tonen van vlak bekendmakingen
 */
public class VlakGerichteWFSClientServlet extends WFSClientServlet {
    /** default serialVersionUID. */
    private static final long serialVersionUID = 1L;
    /** log4j logger. */
    private static final Logger LOGGER = Logger
            .getLogger(VlakGerichteWFSClientServlet.class);

    /** black hat, een percentage van de oppervlakte van een bounding box. */
    private double relevantieFactor;

    /** hoogte/breedte percentage van centrum AOI. */
    private double centrumPerc;

    /** hoogte/breedte percentage van midden AOI. */
    private double middenPerc;

    /** hoogte/breedte percentage van rand AOI. */
    private double randPerc;

    /*
     * (non-Javadoc)
     * 
     * @see nl.geozet.wfs.WFSClientServlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        LOGGER.debug("opstarten servlet");
        super.init(config);
        // relevantie uit config
        this.relevantieFactor = this
                .parseInitParam(config,
                        SERVLETCONFIG_WFS_RELEVANTIE_OPPERVLAKTE_FACTOR.code,
                        DEFAULT_VLAKGERICHT_RELEVANTIE_OPPERVLAKTE_FACTOR
                                .doubleValue());
        // bbox factoren uit config lezen
        this.centrumPerc = this.parseInitParam(config,
                SERVLETCONFIG_WFS_OPPERVLAKTE_FACTOR_CENTRUM.code, 0.05);
        this.middenPerc = this.parseInitParam(config,
                SERVLETCONFIG_WFS_OPPERVLAKTE_FACTOR_MIDDEN.code, 0.4);
        this.randPerc = this.parseInitParam(config,
                SERVLETCONFIG_WFS_OPPERVLAKTE_FACTOR_RAND.code, 0.8);

        LOGGER.debug("schema info: " + this.schema);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * nl.geozet.wfs.WFSClientServlet#service(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        double bbox[];
        try {
            // parsen request param's, hetzij een bbox hetzij een straal en x/y
            if (request.getParameter(REQ_PARAM_BBOX.code) != null) {
                // bbox
                final String[] s = (request.getParameter(REQ_PARAM_BBOX.code)
                        .replaceAll(" ", "")).split(",");
                LOGGER.debug("request params bbox = " + Arrays.toString(s));
                bbox = new double[4];
                for (int i = 0; i < s.length; i++) {
                    bbox[i] = Double.valueOf(s[i]);
                }
            } else {
                // request params uitlezen voor contructie bbox
                final double xcoord = Double.valueOf(request
                        .getParameter(REQ_PARAM_XCOORD.code));
                final double ycoord = Double.valueOf(request
                        .getParameter(REQ_PARAM_YCOORD.code));
                final double straal = Double
                        .valueOf((null == request
                                .getParameter(REQ_PARAM_STRAAL.code) ? OPENLS_ZOOMSCALE_STANDAARD
                                .toString() : request
                                .getParameter(REQ_PARAM_STRAAL.code)));
                LOGGER.debug("request params (x:y) straal = (" + xcoord + ":"
                        + ycoord + ") straal:" + straal);
                final double minx = (xcoord - (straal / 2));
                final double miny = (ycoord - (straal / 2));
                final double maxx = (xcoord + (straal / 2));
                final double maxy = (ycoord + (straal / 2));
                bbox = new double[] { minx, miny, maxx, maxy };
            }
        } catch (final NullPointerException e) {
            LOGGER.error(
                    "Een van de vereiste parameters werd niet in het request gevonden.",
                    e);
            throw new ServletException(
                    "Een van de vereiste parameters werd niet in het request gevonden.",
                    e);
        } catch (final NumberFormatException e) {
            LOGGER.error(
                    "Een van de vereiste parameters kon niet geparsed worden als Double.",
                    e);
            throw new ServletException(
                    "Een van de vereiste parameters kon niet geparsed worden als Double.",
                    e);
        }
        // output type bepalen
        boolean renderJSON = false;
        final String format = request
                .getParameter(REQ_PARAM_RESPONSE_FORMAT.code);
        if (format != null) {
            if (format.equalsIgnoreCase("json")) {
                renderJSON = true;
            }
        }

        // maak filters
        final Filter[] flist = this.maakFilter(bbox);
        // ophalen features
        final Vector<SimpleFeature> centrum = this.ophalenBekendmakingen(
                flist[0], 0, 0);
        final Vector<SimpleFeature> midden = this.ophalenBekendmakingen(
                flist[1], 0, 0);
        final Vector<SimpleFeature> rand = this.ophalenBekendmakingen(flist[2],
                0, 0);

        // final Vector<SimpleFeature> results = new Vector<SimpleFeature>();
        final HashSet<SimpleFeature> results = new HashSet<SimpleFeature>();
        // uitfilteren van de dubbele door ze in een Set te stoppen, dit moet
        // genoeg zijn
        results.addAll(rand);
        results.addAll(midden);
        results.addAll(centrum);

        // // eerst uit de rand
        // for (final SimpleFeature f : rand) {
        // if (!results.contains(f)) {
        // LOGGER.debug("toevoegen van f: " + f.getIdentifier().getID()
        // + " uit rand.");
        // results.add(f);
        // }
        // }
        // // dan midden
        // for (final SimpleFeature f : midden) {
        // if (!results.contains(f)) {
        // LOGGER.debug("toevoegen van f: " + f.getIdentifier().getID()
        // + " uit midden.");
        // results.add(f);
        // }
        // }
        // // dan centrum
        // for (final SimpleFeature f : centrum) {
        // if (!results.contains(f)) {
        // LOGGER.debug("toevoegen van f: " + f.getIdentifier().getID()
        // + " uit centrum.");
        // results.add(f);
        // }
        // }
        // output json of html
        if (renderJSON) {
            this.renderJSONResults(request, response, results);
        } else {
            // set naar Vector
            this.renderHTMLResults(request, response,
                    new Vector<SimpleFeature>(results));
        }
    }

    /**
     * Maak zoek filter.
     * 
     * @param bbox
     *            the bbox
     * @return het aangemaakte WFS filter
     * @throws ServletException
     *             servlet exception wordt geworpen als er een fout optreed in
     *             het maken van het {@code Filter} object
     */
    private Filter[] maakFilter(double[] bbox) throws ServletException {
        final Filter filterCentrum;
        final Filter filterMidden;
        final Filter filterRand;

        try {
            filterCentrum = CQL.toFilter(this.bepaalRelevanteOppervlakte(bbox)
                    + " AND " + this.bepaalBBoxAOI(bbox, this.centrumPerc));

            filterMidden = CQL.toFilter(this.bepaalRelevanteOppervlakte(bbox)
                    + " AND " + this.bepaalBBoxAOI(bbox, this.middenPerc)
                    + " AND NOT " + this.bepaalBBoxAOI(bbox, this.centrumPerc));

            filterRand = CQL.toFilter(this.bepaalRelevanteOppervlakte(bbox)
                    + " AND " + this.bepaalBBoxAOI(bbox, this.randPerc)
                    + " AND NOT " + this.bepaalBBoxAOI(bbox, this.middenPerc));
        } catch (final CQLException e) {
            LOGGER.error("CQL Fout in de query voor de WFS.", e);
            throw new ServletException("CQL Fout in de query voor de WFS.", e);
        }
        return new Filter[] { filterCentrum, filterMidden, filterRand };
    }

    /**
     * Berekent de relevante oppervlakte voor een query.
     * 
     * @param bbox
     *            de bbox {@code double[] minx,miny,maxx,maxy}
     * @return the string
     */
    private String bepaalRelevanteOppervlakte(double[] bbox) {
        final int opp = (int) ((bbox[2] - bbox[0]) * (bbox[3] - bbox[1]) * this.relevantieFactor);
        LOGGER.debug("Relevante oppervlakte voor bbox: "
                + Arrays.toString(bbox) + " is " + opp);
        return FEATURE_ATTR_NAAM_VLAKGERICHT_OPPERVLAKTE + " >" + opp;
    }

    /**
     * geeft de bbox clause voor een query.
     * 
     * @param bbox
     *            the bbox {@code double[] minx,miny,maxx,maxy}
     * @param factor
     *            verkleinings factor (procent als double, dus bijv. 0.4 voor
     *            40%)
     * @return the string
     */
    private String bepaalBBoxAOI(double[] bbox, double factor) {
        final double deltax = (bbox[2] - bbox[0]) * factor;
        final double deltay = (bbox[3] - bbox[1]) * factor;

        LOGGER.debug("delta X = " + deltax + " delta Y = " + deltay
                + " voor bbox: " + Arrays.toString(bbox) + " met factor "
                + factor);

        final double minx = bbox[0] + deltax;
        final double miny = bbox[1] + deltay;
        final double maxx = bbox[2] - deltax;
        final double maxy = bbox[3] - deltay;
        return " BBOX(" + this.schema.getGeometryDescriptor().getLocalName()
                + ", " + minx + "," + miny + "," + maxx + "," + maxy + ") ";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * nl.geozet.wfs.WFSClientServlet#ophalenBekendmakingen(org.opengis.filter
     * .Filter, double, double)
     */
    @Override
    protected Vector<SimpleFeature> ophalenBekendmakingen(Filter filter,
            double xcoord, double ycoord) throws ServletException, IOException {

        LOGGER.debug("ophalen bekendmakingen voor filter: " + filter);
        // query maken
        final Query query = new Query();
        try {
            query.setCoordinateSystem(CRS.decode("EPSG:28992"));
            query.setTypeName(this.typeName);
            query.setFilter(filter);
            query.setPropertyNames(new String[] {
                    this.schema.getGeometryDescriptor().getName().toString(),
                    FEATURE_ATTR_NAAM_TITEL.code,
                    FEATURE_ATTR_NAAM_OVERHEID.code, FEATURE_ATTR_NAAM_URL.code });
            query.setHandle("GEOZET-handle");

            final FilterFactory2 ff = CommonFactoryFinder
                    .getFilterFactory2(GeoTools.getDefaultHints());
            final SortBy[] sortBy = { ff.sort(
                    FEATURE_ATTR_NAAM_VLAKGERICHT_OPPERVLAKTE.code,
                    SortOrder.ASCENDING) };
            query.setSortBy(sortBy);

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

        final Vector<SimpleFeature> bekendmakingen = new Vector<SimpleFeature>();
        final SimpleFeatureIterator iterator = features.features();
        try {
            while (iterator.hasNext()) {
                // voor iedere feature de afstand bepalen tussen p en geometrie
                // van de feature
                final SimpleFeature feature = iterator.next();
                bekendmakingen.add(feature);
            }
        } finally {
            iterator.close();
        }
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
    @Override
    protected void renderHTMLResults(HttpServletRequest request,
            HttpServletResponse response, Vector<SimpleFeature> results)
            throws IOException, ServletException {
        // response headers instellen
        response.setContentType("text/html; charset=UTF-8");
        response.setBufferSize(8192);

        // header inhaken
        final RequestDispatcher header = this.getServletContext()
                .getRequestDispatcher("/WEB-INF/vlakzoekresultaat_begin.jsp");
        if (header != null) {
            header.include(request, response);
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(this._RESOURCES.getString("KEY_BEKENDMAKINGEN_VLAK_TITEL"));
        sb.append(this._RESOURCES.getString("KEY_BEKENDMAKINGEN_VLAK_INTRO"));

        if (results.size() < 1) {
            sb.append("<p>")
                    .append(this._RESOURCES
                            .getString("KEY_BEKENDMAKINGEN_VLAK_NIETSGEVONDEN"))
                    .append("</p>\n");
        }
        sb.append(MessageFormat.format(
                this._RESOURCES.getString("KEY_BEKENDMAKINGEN_VLAK_RESULTATEN"),
                results.size()));

        final String paginering = this.buildPageList(results.size(), request);
        sb.append(paginering);
        if (results.size() > 0) {
            // geen lege OL
            sb.append("<ol id=\"geozetResultsList\">");
        }
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
            sb.append("<a href=\"")
                    .append(this.featureAttribuutCheck(f
                            .getAttribute(FEATURE_ATTR_NAAM_URL.code)))
                    .append("\" class=\"extern\">");
            sb.append("<strong>")
                    .append(this.featureAttribuutCheck(f
                            .getAttribute(FEATURE_ATTR_NAAM_TITEL.code)))
                    .append("</strong>");
            sb.append("<span>")
                    .append("Bron: ")
                    .append(this.featureAttribuutCheck(f
                            .getAttribute(FEATURE_ATTR_NAAM_OVERHEID.code)))
                    .append("</span>");
            sb.append("</a>");
            sb.append("</li>");
        }
        if (results.size() > 0) {
            // geen lege OL
            sb.append("</ol>");
        }
        sb.append(paginering);
        final PrintWriter out = response.getWriter();
        out.print(sb);
        out.flush();

        // header inhaken
        final RequestDispatcher tussen = this.getServletContext()
                .getRequestDispatcher("/WEB-INF/vlakzoekresultaat_tussen.jsp");
        if (tussen != null) {
            tussen.include(request, response);
        }

        // terug link (de paginering wordt eraf geknipt!)
        final String qString = this.buildQueryString(request,
                REQ_PARAM_PAGEOFFSET.code);
        sb.setLength(0);
        sb.append("<a class=\"back\" href=\"")
                .append(this._BEKENDMAKINGEN)
                .append("?")
                .append(qString)
                .append("\" >")
                .append(this._RESOURCES
                        .getString("KEY_BEKENDMAKINGEN_VLAK_TERUG"))
                .append("</a>");
        out.print(sb);
        out.flush();

        // footer aanhaken
        final RequestDispatcher footer = this.getServletContext()
                .getRequestDispatcher("/WEB-INF/vlakzoekresultaat_einde.jsp");
        if (footer != null) {
            footer.include(request, response);
        }
    }

    /**
     * Renderen van de features in json formaat.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @param results
     *            vector met SimpleFeature objecten
     * @throws IOException
     *             als er een schrijffout optreedt
     * @throws ServletException
     *             the servlet exception
     */
    private void renderJSONResults(HttpServletRequest request,
            HttpServletResponse response, Collection<SimpleFeature> results)
            throws IOException, ServletException {
        // response headers instellen
        response.setBufferSize(8192);
        response.setContentType("application/json");

        final StringBuilder sb = new StringBuilder();
        sb.append("{vlakken:[");
        for (final SimpleFeature f : results) {
            sb.append("{");
            sb.append("titel:")
                    .append("'")
                    .append(this.escape(this.featureAttribuutCheck(f
                            .getAttribute(FEATURE_ATTR_NAAM_TITEL.code))))
                    .append("'").append(",");
            sb.append("url:")
                    .append("'")
                    .append(this.escape(this.featureAttribuutCheck(f
                            .getAttribute(FEATURE_ATTR_NAAM_URL.code))))
                    .append("'").append(",");
            sb.append("overheid:")
                    .append("'")
                    .append(this.escape(this.featureAttribuutCheck(f
                            .getAttribute(FEATURE_ATTR_NAAM_OVERHEID.code))))
                    .append("'");
            sb.append("}").append(",");
        }
        if (sb.lastIndexOf(",") == sb.length() - 1) {
            // als laatste char een komma is, die er af halen
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]}");
        final PrintWriter out = response.getWriter();
        out.print(sb);
        out.flush();
    }

    /**
     * escape ' en " tekens in de input.
     * 
     * @param input
     *            te behandelen string
     * @return input met escaped ' en " characters
     */
    private String escape(String input) {
        LOGGER.debug("escaping: " + input);
        return (input.replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\""));
    }
}

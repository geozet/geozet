package nl.geozet.wfs;

import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_CATEGORIE;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_DATUM;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_DESCRIPTION;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_ONDERWERP;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_OVERHEID;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_PLAATS;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_STRAAT;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_TITEL;
import static nl.geozet.common.StringConstants.FEATURE_ATTR_NAAM_URL;
import static nl.geozet.common.StringConstants.REQ_PARAM_FID;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

/**
 * Maakt de detail pagina voor een object uit de WFS.
 * 
 * @author prinsmc@minlnv.nl
 * @since 1.6
 * @since GeoTools 2.7
 * @since Servlet API 2.5
 * @note zoeken en tonen van de details van een bekendmaking
 */
public class WFSDetailsServlet extends WFSClientServlet {

    /** generated serialVersionUID. */
    private static final long serialVersionUID = -3492626956386213039L;
    /** log4j logger. */
    private static final Logger LOGGER = Logger
            .getLogger(WFSDetailsServlet.class);

    /*
     * (non-Javadoc)
     * 
     * @see nl.geozet.wfs.WFSClientServlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
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
        // lees request param fid
        final String fid = request.getParameter(REQ_PARAM_FID.code);
        if ((fid == null) || (fid.length() < 1)) {
            LOGGER.fatal("De vereiste parameter (" + REQ_PARAM_FID
                    + ") kon niet geparsed worden.");
            throw new ServletException("De vereiste parameter ("
                    + REQ_PARAM_FID + ") kon niet geparsed worden.");
        }
        final Filter filter = this.maakFilter(fid);
        final SimpleFeature sf = this.ophalenBekendmaking(filter);
        this.renderHTMLResults(request, response, sf);
    }

    /**
     * maakt het query filter voor de WFS query.
     * 
     * @param fid
     *            Feature ID
     * @return het gemaakte filter
     */
    private Filter maakFilter(String fid) {
        final Set<FeatureId> fids = new HashSet<FeatureId>();
        final FilterFactory2 ff = CommonFactoryFinder
                .getFilterFactory2(GeoTools.getDefaultHints());
        fids.add(ff.featureId(fid));
        final Filter filter = ff.id(fids);
        return filter;
    }

    /**
     * Verzorgt de output voor deze servlet in HTML formaat.
     * 
     * @param request
     *            de servlet request
     * @param response
     *            de servlet response
     * @param f
     *            de (simple) feature waarvan de details worden gerenderd
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             de servlet exception
     * @see nl.geozet.wfs.WFSClientServlet#renderHTMLResults(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse,java.util.Vector)
     */
    protected void renderHTMLResults(HttpServletRequest request,
            HttpServletResponse response, SimpleFeature f) throws IOException,
            ServletException {
        // response headers instellen
        response.setContentType("text/html; charset=UTF-8");
        response.setBufferSize(4096);

        // header inhaken
        final RequestDispatcher header = this.getServletContext()
                .getRequestDispatcher(
                        "/WEB-INF/bekendmakingdetail_begin_voor_title.jsp");
        if (header != null) {
            header.include(request, response);
        }

        LOGGER.debug("rendering feature: " + f);
        final String titel = "<title>Bekendmaking detail - "
                + this.featureAttribuutCheck(f
                        .getAttribute(FEATURE_ATTR_NAAM_TITEL.code))
                + "</title>";

        final PrintWriter out = response.getWriter();
        out.println(titel);

        // header inhaken
        final RequestDispatcher header2 = this.getServletContext()
                .getRequestDispatcher("/WEB-INF/bekendmakingdetail_begin.jsp");
        if (header2 != null) {
            header2.include(request, response);
        }

        final StringBuilder sb = new StringBuilder();
        // titel
        sb.append("<")
                .append(this._RESOURCES
                        .getString("KEY_BEKENDMAKING_TITEL_NIVO"))
                .append(" class=\"title-main\">")
                .append(this.featureAttribuutCheck(f
                        .getAttribute(FEATURE_ATTR_NAAM_TITEL.code)))
                .append("</")
                .append(this._RESOURCES
                        .getString("KEY_BEKENDMAKING_TITEL_NIVO")).append(">");
        sb.append("<ul class=\"geozetDetails\">");
        // type
        sb.append("<li>")
                .append(this.featureAttribuutCheck(f
                        .getAttribute(FEATURE_ATTR_NAAM_ONDERWERP.code)))
                .append("</li>");
        // thema
        sb.append("<li>")
                .append(this.featureAttribuutCheck(f
                        .getAttribute(FEATURE_ATTR_NAAM_CATEGORIE.code)))
                .append("</li>");
        // adres
        sb.append("<li>")
                .append(this.featureAttribuutCheck(f
                        .getAttribute(FEATURE_ATTR_NAAM_STRAAT.code)))
                .append(", ")
                .append(this.featureAttribuutCheck(f
                        .getAttribute(FEATURE_ATTR_NAAM_PLAATS.code)))
                .append("</li>");
        // datum
        final GregorianCalendar cal = (GregorianCalendar) f
                .getAttribute(FEATURE_ATTR_NAAM_DATUM.code);
        final SimpleDateFormat dFmt = new SimpleDateFormat("dd-MM-yyyy");
        dFmt.setCalendar(cal);
        sb.append("<li>").append(dFmt.format(cal.getTime())).append("</li>");
        sb.append("</ul>");
        // beschrijving
        sb.append("<p>");
        sb.append(this.featureAttribuutCheck(f
                .getAttribute(FEATURE_ATTR_NAAM_DESCRIPTION.code)));
        sb.append("</p>");
        // externe link
        sb.append("<p><a href=\"")
                .append(this.featureAttribuutCheck(f
                        .getAttribute(FEATURE_ATTR_NAAM_URL.code)))
                .append("\" class=\"extern\">")
                .append(this._RESOURCES.getString("KEY_BEKENDMAKING_BEKIJKBIJ"))
                .append(" ")
                .append(this.featureAttribuutCheck(f
                        .getAttribute(FEATURE_ATTR_NAAM_OVERHEID.code)))
                .append("</a></p>");

        out.print(sb);
        out.flush();

        final RequestDispatcher tussen = this.getServletContext()
                .getRequestDispatcher("/WEB-INF/bekendmakingdetail_tussen.jsp");
        if (tussen != null) {
            tussen.include(request, response);
        }

        // terug link (de paginering wordt eraf geknipt!)
        final String qString = this.buildQueryString(request,
                REQ_PARAM_FID.code);
        sb.setLength(0);
        sb.append("<a href=\"").append(this._BEKENDMAKINGEN).append("?")
                .append(qString).append("\" class=\"back\">")
                .append(this._RESOURCES.getString("KEY_BEKENDMAKING_TERUG"))
                .append("</a>");
        out.print(sb);
        out.flush();

        // footer inhaken
        final RequestDispatcher footer = this.getServletContext()
                .getRequestDispatcher("/WEB-INF/bekendmakingdetail_einde.jsp");
        if (footer != null) {
            footer.include(request, response);
        }
    }

    /**
     * Ophalen van een bekendmaking op baisis van de feature ID.
     * 
     * @param filter
     *            het filter
     * @return de simple feature die is opgehaald bij de WFS
     * @throws ServletException
     *             servlet exception wordt geworpen als er een fout optreed
     *             tijdens het ophalen, deze kan een geneste fout bevatten
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected SimpleFeature ophalenBekendmaking(Filter filter)
            throws ServletException, IOException {
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
        LOGGER.debug("Er is/zijn " + features.size()
                + " (max. 1!) feature(s) opgehaald.");
        if (1 != features.size()) {
            LOGGER.warn("Er zijn meer of minder dan één (1) features (namelijk: "
                    + features.size()
                    + ") opgehaald voor een bekende feature ID, dit duidt op een data integriteits probleem.");
        }
        return (features.toArray(new SimpleFeature[1]))[0];
    }

}

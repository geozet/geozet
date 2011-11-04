package nl.geozet.common;

import static nl.geozet.common.NumberConstants.DEFAULT_ITEMS_PER_PAGINA;
import static nl.geozet.common.StringConstants.CONFIG_PARAM_BEKENDMAKINGENSERVLET;
import static nl.geozet.common.StringConstants.CONFIG_PARAM_BEKENDMAKINGSERVLET;
import static nl.geozet.common.StringConstants.CONFIG_PARAM_GEOZETSERVLET;
import static nl.geozet.common.StringConstants.CONFIG_PARAM_LOCATIESERVLET;
import static nl.geozet.common.StringConstants.CONFIG_PARAM_PAGINALENGTE;
import static nl.geozet.common.StringConstants.CONFIG_PARAM_RESOURCENAME;
import static nl.geozet.common.StringConstants.CONFIG_PARAM_VLAKBEKENDMAKINGSERVLET;
import static nl.geozet.common.StringConstants.REQ_PARAM_PAGEOFFSET;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Algemene initialisatie code en gedeelde functies voor de GEOZET servlets.
 * 
 * @author prinsmc@minlnv.nl
 * @since 1.6
 * @since Servlet API 2.5
 * @note gedeelde basis voor de geozet servlets
 */
public abstract class ServletBase extends HttpServlet {
    /** default serialVersionUID. */
    private static final long serialVersionUID = -1L;
    /** log4j logger. */
    private static final Logger LOGGER = Logger.getLogger(ServletBase.class);
    /**
     * aantal items op een pagina.
     * 
     * @see NumberConstants#DEFAULT_ITEMS_PER_PAGINA
     */
    protected int itemsPerPage;
    /** De gedeelde, read-only, resourcebundel voor de applicatie. */
    protected CoreResources _RESOURCES;
    /**
     * naam van de bekendmakingen servlet tbv url generatie en request
     * forwarding.
     */
    protected String _BEKENDMAKINGEN = null;
    /**
     * naam van de bekendmaking detail servlet tbv url generatie en request
     * forwarding.
     */
    protected String _BEKENDMAKINGDETAIL = null;
    /**
     * naam van de vlak bekendmakingen servlet tbv url generatie en request
     * forwarding.
     */
    protected String _BEKENDMAKINGENVLAK = null;
    /** naam van de locatie servlet tbv url generatie en request forwarding. */
    protected String _LOCATIE = null;
    /** naam van de geozet servlet tbv url generatie en request forwarding. */
    protected String _GEOZET = null;

    /**
     * Zorgt voor het ophalen en uitlezen en valideren van de context
     * parameters.
     * 
     * @param config
     *            de servletconfig
     * @throws ServletException
     *             the servlet exception
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            this.itemsPerPage = Integer.valueOf(this.getServletContext()
                    .getInitParameter(CONFIG_PARAM_PAGINALENGTE.code));
        } catch (final NumberFormatException e) {
            this.itemsPerPage = DEFAULT_ITEMS_PER_PAGINA.intValue();
        }
        LOGGER.debug("Er komen " + this.itemsPerPage + " items per pagina.");

        this._BEKENDMAKINGEN = this.getServletContext().getInitParameter(
                CONFIG_PARAM_BEKENDMAKINGENSERVLET.code);
        this._BEKENDMAKINGDETAIL = this.getServletContext().getInitParameter(
                CONFIG_PARAM_BEKENDMAKINGSERVLET.code);
        this._LOCATIE = this.getServletContext().getInitParameter(
                CONFIG_PARAM_LOCATIESERVLET.code);
        this._GEOZET = this.getServletContext().getInitParameter(
                CONFIG_PARAM_GEOZETSERVLET.code);
        this._BEKENDMAKINGENVLAK = this.getServletContext().getInitParameter(
                CONFIG_PARAM_VLAKBEKENDMAKINGSERVLET.code);
        if ((null == this._BEKENDMAKINGEN)
                || (null == this._BEKENDMAKINGDETAIL)
                || (null == this._LOCATIE) || (null == this._GEOZET)
                || (null == this._BEKENDMAKINGENVLAK)) {
            LOGGER.fatal("Een van de vereiste context configuratie parameters "
                    + "is niet gevonden in de webapp configuratie.");
            throw new ServletException(
                    "Een van de vereiste context configuratie parameters "
                            + "is niet gevonden in de webapp configuratie.");
        }
        this._RESOURCES = new CoreResources(this.getServletContext()
                .getInitParameter(CONFIG_PARAM_RESOURCENAME.code));

    }

    /**
     * Maakt de parameter string, onafhankelijk van de toegangsmethode (POST of
     * GET).
     * 
     * @param request
     *            het request van dit service verzoek
     * @param excludeparam
     *            een request parameter die eventueel uitgesloten dient te
     *            worden
     * @return de string begint met een ampersand (&amp;)
     */
    protected final String buildQueryString(HttpServletRequest request,
            String excludeparam) {
        /* want servlet api is niet generic */
        @SuppressWarnings("unchecked")
        final Map<String, String[]> reqs = request.getParameterMap();
        final StringBuilder s = new StringBuilder();
        String key;
        String[] vals;

        for (final Map.Entry<String, String[]> entry : reqs.entrySet()) {
            key = entry.getKey();
            if (key.equals(excludeparam)) {
                continue;
            }
            s.append("&amp;").append(key).append("=");
            vals = entry.getValue();
            final int count = vals.length;
            if (count > 1) {
                for (int i = 0; i < count; i++) {
                    s.append(vals[i]);
                    s.append("&amp;").append(key).append("=");
                }
                s.delete(s.lastIndexOf("&amp;"), s.length());
            } else {
                s.append(vals[0]);
            }
        }
        // dit moet soms wel soms niet..dus maar even niet: s.delete(0,
        // "&amp;".length());
        return s.toString();
    }

    /**
     * Geeft een paginering in HTML formaat. Als in een resultaatlijst
     * (bijvoorbeeld bij de zoekpagina) meer dan een configureerbaar aantal
     * resultaten gevonden worden, komen er meerdere pagina's met
     * zoekresultaten. In dit geval komt er boven en onder de resultaatlijst een
     * paginanummering te staan. Deze bestaat uit een aantal elementen:
     * <dl>
     * <dt>Vorige</dt>
     * <dd>Indien de huidige resultaatpagina niet de eerste is begint de
     * paginanummering met een link 'Vorige'</dd>
     * <dt>Volgende</dt>
     * <dd>Indien de huidige resultaatpagina niet de laatste is eindigt de
     * paginanummering met een link 'Volgende'</dd>
     * <dt>Paginanummers</dt>
     * <dd>Een maximum van 7 paginanummers staat in de lijst. Dit is de huidige
     * pagina, twee ervoor, twee erna en de eerste en laatste pagina. De
     * volgende regels gelden hiervoor:
     * <ul>
     * <li>Indien er geen twee pagina's voor de huidige pagina zijn worden er
     * meer nummers erna getoond. En vice versa. Het totaal van deze rij blijft
     * altijd 5.</li>
     * <li>Indien in de twee pagina's voor of na de huidige pagina (of in de
     * uitzonderingsregel hierboven) de eerste of laatste pagina is opgenomen
     * vervalt deze als aparte optie.</li>
     * <li>Tussen de rij van 5 paginanummers en de eerste danwel laatste is een
     * extra item met '...'.</li>
     * <li>Afgezien van 'Vorige', 'Volgende' en '...' worden er alleen numerieke
     * waarden getoond (de paginanummers)</li>
     * </ul>
     * </dd>
     * </dl>
     * 
     * @param items
     *            aantal items
     * @param request
     *            the request
     * @return een html snippet met hyperlinks of een lege string als er geen
     *         paginering is
     */
    protected final String buildPageList(int items, HttpServletRequest request) {
        final int totaalAantalPaginas = (int) Math.ceil((double) items
                / this.itemsPerPage);
        LOGGER.debug("Er komen " + totaalAantalPaginas + " pagina's");
        if (totaalAantalPaginas <= 1) {
            // bij 1 (of minder) geen paginering, klaar!
            return "";
        }

        int currentOffset;
        try {
            currentOffset = Integer.parseInt(request
                    .getParameter(REQ_PARAM_PAGEOFFSET.code));
        } catch (final NullPointerException e) {
            currentOffset = 0;
        } catch (final NumberFormatException e) {
            currentOffset = 0;
        }
        final String reqString = this.buildQueryString(request,
                REQ_PARAM_PAGEOFFSET.code);

        final String addParam = request.getServletPath().replaceFirst("/", "")
                + "?" + reqString + "&amp;" + REQ_PARAM_PAGEOFFSET + "=";

        final int eerste = 0 * this.itemsPerPage;
        final int vorige = currentOffset - this.itemsPerPage;
        final int volgende = currentOffset + this.itemsPerPage;
        final int laatste = (totaalAantalPaginas - 1) * this.itemsPerPage;

        final StringBuilder sb = new StringBuilder(
                "<ul class=\"geozetPaging\">");
        // vorige
        if (vorige >= 0) {
            sb.append("<li class=\"prev first\"><a href=\"").append(addParam)
                    .append(vorige).append("\">")
                    .append(this._RESOURCES.getString("KEY_PAGINERING_VORIGE"))
                    .append("</a></li>");
        }
        // eerste pag
        if (eerste != currentOffset) {
            sb.append(this.maakPaginaItem(addParam, eerste));
        }

        LOGGER.debug("eerste=" + eerste + " huidige=" + currentOffset);
        // ... tussendoor
        if (totaalAantalPaginas > 6) {
            // alleen als aantal pagina's groter is dan 5
            if (currentOffset - eerste > 3 * this.itemsPerPage) {
                // alleen als er meer dan 3 paginas tussen eerste en huidige
                // zitten
                sb.append("<li>...</li>");
            }
        }

        // bepalen welke pagina's voorafgaand en volgend op
        int start = currentOffset - (2 * this.itemsPerPage);
        int maxPages = 5;
        if (start < 0) {
            // minimaal bij offset 0 beginnen
            start = 0;
        }

        if (currentOffset >= (totaalAantalPaginas - 3) * this.itemsPerPage) {
            // maximaal bij totaal aantal minus 5 eindigen
            start = (totaalAantalPaginas * this.itemsPerPage)
                    - (maxPages * this.itemsPerPage);
            if (start < 0) {
                // voor het geval er minder dan 5 pagina's zijn
                start = 0;
                maxPages = totaalAantalPaginas;
            }

        }
        // pagina's
        int offset = 0;
        for (int i = 0; i < maxPages; i++) {
            offset = start + i * this.itemsPerPage;
            if (currentOffset == offset) {
                LOGGER.debug("pagina " + (i + 1)
                        + " is de huidige pagina en heeft offset " + offset);
                sb.append("<li class=\"active\"><strong>")
                        .append(offset / this.itemsPerPage + 1)
                        .append("</strong></li>");
            } else {
                if (!((eerste == offset) || (laatste == offset))) {
                    // eerste en laatste overslaan, die komen er sowieso in
                    LOGGER.debug("pagina " + (i + 1) + " heeft offset "
                            + offset);
                    sb.append(this.maakPaginaItem(addParam, offset));
                }
            }
        }

        // ... tussendoor
        if (totaalAantalPaginas > 6) {
            if (laatste - currentOffset > 3 * this.itemsPerPage) {
                // alleen als er meer dan 3 paginas tussen laatste en huidige
                // zitten
                sb.append("<li>...</li>");
            }
        }

        // laatste pag
        if (laatste != currentOffset) {
            sb.append(this.maakPaginaItem(addParam, laatste));
        }
        // volgende alleen als we nog niet aan het einde zijn
        if (volgende <= laatste) {
            sb.append("<li class=\"next\"><a href=\"")
                    .append(addParam)
                    .append(volgende)
                    .append("\">")
                    .append(this._RESOURCES
                            .getString("KEY_PAGINERING_VOLGENDE"))
                    .append("</a></li>");
        }
        sb.append("</ul>");
        return sb.toString();
    }

    /**
     * genereer een pagina link.
     * 
     * @param addParam
     *            request string snipper
     * @param plusPag
     *            pagina offset snipper
     * @return pagina hyperlink
     */
    private String maakPaginaItem(String addParam, int plusPag) {
        return "<li><a href=\"" + addParam + plusPag + "\">"
                + (plusPag / this.itemsPerPage + 1) + "</a></li>";
    }

    /**
     * voor string attributen; kijken of ze null zijn, zo ja dan een lege string
     * anders de stringweergave van het object.
     * 
     * @param input
     *            te controleren object
     * @return stringweergave van de input
     */
    public final String featureAttribuutCheck(Object input) {
        try {
            return input.toString();
        } catch (final NullPointerException npe) {
            return "";
        }
    }

    /**
     * parse gevraagde param en geef die terug als double of de default waarde.
     * 
     * @param config
     *            de servletconfig
     * @param s
     *            param naam waarvan de waarde opgehaald moet worden
     * @param dflt
     *            de default waarde
     * @return de waarde van de gevraagde param of de default waarde.
     */
    public final double parseInitParam(ServletConfig config, String s,
            double dflt) {
        try {
            return Double.valueOf(config.getInitParameter(s));
        } catch (final NumberFormatException e) {
            return dflt;
        } catch (final NullPointerException e) {
            return dflt;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        throw new ServletException("HEAD is geen ondersteunde functie.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        throw new ServletException("PUT is geen ondersteunde functie.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        throw new ServletException("DELETE is geen ondersteunde functie.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doOptions(javax.servlet.http.
     * HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        throw new ServletException("OPTIONS is geen ondersteunde functie.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.http.HttpServlet#doTrace(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        throw new ServletException("TRACE is geen ondersteunde functie.");
    }
}

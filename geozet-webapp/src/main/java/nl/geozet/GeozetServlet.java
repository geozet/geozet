package nl.geozet;

import static nl.geozet.common.StringConstants.REQ_PARAM_ADRES;
import static nl.geozet.common.StringConstants.REQ_PARAM_STRAAL;
import static nl.geozet.common.StringConstants.REQ_PARAM_XCOORD;
import static nl.geozet.common.StringConstants.REQ_PARAM_YCOORD;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.geozet.common.ServletBase;

import org.apache.log4j.Logger;

/**
 * GeozetServlet is een "dispatcher" voor de applicatie GEOZET viewer. De
 * servlet bepaald op basis van de request parameters welke service wordt
 * aangestuurd.
 * 
 * @author prinsmc@minlnv.nl
 * @author strampel@atlis.nl
 * @since 1.5
 * @since Servlet API 2.5
 * @note dispatcher naar OLS en WFS clients
 */
public class GeozetServlet extends ServletBase {
    /** generated serialVersionUID. */
    private static final long serialVersionUID = -412117385071887803L;

    /** log4j logger. */
    private static final Logger LOGGER = Logger.getLogger(GeozetServlet.class);

    /**
     * In deze methode worden de request parametrs uitegelezen en wordt bepaald
     * aan welke servlet het verzoek wordt toebedeeld voor verdere afhandeling.
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // controle voor wfs param, als er een coord paar en een straal is
        // kunnen we de bekendmakingen ophalen
        double xcoord = 0;
        double ycoord = 0;
        double straal = 0;
        try {
            // request params uitlezen voor het zoeken
            xcoord = Double
                    .valueOf(request.getParameter(REQ_PARAM_XCOORD.code));
            ycoord = Double
                    .valueOf(request.getParameter(REQ_PARAM_YCOORD.code));
            straal = Double
                    .valueOf(request.getParameter(REQ_PARAM_STRAAL.code));
        } catch (final NullPointerException e) {
            // niets doen
            LOGGER.debug("Geen geldige request param gevonden voor WFS request.");
        } catch (final NumberFormatException e) {
            // niets doen
            LOGGER.debug("Geen getal gevonden voor " + REQ_PARAM_XCOORD + ", "
                    + REQ_PARAM_YCOORD + " of " + REQ_PARAM_STRAAL);
        }
        if ((xcoord > 0) && (ycoord > 0) && (straal > 0)) {
            // naar de WFS om bekendmakingen op te halen want de minimale set
            // param's is aanwezig
            LOGGER.debug("Forwarding naar de WFS bekendmakingen client.");
            final RequestDispatcher rd = this.getServletContext()
                    .getRequestDispatcher("/" + this._BEKENDMAKINGEN);
            rd.forward(request, response);
            return;
        }

        // controle voor OLS param's, als het zoekveld is gevuld adres opzoeken
        final String zoekveld = request.getParameter(REQ_PARAM_ADRES.code);
        if ((zoekveld != null) /* && (zoekveld != "") */) {
            LOGGER.debug("Forwarding naar de OLS client.");
            final RequestDispatcher rd = this.getServletContext()
                    .getRequestDispatcher("/" + this._LOCATIE);
            rd.forward(request, response);
            return;
        }

        // geen van de andere opties was succesvol dus we gaan naar het
        // start/zoek formulier
        LOGGER.debug("Forwarding naar de start pagina.");
        final RequestDispatcher rd = this.getServletContext()
                .getRequestDispatcher("/index.jsp");
        rd.forward(request, response);
    }
}

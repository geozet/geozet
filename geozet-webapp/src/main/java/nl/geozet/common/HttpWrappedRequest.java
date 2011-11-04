package nl.geozet.common;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Klasse HttpWrappedRequest wordt gebruikt om vanuit de ene servlet informatie
 * door te geven aan de volgende via de
 * {@link javax.servlet.http.HttpServletRequest} . Via de constructor kunnen
 * extra of aangepaste parameters worden meegegeven.
 * 
 * @author strampel@atlis.nl
 * @since 1.5
 * @since Servlet API 2.5
 */
public class HttpWrappedRequest extends HttpServletRequestWrapper {

    /** map met de modifiable request parameters. */
    private final Map<String, String[]> modifiableParameters;

    /** map met alle request parameters. */
    private Map<String, String[]> allParameters = null;

    /**
     * Maakt een nieuwe request wrapper waarin additionele request parameters
     * worden samengevoegd met de reeds bestaande.
     * 
     * @param request
     *            oorspronkelijke request object
     * @param additionalParams
     *            additionele request params
     */
    public HttpWrappedRequest(final HttpServletRequest request,
            final Map<String, String[]> additionalParams) {
        super(request);
        this.modifiableParameters = new TreeMap<String, String[]>();
        this.modifiableParameters.putAll(additionalParams);
    }

    /*
     * (non-Javadoc)
     * 
     * Geeft de gevraagde parameter of {@code null} als de parameter onbekend
     * is.
     * 
     * @param name the name
     * 
     * @return the parameter
     * 
     * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(final String name) {
        final String[] strings = this.getParameterMap().get(name);
        if (strings != null) { return strings[0]; }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletRequestWrapper#getParameterMap()
     */
    @SuppressWarnings("unchecked")
    /* servlet api is nog niet generic */
    @Override
    public Map<String, String[]> getParameterMap() {
        if (this.allParameters == null) {
            this.allParameters = new TreeMap<String, String[]>();
            this.allParameters.putAll(super.getParameterMap());
            this.allParameters.putAll(this.modifiableParameters);
        }
        return Collections.unmodifiableMap(this.allParameters);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletRequestWrapper#getParameterNames()
     */
    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.getParameterMap().keySet());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.ServletRequestWrapper#getParameterValues(java.lang.String)
     */
    @Override
    public String[] getParameterValues(final String name) {
        return this.getParameterMap().get(name);
    }

}

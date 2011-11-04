package nl.geozet.openls.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import nl.geozet.common.ProxyConfiguration;
import nl.geozet.openls.databinding.openls.GeocodeRequest;
import nl.geozet.openls.databinding.openls.GeocodeResponse;
import nl.geozet.openls.parser.OpenLSResponseParser;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

/**
 * OpenLSClient.
 * 
 * @author strampel@atlis.nl
 * @author prinsmc@minlnv.nl
 */
public class OpenLSClient {

    /** onze LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(OpenLSClient.class);

    /** The client. */
    private final HttpClient client;

    /** De open ls response parser. */
    private final OpenLSResponseParser openLSResponseParser;

    /**
     * Instantiates a new open ls client.
     */
    public OpenLSClient() {
        this.client = new HttpClient();
        ProxyConfiguration.updateProxyConfig(this.client);
        this.openLSResponseParser = new OpenLSResponseParser();
    }

    /**
     * Do get open ls request.
     * 
     * @param url
     *            the url
     * @param getParams
     *            the get params
     * @return the geocode response, will be null if something went wrong in the
     *         process of getting an openls response and parsing it
     */
    public GeocodeResponse doGetOpenLSRequest(String url,
            Map<String, String> getParams) {
        final String queryString = url.endsWith("?") ? url : url + "?";
        final StringBuilder qs = new StringBuilder(queryString);
        try {
            for (final Entry<String, String> getParam : getParams.entrySet()) {
                qs.append(URLEncoder.encode(getParam.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(getParam.getValue(), "UTF-8"))
                        .append("&");
            }
        } catch (final UnsupportedEncodingException e) {
            LOGGER.fatal("De gebruikte Java VM ondersteunt geen UTF-8 encoding: "
                    + e);
        }
        LOGGER.debug("GETting OLS query:\n\t" + qs.toString());
        final GetMethod getMethod = new GetMethod(qs.toString());
        BufferedReader br = null;
        final StringBuilder sb = new StringBuilder();
        try {
            final int returnCode = this.client.executeMethod(getMethod);
            if (returnCode == HttpStatus.SC_OK) {
                br = new BufferedReader(new InputStreamReader(
                        getMethod.getResponseBodyAsStream()));
                String readLine;
                while (((readLine = br.readLine()) != null)) {
                    sb.append(readLine);
                }
            } else {
                LOGGER.error("OpenLS server get error response: "
                        + getMethod.getResponseBodyAsString());
            }
        } catch (final HttpException e) {
            LOGGER.fatal(
                    "Versturen get request naar OpenLS server is mislukt: ", e);
        } catch (final IOException e) {
            LOGGER.fatal(
                    "Ontvangen get response van OpenLS server is mislukt: ", e);
        } finally {
            getMethod.releaseConnection();
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException fe) {
                    LOGGER.debug(
                            "Fout opgetreden tijdens release van verbinding, "
                                    + "hier is niks meer aan te doen.", fe);
                }
            }
        }
        return this.openLSResponseParser.parseOpenLSResponse(sb.toString());
    }

    /**
     * Do post open ls request.
     * 
     * @param url
     *            the url
     * @param request
     *            the request
     * @return the geocode response, will be null if something went wrong in the
     *         process of getting an openls response and parsing it
     */
    public GeocodeResponse doPostOpenLSRequest(String url,
            GeocodeRequest request) {
        final PostMethod postMethod = new PostMethod(url);
        LOGGER.debug("POSTting OLS query:\n\t" + request.toXML());

        StringRequestEntity str = null;
        try {
            str = new StringRequestEntity(request.toXML(), "text/xml", "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            LOGGER.fatal("De gebruikte Java VM ondersteunt geen UTF-8 encoding: "
                    + e);
        }
        postMethod.setRequestEntity(str);
        final StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            final int returnCode = this.client.executeMethod(postMethod);
            if (returnCode == HttpStatus.SC_OK) {
                br = new BufferedReader(new InputStreamReader(
                        postMethod.getResponseBodyAsStream()));
                String readLine;
                while (((readLine = br.readLine()) != null)) {
                    sb.append(readLine);
                }
            } else {
                LOGGER.error("OpenLS server post error response: "
                        + postMethod.getResponseBodyAsString());
            }
        } catch (final HttpException e) {
            LOGGER.fatal(
                    "Versturen post request naar OpenLS server is mislukt: ", e);
        } catch (final IOException e) {
            LOGGER.fatal(
                    "Ontvangen post response van OpenLS server is mislukt: ", e);
        } finally {
            postMethod.releaseConnection();
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException fe) {
                    LOGGER.debug(
                            "Fout opgetreden tijden release van verbinding, "
                                    + "hier is niks meer aan te doen.", fe);
                }
            }
        }
        return this.openLSResponseParser.parseOpenLSResponse(sb.toString());
    }
}

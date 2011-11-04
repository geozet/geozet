package nl.geozet.common;

import org.apache.commons.httpclient.HttpClient;
import org.apache.log4j.Logger;

/**
 * Een configurator utility klasse die op basis van omgevingsvariabelen http
 * proxy informatie aangeeft. De variabelen zijn: {@code http.proxyPort} en
 * {@code http.proxyHost}.
 * 
 * @author prinsmc@minlnv.nl
 * @since 1.6
 */
public final class ProxyConfiguration {
    /** onze LOGGER. */
    private static final Logger LOGGER = Logger
            .getLogger(ProxyConfiguration.class);

    /** proxy host. {@value} */
    private static final String PHOST = "http.proxyHost";
    /** proxy port. {@value} */
    private static final String PPORT = "http.proxyPort";

    /** private constructor. */
    private ProxyConfiguration() {
        /* utility class heeft een private constructor. */
    }

    /**
     * Update proxy config gebaseerd op de variabelen {@code http.proxyHost} en
     * {@code http.proxyPort} uit de runtime omgeving.
     * 
     * @param client
     *            de client die wordt ge-update
     */
    public static void updateProxyConfig(HttpClient client) {
        final String pHost = System.getProperty(PHOST);
        int pPort = -1;
        try {
            pPort = Integer.valueOf(System.getProperty(PPORT));
        } catch (final NumberFormatException e) {
            LOGGER.debug("Geen proxy poort gedefinieerd.");
        }
        LOGGER.debug("proxy config data (host:port): " + pHost + ":" + pPort);

        if ((null != pHost) && (pPort > 0)) {
            LOGGER.info("Instellen van proxy config: " + pHost + ":" + pPort);
            client.getHostConfiguration().setProxy(pHost, pPort);
        } else {
            LOGGER.info("Er wordt geen proxy ingesteld.");
        }
    }
}

package nl.geozet.common;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * wrapper om de ResourceBundle met applicatie testen.
 * 
 * @author prinsmc@minlnv.nl
 * @since 1.6
 */
public final class CoreResources {
    /** log4j logger. */
    private static final Logger LOGGER = Logger.getLogger(CoreResources.class);
    /** resources. */
    private ResourceBundle resBundle;

    /**
     * Instantiates a new core resources.
     * 
     * @param resBundleName
     *            the res bundle name
     */
    public CoreResources(final String resBundleName) {
        try {
            this.resBundle = ResourceBundle
                    .getBundle(
                            resBundleName,
                            ResourceBundle.Control
                                    .getControl(ResourceBundle.Control.FORMAT_PROPERTIES));
        } catch (final NullPointerException e) {
            LOGGER.fatal("baseName or control is null.", e);
        } catch (final MissingResourceException e) {
            LOGGER.fatal("De resourcebundel (" + resBundleName
                    + ".properties) is niet gevonden", e);
        } catch (final IllegalArgumentException e) {
            // the given control doesn't perform properly (e.g.,
            // control.getCandidateLocales returns null.) Note that validation
            // of control is performed as needed.
            LOGGER.fatal(
                    "ResourceBundle.Control heeft geen locale kunnen achterhalen.",
                    e);
        }
    }

    /**
     * Geeft de string voor de sleutel, de string kan leeg zijn in het geval van
     * ontbreken van de key.
     * 
     * @param key
     *            de sleutel
     * @return de string
     */
    public String getString(final String key) {
        String s = "";
        try {
            s = this.resBundle.getString(key);
        } catch (final MissingResourceException e) {
            LOGGER.error("Er is geen object gevonden voor de gevraagde key ("
                    + key + ").", e);
        } catch (final NullPointerException e) {
            LOGGER.error("Sleutel (" + key + ") is null.", e);
        } catch (final ClassCastException e) {
            LOGGER.error("Het Object voor de gevraagde sleutel  (" + key
                    + ") is geen String.", e);
        }
        return s;
    }
}

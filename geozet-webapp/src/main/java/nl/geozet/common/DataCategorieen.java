package nl.geozet.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Deze klasse levert de thema's of categorieen voro de applicatie. Deze zijn
 * runtime configureerbaar middels de property file
 * {@code core-datacategorieen.properties} zie ook de js pendant
 * {@code Geozet.config.classificationInfo} in settings.js. De volgorde van de
 * elementen wordt bepaald door de volgorde in de property file.
 * 
 * @author prinsmc@minlnv.nl
 * @since 1.6
 */
public final class DataCategorieen { /* implements Enumeration<String[]> */
    /** log4j logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DataCategorieen.class);

    /** de itemList. */
    private static final List<String[]> ITEMLIST = new Vector<String[]>();

    /** de itemKeys. */
    private static final List<String> ITEMKEYS = new Vector<String>();

    /** het pad naar de property file, relatief tav de class resource. */
    private static final String INIT_FILE = "/core-datacategorieen.properties";

    static {
        /**
         * runtime applicatie properties uit de file
         * 'core-datacategorieen.properties' laden.
         */
        final Properties props = new Properties();
        try {
            props.load(DataCategorieen.class.getResourceAsStream(INIT_FILE));
        } catch (final FileNotFoundException e) {
            LOGGER.fatal("Het initialisatie bestand (" + INIT_FILE
                    + ") is niet gevonden.", e);
            throw new ExceptionInInitializerError(e);
        } catch (final IOException e) {
            LOGGER.fatal("I/O fout tijden inlezen initialisatie bestand ("
                    + INIT_FILE + ").", e);
            throw new ExceptionInInitializerError(e);
        }
        // process properties content
        final String csvString = props.getProperty("categorieen");
        LOGGER.debug(csvString);
        final String[] result = csvString.split(";");
        for (final String element : result) {
            ITEMLIST.add(element.split(":"));
            ITEMKEYS.add(element.split(":")[0]);
        }
    }

    /**
     * Instantiates a new data categorieen, Private constructor voor deze
     * utility klasse.
     */
    private DataCategorieen() {
        /* Private constructor voor deze utility klasse. */
    }

    /**
     * Geeft de elementen in deze verzameling. Een element bestaat uit een
     * {@code String[3] [key,class,desc]}
     * 
     * @return de {@code List<String[]>} met [sleutel,klasse,beschrijving]
     */
    public static List<String[]> elements() {
        return Collections.unmodifiableList((ITEMLIST));
    }

    /**
     * geeft de sleutels in deze verzameling.
     * 
     * @return de {@code List<String>} met sleutels
     */
    public static List<String> keys() {
        return Collections.unmodifiableList((ITEMKEYS));
    }

    /**
     * geeft de sleutels van deze verzameling in een array.
     * 
     * @return het {@code String[]} met sleutels
     */
    public static String[] keysAsArray() {
        return keys().toArray(new String[] {});
    }
}

package nl.geozet.common;

import static nl.geozet.common.StringConstants.AFSTAND_NAAM;

import java.io.Serializable;
import java.util.Comparator;

import org.opengis.feature.simple.SimpleFeature;

/**
 * AfstandComparator wordt gebruikt voor het vergelijken van de afstand naar een
 * vast punt (de zoeklocatie) tussen twee
 * {@link org.opengis.feature.simple.SimpleFeature SimpleFeature}'s, de afstand
 * is opgeslagen in de
 * {@linkplain org.opengis.feature.simple.SimpleFeature#getUserData()} van de
 * Feature.
 * 
 * @see org.opengis.feature.simple.SimpleFeature
 * @author prinsmc@minlnv.nl
 * @since GeoTools 2.7
 */
public class AfstandComparator implements Comparator<SimpleFeature>,
        Serializable {

    /** default serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Vergelijkt de afstand van o1 kleiner is dan die van o2. De aftstand is
     * een {@link org.opengis.feature.simple.SimpleFeature#getUserData()
     * UserData} attribuut van de SimpleFeature.
     * 
     * @param o1
     *            de te vergelijken SimpleFeature
     * @param o2
     *            de vergeleken SimpleFeature
     * @return 1 als die afstand groter is en 0 als de afstand gelijk is.
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     * @see org.opengis.feature.simple.SimpleFeature#getUserData()
     */
    @Override
    public final int compare(SimpleFeature o1, SimpleFeature o2) {
        return Double.compare((Double) o1.getUserData().get(AFSTAND_NAAM),
                (Double) o2.getUserData().get(AFSTAND_NAAM));
    }
}

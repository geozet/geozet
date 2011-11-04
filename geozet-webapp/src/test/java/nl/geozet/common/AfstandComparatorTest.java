package nl.geozet.common;

import static nl.geozet.common.StringConstants.AFSTAND_NAAM;
import junit.framework.TestCase;

import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Testcase voor {@link AfstandComparator}.
 */
public class AfstandComparatorTest extends TestCase {

    /** testfeatures. */
    private SimpleFeature f1, f2, f3;

    /** feature builder. */
    private SimpleFeatureBuilder sfBuilder;

    /**
     * Instantiates a new afstand comparator test.
     * 
     * @throws SchemaException
     *             the schema exception
     */
    public AfstandComparatorTest() throws SchemaException {
        SimpleFeatureType type = DataUtilities.createType("location",
                "Location:Point,Id:Integer,Name:String");
        sfBuilder = new SimpleFeatureBuilder(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    @Before
    protected void setUp() throws Exception {
        f1 = sfBuilder.buildFeature("f1");
        f2 = sfBuilder.buildFeature("f2");
        f3 = sfBuilder.buildFeature("f3");
        f1.getUserData().put(AFSTAND_NAAM, 1000d);
        f2.getUserData().put(AFSTAND_NAAM, 1500d);
        f3.getUserData().put(AFSTAND_NAAM, 1000d);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    @After
    protected void tearDown() throws Exception {
        sfBuilder.reset();
    }

    /**
     * Test methode voor
     * {@link AfstandComparator#compare(SimpleFeature, SimpleFeature)}.
     */
    @Test
    public void testCompare() {
        AfstandComparator c = new AfstandComparator();
        assertEquals(-1, c.compare(f1, f2));
        assertEquals(1, c.compare(f2, f3));
        assertEquals(0, c.compare(f1, f3));
    }

}

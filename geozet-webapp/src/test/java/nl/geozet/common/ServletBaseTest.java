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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author xp_prinsmc
 */
@RunWith(JMock.class)
public class ServletBaseTest {
    /** servlet die we testen. */
    private ServletBase baseServlet;
    /** servlet die we testen. */
    private ServletBase baseServlet2;
    /** junit mockery. */
    private final Mockery mockery = new JUnit4Mockery();
    /** ge-mockte servlet request gebruikt in de test. */
    private HttpServletRequest request;
    /** ge-mockte servlet request gebruikt in de test. */
    private HttpServletResponse response;
    /** ge-mockte servlet context gebruikt in de test. */
    private ServletContext servletContext;
    /** ge-mockte servlet config gebruikt in de test. */
    private ServletConfig servletConfig;
    /** waarde van het config item. */
    private static final int _itemsPerPagina = 10;

    private HashMap<String, String[]> requestMap = null;
    private static final String ADRES = "Baarn (gemeente)";
    private static final String STRAAL = "1500";
    private static final String YCOORD = "469199.0";
    private static final String XCOORD = "148082.0";

    /**
     * @throws java.lang.Exception
     */
    @SuppressWarnings("serial")
    @Before
    public void setUp() throws Exception {
        this.servletConfig = this.mockery.mock(ServletConfig.class);
        this.servletContext = this.mockery.mock(ServletContext.class);
        this.request = this.mockery.mock(HttpServletRequest.class);
        this.response = this.mockery.mock(HttpServletResponse.class);

        // set up mock context voor een juist geconfigureerde context
        this.mockery.checking(new Expectations() {
            {
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(CONFIG_PARAM_PAGINALENGTE.code);
                this.will(returnValue(_itemsPerPagina + "" /*
                                                            * moet een string
                                                            * zijn
                                                            */));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(CONFIG_PARAM_RESOURCENAME.code);
                this.will(returnValue("core-resources"));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(
                                CONFIG_PARAM_BEKENDMAKINGENSERVLET.code);
                this.will(returnValue("bekendmakingen"));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(CONFIG_PARAM_BEKENDMAKINGSERVLET.code);
                this.will(returnValue("bekendmaking"));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(CONFIG_PARAM_LOCATIESERVLET.code);
                this.will(returnValue("locatie"));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(CONFIG_PARAM_GEOZETSERVLET.code);
                this.will(returnValue("geozet"));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(
                                CONFIG_PARAM_VLAKBEKENDMAKINGSERVLET.code);
                this.will(returnValue("vlakbekendmakingen"));
            }
        });

        // this.allowing(request.getServletPath());
        // this.will(returnValue("servletpad"));

        // Override getServletConfig/getServletContext to return mocked
        // config/context
        this.baseServlet = new ServletBase() {
            /** return de mocked servletConfig. */
            @Override
            public ServletConfig getServletConfig() {
                return ServletBaseTest.this.servletConfig;
            }

            /** return de mocked servletContext. */
            @Override
            public ServletContext getServletContext() {
                return ServletBaseTest.this.servletContext;
            }
        };
        this.requestMap = new HashMap<String, String[]>();
        this.requestMap.put("ycoord", new String[] { YCOORD });
        this.requestMap.put("xcoord", new String[] { XCOORD });
        this.requestMap.put("straal", new String[] { STRAAL });
        this.requestMap.put("adres", new String[] { ADRES });
        this.requestMap.put("gevonden", new String[] { ADRES });
    }

    /**
     * Test method for
     * {@link nl.geozet.common.ServletBase#init(javax.servlet.ServletConfig)}.
     */
    @SuppressWarnings("serial")
    @Test
    public final void testInitServletConfig() {
        try {
            this.baseServlet.init(this.servletConfig);
            assertEquals(_itemsPerPagina, this.baseServlet.itemsPerPage);
        } catch (final ServletException e) {
            fail("Servlet Exception voor init() in test setup. "
                    + e.getLocalizedMessage());
        }

        // set up mock context voor test van missende config optie
        this.mockery.checking(new Expectations() {
            {
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(CONFIG_PARAM_PAGINALENGTE.code);
                this.will(returnValue(null /* moet een string zijn */));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(CONFIG_PARAM_RESOURCENAME.code);
                this.will(returnValue("core-resources"));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(
                                CONFIG_PARAM_BEKENDMAKINGENSERVLET.code);
                this.will(returnValue("bekendmakingen"));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(CONFIG_PARAM_BEKENDMAKINGSERVLET.code);
                this.will(returnValue("bekendmaking"));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(CONFIG_PARAM_LOCATIESERVLET.code);
                this.will(returnValue("locatie"));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(CONFIG_PARAM_GEOZETSERVLET.code);
                this.will(returnValue("geozet"));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(
                                CONFIG_PARAM_VLAKBEKENDMAKINGSERVLET.code);
                this.will(returnValue("vlakbekendmakingen"));
            }
        });
        // Override getServletConfig/getServletContext to return the mocked
        // config/context
        this.baseServlet2 = new ServletBase() {
            /** return de mocked servletConfig. */
            @Override
            public ServletConfig getServletConfig() {
                return ServletBaseTest.this.servletConfig;
            }

            /** return de mocked servletContext. */
            @Override
            public ServletContext getServletContext() {
                return ServletBaseTest.this.servletContext;
            }
        };
        try {
            this.baseServlet2.init(this.servletConfig);
            assertEquals(DEFAULT_ITEMS_PER_PAGINA.intValue(),
                    this.baseServlet2.itemsPerPage);
        } catch (final ServletException e) {
            fail("Servlet Exception voor init() in test setup. "
                    + e.getLocalizedMessage());
        }

    }

    /*
     * 
     * <ul class=\"geozetPaging\"><li class=\"prev\"><a
     * href=\"#\">Vorige</a></li> <li><a href=\"pad/naar/1\">1</a></li>
     * <li>...</li> <li><a href=\"#\">nr. huidige pagina - 2</a></li> <li><a
     * href=\"#\">nr. huidige pagina - 1</a></li> <li class=\"active\">nr.
     * huidige pagina</li> <li><a href=\"#\">nr. huidige pagina + 1</a></li>
     * <li><a href=\"#\">nr. huidige pagina + 2</a></li> <li>...</li> <li><a
     * href=\"pad/naar/max\">#max</a></li> <li class=\"next\"><a
     * href=\"#\">Volgende</a></li></ul>
     */

    /**
     * Test method for
     * {@link nl.geozet.common.ServletBase#buildQueryString(javax.servlet.http.HttpServletRequest, java.lang.String)}
     * .
     */
    @Test
    public final void testBuildQueryString() {
        final String expected = "&amp;ycoord=" + YCOORD + "&amp;xcoord="
                + XCOORD + "&amp;adres=" + ADRES + "&amp;straal=" + STRAAL
                + "&amp;gevonden=" + ADRES + "";
        final String excludeparam = "offset";
        this.requestMap.put("offset", new String[] { excludeparam });

        // set up mock request
        this.mockery.checking(new Expectations() {
            {
                this.oneOf(ServletBaseTest.this.request).getParameterMap();
                this.will(returnValue(ServletBaseTest.this.requestMap));
            }
        });
        try {
            this.baseServlet.init(this.servletConfig);
        } catch (final ServletException e) {
            fail("Servlet Exception voor init() in test setup. "
                    + e.getLocalizedMessage());
        }
        final String actual = this.baseServlet.buildQueryString(this.request,
                excludeparam);
        assertEquals(expected, actual);
    }

    /**
     * Test method for
     * {@link nl.geozet.common.ServletBase#init(javax.servlet.ServletConfig)}.
     */
    @SuppressWarnings("serial")
    @Test
    public final void testInitServletConfigContextFout() {
        try {
            this.baseServlet.init(this.servletConfig);
            assertEquals(_itemsPerPagina, this.baseServlet.itemsPerPage);
        } catch (final ServletException e) {
            fail("Servlet Exception voor init() in test setup. "
                    + e.getLocalizedMessage());
        }

        // set up mock context voor test van missende config optie
        this.mockery.checking(new Expectations() {
            {
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(CONFIG_PARAM_PAGINALENGTE.code);
                this.will(returnValue(null /* moet een string zijn */));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(CONFIG_PARAM_RESOURCENAME.code);
                this.will(returnValue("core-resources"));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(
                                CONFIG_PARAM_BEKENDMAKINGENSERVLET.code);
                this.will(returnValue("bekendmakingen"));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(CONFIG_PARAM_BEKENDMAKINGSERVLET.code);
                this.will(returnValue("bekendmaking"));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(CONFIG_PARAM_LOCATIESERVLET.code);
                this.will(returnValue("locatie"));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(CONFIG_PARAM_GEOZETSERVLET.code);
                this.will(returnValue(""));
                this.oneOf(ServletBaseTest.this.servletContext)
                        .getInitParameter(
                                CONFIG_PARAM_VLAKBEKENDMAKINGSERVLET.code);
                this.will(returnValue("vlakbekendmakingen"));
            }
        });
        // Override getServletConfig/getServletContext to return the mocked
        // config/context
        this.baseServlet2 = new ServletBase() {
            /** return de mocked servletConfig. */
            @Override
            public ServletConfig getServletConfig() {
                return ServletBaseTest.this.servletConfig;
            }

            /** return de mocked servletContext. */
            @Override
            public ServletContext getServletContext() {
                return ServletBaseTest.this.servletContext;
            }
        };
        try {
            this.baseServlet2.init(this.servletConfig);
            assertEquals(DEFAULT_ITEMS_PER_PAGINA.intValue(),
                    this.baseServlet2.itemsPerPage);
        } catch (final ServletException e) {
            assertTrue(e.getMessage().length() > 1);
            fail("Servlet Exception voor init() in test setup. "
                    + e.getLocalizedMessage());
        }

    }

    /**
     * Test method for
     * {@link nl.geozet.common.ServletBase#buildPageList(int, javax.servlet.http.HttpServletRequest)}
     * test het geval waar de offset=0 (eerste pagina)en het aantal items=102
     * met itemsperpagina=10.
     */
    @Test
    public final void testBuildPageListPage1() {
        final String expected = "<ul class=\"geozetPaging\"><li class=\"active\"><strong>1</strong></li><li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=10\">2</a></li>"
                + "<li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=20\">3</a></li>"
                + "<li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=30\">4</a></li>"
                + "<li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=40\">5</a></li>"
                + "<li>...</li><li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=100\">11</a></li><li class=\"next\"><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=10\">Volgende</a></li></ul>";
        final int numberofItems = 102;
        final String page3Offset = "0";
        this.requestMap.put("offset", new String[] { page3Offset });

        // set up mock request
        this.mockery.checking(new Expectations() {
            {
                this.oneOf(ServletBaseTest.this.request).getParameterMap();
                this.will(returnValue(ServletBaseTest.this.requestMap));
                this.oneOf(ServletBaseTest.this.request).getParameter(
                        REQ_PARAM_PAGEOFFSET.code);
                this.will(returnValue(page3Offset));
                this.oneOf(ServletBaseTest.this.request).getServletPath();
                this.will(returnValue("bekendmakingen"));
            }
        });
        try {
            this.baseServlet.init(this.servletConfig);
        } catch (final ServletException e) {
            fail("Servlet Exception voor init() in test setup. "
                    + e.getLocalizedMessage());
        }
        final String actual = this.baseServlet.buildPageList(numberofItems,
                this.request);
        assertEquals(expected, actual);
    }

    /**
     * Test method for
     * {@link nl.geozet.common.ServletBase#buildPageList(int, javax.servlet.http.HttpServletRequest)}
     * test het geval waar offset=20 (derde pagina)en het aantal items=102 met
     * itemsperpagina=10 .
     */
    @Test
    public final void testBuildPageListPage3() {
        final String expected = "<ul class=\"geozetPaging\"><li class=\"prev first\"><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=10\">Vorige</a></li><li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=0\">1</a></li><li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=10\">2</a></li><li class=\"active\"><strong>3</strong></li><li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=30\">4</a></li><li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=40\">5</a></li><li>...</li><li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=100\">11</a></li><li class=\"next\"><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=30\">Volgende</a></li></ul>";
        final String page3Offset = "20";
        this.requestMap.put("offset", new String[] { page3Offset });

        // set up mock request
        this.mockery.checking(new Expectations() {
            {
                this.oneOf(ServletBaseTest.this.request).getParameterMap();
                this.will(returnValue(ServletBaseTest.this.requestMap));
                this.oneOf(ServletBaseTest.this.request).getParameter(
                        REQ_PARAM_PAGEOFFSET.code);
                this.will(returnValue(page3Offset));
                this.oneOf(ServletBaseTest.this.request).getServletPath();
                this.will(returnValue("bekendmakingen"));
            }
        });
        try {
            this.baseServlet.init(this.servletConfig);
        } catch (final ServletException e) {
            fail("Servlet Exception voor init() in test setup. "
                    + e.getLocalizedMessage());
        }
        final String actual = this.baseServlet.buildPageList(102, this.request);
        assertEquals(expected, actual);
    }

    /**
     * Test method for
     * {@link nl.geozet.common.ServletBase#buildPageList(int, javax.servlet.http.HttpServletRequest)}
     * test het geval waar offset=null (eerste pagina) en het aantal items=102
     * met itemsperpagina=10.
     */
    @Test
    public final void testBuildPageListDefault() {
        final String expected = "<ul class=\"geozetPaging\"><li class=\"active\"><strong>1</strong></li><li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=10\">2</a></li><li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=20\">3</a></li><li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=30\">4</a></li><li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=40\">5</a></li><li>...</li><li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=100\">11</a></li><li class=\"next\"><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=10\">Volgende</a></li></ul>";
        final String page3Offset = null;
        this.requestMap.put("offset", new String[] { page3Offset });

        // set up mock request
        this.mockery.checking(new Expectations() {
            {
                this.oneOf(ServletBaseTest.this.request).getParameterMap();
                this.will(returnValue(ServletBaseTest.this.requestMap));
                this.oneOf(ServletBaseTest.this.request).getParameter(
                        REQ_PARAM_PAGEOFFSET.code);
                this.will(returnValue(page3Offset));
                this.oneOf(ServletBaseTest.this.request).getServletPath();
                this.will(returnValue("bekendmakingen"));
            }
        });
        try {
            this.baseServlet.init(this.servletConfig);
        } catch (final ServletException e) {
            fail("Servlet Exception voor init() in test setup. "
                    + e.getLocalizedMessage());
        }
        final String actual = this.baseServlet.buildPageList(102, this.request);
        assertEquals(expected, actual);
    }

    /**
     * Test method for
     * {@link nl.geozet.common.ServletBase#buildPageList(int, javax.servlet.http.HttpServletRequest)}
     * Test het geval waar geen paginering moet worden gegeven omdat het aantal
     * items eentje minder is als het aantal items per pagina. Test het geval
     * waar geen paginering moet worden gegeven omdat het aantal items hetzelfde
     * is als het aantal items per pagina. Test het geval waar twee pagina's
     * gemaakt moeten worden omdat het aantal items 1 meer is dan het aantal
     * items per pagina.
     */
    @Test
    public final void testBuildPageListNoPage() {
        String expected;
        String actual;
        final String pageOffset = null;
        this.requestMap.put("offset", new String[] { pageOffset });

        // set up mock request
        this.mockery.checking(new Expectations() {
            {
                this.allowing(ServletBaseTest.this.request).getParameter(
                        REQ_PARAM_PAGEOFFSET.code);
                this.will(returnValue(pageOffset));
                this.allowing(ServletBaseTest.this.request).getParameterMap();
                this.will(returnValue(ServletBaseTest.this.requestMap));
                this.oneOf(ServletBaseTest.this.request).getServletPath();
                this.will(returnValue("bekendmakingen"));
            }
        });
        try {
            this.baseServlet.init(this.servletConfig);
        } catch (final ServletException e) {
            fail("Servlet Exception voor init() in test setup. "
                    + e.getLocalizedMessage());
        }
        // 1 minder dan items op een pagina
        actual = this.baseServlet.buildPageList(_itemsPerPagina - 1,
                this.request);
        expected = "";
        assertEquals(expected, actual);
        // evenveel als items op een pagina
        expected = "";
        actual = this.baseServlet.buildPageList(_itemsPerPagina, this.request);
        assertEquals(expected, actual);
        // 1 meer als items op een pagina
        actual = this.baseServlet.buildPageList(_itemsPerPagina + 1,
                this.request);
        expected = "<ul class=\"geozetPaging\"><li class=\"active\"><strong>1</strong></li><li><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=10\">2</a></li><li class=\"next\"><a href=\"bekendmakingen?&amp;ycoord="
                + YCOORD
                + "&amp;xcoord="
                + XCOORD
                + "&amp;adres="
                + ADRES
                + "&amp;straal="
                + STRAAL
                + "&amp;gevonden="
                + ADRES
                + "&amp;offset=10\">Volgende</a></li></ul>";
    }

    /**
     * Test method for
     * {@link nl.geozet.common.ServletBase#doDelete(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    public final void testDoDelete() {
        try {
            this.baseServlet.init(this.servletConfig);
            this.baseServlet.doDelete(request, response);
            fail("Overwachte ondersteuning voor deze methode (DELETE).");
        } catch (ServletException e) {
            // verwacht
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("DELETE"));
        } catch (IOException e) {
            // niet verwacht
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link nl.geozet.common.ServletBase#doHead(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    public final void testDoHead() {
        try {
            this.baseServlet.init(this.servletConfig);
            this.baseServlet.doHead(request, response);
            fail("Overwachte ondersteuning voor deze methode (HEAD).");
        } catch (ServletException e) {
            // verwacht
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("HEAD"));
        } catch (IOException e) {
            // niet verwacht
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link nl.geozet.common.ServletBase#doPut(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    public final void testDoPut() {
        try {
            this.baseServlet.init(this.servletConfig);
            this.baseServlet.doPut(request, response);
            fail("Overwachte ondersteuning voor deze methode (PUT).");
        } catch (ServletException e) {
            // verwacht
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("PUT"));
        } catch (IOException e) {
            // niet verwacht
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link nl.geozet.common.ServletBase#doTrace(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    public final void testDoTrace() {
        try {
            this.baseServlet.init(this.servletConfig);
            this.baseServlet.doTrace(request, response);
            fail("Overwachte ondersteuning voor deze methode (TRACE).");
        } catch (ServletException e) {
            // verwacht
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("TRACE"));
        } catch (IOException e) {
            // niet verwacht
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link nl.geozet.common.ServletBase#doOptions(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    public final void testDoOptions() {
        try {
            this.baseServlet.init(this.servletConfig);
            this.baseServlet.doOptions(request, response);
            fail("Overwachte ondersteuning voor deze methode (OPTIONS).");
        } catch (ServletException e) {
            // verwacht
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("OPTIONS"));
        } catch (IOException e) {
            // niet verwacht
            fail(e.getMessage());
        }
    }
}

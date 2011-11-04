package nl.geozet.openls.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nl.geozet.openls.databinding.gml.Point;
import nl.geozet.openls.databinding.gml.Pos;
import nl.geozet.openls.databinding.openls.Address;
import nl.geozet.openls.databinding.openls.Building;
import nl.geozet.openls.databinding.openls.GeocodeResponse;
import nl.geozet.openls.databinding.openls.GeocodeResponseList;
import nl.geozet.openls.databinding.openls.GeocodedAddress;
import nl.geozet.openls.databinding.openls.Place;
import nl.geozet.openls.databinding.openls.PostalCode;
import nl.geozet.openls.databinding.openls.Street;
import nl.geozet.openls.databinding.openls.StreetAddress;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The Class OpenLSResponseParser.
 */
public class OpenLSResponseParser extends DefaultHandler {

    /** onze LOGGER. */
    private static final Logger LOGGER = Logger
            .getLogger(OpenLSResponseParser.class);

    /** SAX parser. */
    private SAXParser parser;

    /** object stack. */

    private Stack objStack = new Stack();

    /** The e val buf. */
    private StringBuffer eValBuf;

    /**
     * Instantiates a new open ls response parser.
     */
    public OpenLSResponseParser() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        try {
            parser = factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            LOGGER.fatal("Configureren van de saxparser is mislukt: ", e);
        } catch (SAXException e) {
            LOGGER.error("Maken van de saxparser is mislukt: ", e);
        }
    }

    /**
     * Parses the open ls response.
     * 
     * @param data
     *            the data which is an OpenLS response xml document
     * @return the geocode response object, will return null if parsing the data
     *         failed
     */
    public GeocodeResponse parseOpenLSResponse(String data) {
        objStack.clear();
        try {
            parser.parse(new InputSource(new StringReader(data)), this);
        } catch (SAXException e) {
            LOGGER.error("OpenLS response XML verwerken is mislukt: " + data
                    + ": ", e);
        } catch (IOException e) {
            LOGGER.error("OpenLS response XML lezen is mislukt: ", e);
        }
        return getGeocodeResponse();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        eValBuf = new StringBuffer();
        String[] nsName = qName.split(":");
        String eName = "";
        if (nsName.length > 1) {
            eName = nsName[1];
        } else {
            eName = nsName[0];
        }
        if (eName.equalsIgnoreCase("GeocodeResponse")) {
            GeocodeResponse obj = new GeocodeResponse();
            objStack.push(obj);
        } else if (eName.equalsIgnoreCase("GeocodeResponseList")) {
            GeocodeResponseList obj = new GeocodeResponseList();
            objStack.push(obj);
            for (int i = 0; i < attributes.getLength(); i++) {
                String key = attributes.getQName(i);
                String value = attributes.getValue(i);
                if (key.equalsIgnoreCase("numberOfGeocodedAddresses")) {
                    int val = Integer.parseInt(value);
                    obj.setNumberOfGeocodedAddresses(val);
                }
            }
        } else if (eName.equalsIgnoreCase("GeocodedAddress")) {
            GeocodedAddress obj = new GeocodedAddress();
            objStack.push(obj);
        } else if (eName.equalsIgnoreCase("Point")) {
            Point obj = new Point();
            objStack.push(obj);
            for (int i = 0; i < attributes.getLength(); i++) {
                String key = attributes.getQName(i);
                String value = attributes.getValue(i);
                if (key.equalsIgnoreCase("srsName")) {
                    obj.setSrsName(value);
                }
            }
        } else if (eName.equalsIgnoreCase("pos")) {
            Pos obj = new Pos();
            objStack.push(obj);
            for (int i = 0; i < attributes.getLength(); i++) {
                String key = attributes.getQName(i);
                String value = attributes.getValue(i);
                if (key.equalsIgnoreCase("dimension")) {
                    obj.setDimension(Integer.parseInt(value));
                }
            }
        } else if (eName.equalsIgnoreCase("Address")) {
            Address obj = new Address();
            objStack.push(obj);
            for (int i = 0; i < attributes.getLength(); i++) {
                String key = attributes.getQName(i);
                String value = attributes.getValue(i);
                if (key.equalsIgnoreCase("countryCode")) {
                    obj.setCountryCode(value);
                }
            }
        } else if (eName.equalsIgnoreCase("StreetAddress")) {
            StreetAddress obj = new StreetAddress();
            objStack.push(obj);
        } else if (eName.equalsIgnoreCase("Building")) {
            Building obj = new Building();
            objStack.push(obj);
            for (int i = 0; i < attributes.getLength(); i++) {
                String key = attributes.getQName(i);
                String value = attributes.getValue(i);
                if (key.equalsIgnoreCase("number")) {
                    obj.setNumber(value);
                }
            }
        } else if (eName.equalsIgnoreCase("Street")) {
            Street obj = new Street();
            objStack.push(obj);
        } else if (eName.equalsIgnoreCase("Place")) {
            Place obj = new Place();
            objStack.push(obj);
            for (int i = 0; i < attributes.getLength(); i++) {
                String key = attributes.getQName(i);
                String value = attributes.getValue(i);
                if (key.equalsIgnoreCase("type")) {
                    obj.setType(value);
                }
            }
        } else if (eName.equalsIgnoreCase("PostalCode")) {
            PostalCode obj = new PostalCode();
            objStack.push(obj);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        String[] nsName = qName.split(":");
        String eName = "";
        if (nsName.length > 1) {
            eName = nsName[1];
        } else {
            eName = nsName[0];
        }
        if (eName.equalsIgnoreCase("GeocodeResponseList")) {
            GeocodeResponseList obj = (GeocodeResponseList) (objStack.pop());
            if (objStack.peek().getClass() == new GeocodeResponse().getClass()) {
                ((GeocodeResponse) (objStack.peek()))
                        .addGeocodeResponseList(obj);
            }
        } else if (eName.equalsIgnoreCase("GeocodedAddress")) {
            GeocodedAddress obj = (GeocodedAddress) (objStack.pop());
            if (objStack.peek().getClass() == new GeocodeResponseList()
                    .getClass()) {
                ((GeocodeResponseList) (objStack.peek()))
                        .addGeocodedAddress(obj);
            }
        } else if (eName.equalsIgnoreCase("Point")) {
            Point obj = (Point) (objStack.pop());
            if (objStack.peek().getClass() == new GeocodedAddress().getClass()) {
                ((GeocodedAddress) (objStack.peek())).setPoint(obj);
            }
        } else if (eName.equalsIgnoreCase("pos")) {
            Pos obj = (Pos) (objStack.pop());
            obj.setXY(eValBuf.toString());
            if (objStack.peek().getClass() == new Point().getClass()) {
                ((Point) (objStack.peek())).addPos(obj);
            }
        } else if (eName.equalsIgnoreCase("Address")) {
            Address obj = (Address) (objStack.pop());
            if (objStack.peek().getClass() == new GeocodedAddress().getClass()) {
                ((GeocodedAddress) (objStack.peek())).setAddress(obj);
            }
        } else if (eName.equalsIgnoreCase("StreetAddress")) {
            StreetAddress obj = (StreetAddress) (objStack.pop());
            if (objStack.peek().getClass() == new Address().getClass()) {
                ((Address) (objStack.peek())).setStreetAddress(obj);
            }
        } else if (eName.equalsIgnoreCase("Building")) {
            Building obj = (Building) (objStack.pop());
            if (objStack.peek().getClass() == new StreetAddress().getClass()) {
                ((StreetAddress) (objStack.peek())).setBuilding(obj);
            }
        } else if (eName.equalsIgnoreCase("Street")) {
            Street obj = (Street) (objStack.pop());
            obj.setStreet(eValBuf.toString());
            if (objStack.peek().getClass() == new StreetAddress().getClass()) {
                ((StreetAddress) (objStack.peek())).setStreet(obj);
            }
        } else if (eName.equalsIgnoreCase("Place")) {
            Place obj = (Place) (objStack.pop());
            obj.setPlace(eValBuf.toString());
            if (objStack.peek().getClass() == new Address().getClass()) {
                ((Address) (objStack.peek())).addPlace(obj);
            }
        } else if (eName.equalsIgnoreCase("PostalCode")) {
            PostalCode obj = (PostalCode) (objStack.pop());
            obj.setPostalCode(eValBuf.toString());
            if (objStack.peek().getClass() == new Address().getClass()) {
                ((Address) (objStack.peek())).setPostalCode(obj);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        eValBuf.append(ch, start, length);
    }

    /**
     * Gets the geocode response.
     * 
     * @return the geocode response
     */
    public GeocodeResponse getGeocodeResponse() {
        GeocodeResponse geocodeResponse = null;
        if (objStack.firstElement() != null) {
            if (objStack.firstElement().getClass() == new GeocodeResponse()
                    .getClass()) {
                geocodeResponse = (GeocodeResponse) objStack.firstElement();
            }
        }
        return geocodeResponse;
    }

}

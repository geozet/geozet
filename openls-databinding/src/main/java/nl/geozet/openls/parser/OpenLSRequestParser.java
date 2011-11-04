package nl.geozet.openls.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nl.geozet.openls.databinding.openls.Address;
import nl.geozet.openls.databinding.openls.Building;
import nl.geozet.openls.databinding.openls.GeocodeRequest;
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
 * The Class OpenLSRequestParser.
 */
public class OpenLSRequestParser extends DefaultHandler {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger
            .getLogger(OpenLSRequestParser.class);

    /** The parser. */
    private SAXParser parser;

    /** The obj stack. */
    private Stack objStack = new Stack();

    /** The e val buf. */
    private StringBuffer eValBuf;

    /**
     * Instantiates a new open ls request parser.
     */
    public OpenLSRequestParser() {
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
     * Parses the open ls request.
     * 
     * @param data
     *            the data
     * @return the geocode request
     */
    public GeocodeRequest parseOpenLSRequest(String data) {
        objStack.clear();
        try {
            parser.parse(new InputSource(new StringReader(data)), this);
        } catch (SAXException e) {
            LOGGER.error("OpenLS response XML verwerken is mislukt: " + data
                    + ": ", e);
        } catch (IOException e) {
            LOGGER.error("OpenLS response XML lezen is mislukt: ", e);
        }
        return getGeocodeRequest();
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
        if (eName.equalsIgnoreCase("GeocodeRequest")) {
            GeocodeRequest obj = new GeocodeRequest();
            objStack.push(obj);
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
        if (eName.equalsIgnoreCase("Address")) {
            Address obj = (Address) (objStack.pop());
            if (objStack.peek().getClass() == new GeocodeRequest().getClass()) {
                ((GeocodeRequest) (objStack.peek())).addAddress(obj);
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
     * Gets the geocode request.
     * 
     * @return the geocode request
     */
    public GeocodeRequest getGeocodeRequest() {
        GeocodeRequest geocodeRequest = null;
        if (objStack.firstElement() != null) {
            if (objStack.firstElement().getClass() == new GeocodeRequest()
                    .getClass()) {
                geocodeRequest = (GeocodeRequest) objStack.firstElement();
            }
        }
        return geocodeRequest;
    }
}

package nl.geozet.openls;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import nl.geozet.openls.databinding.gml.Point;
import nl.geozet.openls.databinding.gml.Pos;
import nl.geozet.openls.databinding.openls.Address;
import nl.geozet.openls.databinding.openls.Building;
import nl.geozet.openls.databinding.openls.GeocodeResponse;
import nl.geozet.openls.databinding.openls.GeocodeResponseList;
import nl.geozet.openls.databinding.openls.GeocodedAddress;
import nl.geozet.openls.databinding.openls.OpenLSConstants;
import nl.geozet.openls.databinding.openls.Place;
import nl.geozet.openls.databinding.openls.PostalCode;
import nl.geozet.openls.databinding.openls.Street;
import nl.geozet.openls.databinding.openls.StreetAddress;
import nl.geozet.openls.parser.OpenLSResponseParser;

import org.junit.Test;

/**
 * The Class OpenLSResponseParserTest.
 */
public class OpenLSResponseParserTest extends TestCase {

    /**
     * Test open ls request parser. Iterate through the sample openls response
     * files and try to extract a GeocodeResponse from them.
     * 
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    public void testOpenLSResponseParser() throws java.io.IOException {
        OpenLSResponseParser rp = new OpenLSResponseParser();
        File folder = new File("./target/test-classes/sampleresponses/");
        List<File> fileList = new ArrayList<File>();
        listDirectoryFilenames(folder, fileList);
        java.util.Iterator<File> fileIt = fileList.iterator();
        while (fileIt.hasNext()) {
            String fileName = "/sampleresponses/" + fileIt.next().getName();
            String responseString = readFileAsString(fileName);
            GeocodeResponse gcr = rp.parseOpenLSResponse(responseString);
            if (gcr != null) {
                System.out.println(gcr.toXML());
            }
            assertNotNull(gcr);
        }
    }

    /**
     * Test open ls response roundtrip. 1: create an openls response 2:
     * serialize it to xml string 3: use the response parser to deserialize the
     * xml to a new openls response object 4: serialize the new openls object to
     * xml string 5: check if the first xml string is the same as the second xml
     * string
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    public void testOpenLSResponseRoundtrip() throws java.io.IOException {
        GeocodeResponse gcr = new GeocodeResponse();

        GeocodeResponseList gcrl = new GeocodeResponseList();
        gcrl.setNumberOfGeocodedAddresses(1);

        GeocodedAddress gca = new GeocodedAddress();

        Address address = new Address();

        StreetAddress sa = new StreetAddress();

        Building building = new Building();
        building.setNumber("100");

        Place p = new Place();
        p.setType(OpenLSConstants.PLACE_TYPE_MUNICIPALITY);
        p.setPlace("Utrecht");

        PostalCode pc = new PostalCode();
        pc.setPostalCode("1234HH");

        Street street = new Street();
        street.setStreet("Kosterijland 78");

        Point point = new Point();
        point.setSrsName("EPSG:28992");

        Pos pos = new Pos();
        pos.setX(new Double(1234));
        pos.setY(new Double(5678));

        point.addPos(pos);
        sa.setStreet(street);
        sa.setBuilding(building);
        address.setPostalCode(pc);
        address.setCountryCode("NL");
        address.addPlace(p);
        address.setStreetAddress(sa);
        gca.setAddress(address);
        gca.setPoint(point);
        gcrl.addGeocodedAddress(gca);
        gcr.addGeocodeResponseList(gcrl);

        // create xml from response object
        String gcrXML = gcr.toXML();

        OpenLSResponseParser rp = new OpenLSResponseParser();
        GeocodeResponse newgcr = rp.parseOpenLSResponse(gcrXML);
        String newgcrXML = newgcr.toXML();

        System.out.println(gcrXML);
        System.out.println(newgcrXML);

        assertEquals(gcrXML, newgcrXML);
    }

    /**
     * Read file as string.
     * 
     * @param filePath
     *            the file path
     * @return the string
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private String readFileAsString(String filePath) throws java.io.IOException {
        URL url = this.getClass().getResource(filePath);
        File file = new File(url.getFile());
        byte[] buffer = new byte[(int) file.length()];
        BufferedInputStream f = new BufferedInputStream(new FileInputStream(
                file));
        f.read(buffer);
        return new String(buffer);
    }

    /**
     * List directory filenames.
     * 
     * @param folder
     *            the folder
     * @param list
     *            the list
     */
    private void listDirectoryFilenames(File folder, List<File> list) {
        folder.setReadOnly();
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".xml")) {
                list.add(file);
            }
            if (file.isDirectory()) {
                listDirectoryFilenames(file, list);
            }
        }
    }

}

package nl.geozet.openls.databinding.openls;

import java.util.Vector;

import nl.geozet.openls.databinding.common.XmlNamespaceConstants;

public class GeocodeResponseList {
    /*
     * http://schemas.opengis.net/ols/1.2.0/LocationUtilityService.xsd
     * 
     * <complexType name="GeocodeResponseListType"> <sequence> <element
     * name="GeocodedAddress" type="xls:GeocodedAddressType"
     * maxOccurs="unbounded"> <annotation> <documentation>The list of 1-n
     * addresses that are returned for each Address request, sorted by
     * Accuracy.</documentation> </annotation> </element> </sequence> <attribute
     * name="numberOfGeocodedAddresses" type="nonNegativeInteger"
     * use="required"> <annotation> <documentation>This is the number of
     * responses generated per the different requests. Within each geocoded
     * address tit's possible to have multiple candidates</documentation>
     * </annotation> </attribute> </complexType>
     */

    private Vector<GeocodedAddress> geocodedAddress = new Vector<GeocodedAddress>();
    private int numberOfGeocodedAddresses;

    public void addGeocodedAddress(GeocodedAddress val) {
        geocodedAddress.add(val);
    }

    public GeocodedAddress getGeocodedAddressAt(int i) {
        return (GeocodedAddress) geocodedAddress.get(i);
    }

    public int getGeocodedAddressSize() {
        return geocodedAddress.size();
    }

    public void setNumberOfGeocodedAddresses(int val) {
        numberOfGeocodedAddresses = val;
    }

    public int getNumberOfGeocodedAddresses() {
        return numberOfGeocodedAddresses;
    }

    public String toXML() {
        String xml = "<" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":GeocodeResponseList " + "numberOfGeocodedAddresses=\""
                + Integer.toString(getGeocodedAddressSize()) + "\">";
        for (GeocodedAddress gca : geocodedAddress) {
            xml += gca.toXML();
        }
        xml += "</" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":GeocodeResponseList>";
        return xml;
    }
}

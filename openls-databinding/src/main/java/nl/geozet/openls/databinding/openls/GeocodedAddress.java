package nl.geozet.openls.databinding.openls;

import nl.geozet.openls.databinding.common.XmlNamespaceConstants;
import nl.geozet.openls.databinding.gml.Point;

public class GeocodedAddress {
    /*
     * http://schemas.opengis.net/ols/1.2.0/LocationUtilityService.xsd
     * 
     * <complexType name="GeocodedAddressType"> <sequence> <element
     * ref="gml:Point"/> <element ref="xls:Address"/> <element
     * ref="xls:GeocodeMatchCode" minOccurs="0"/> <!-- ref="xls:Point"/> -->
     * </sequence> </complexType>
     */
    private Point point;
    private Address address;

    private boolean hasPoint;
    private boolean hasAddress;

    public GeocodedAddress() {
        hasPoint = false;
        hasAddress = false;
    }

    public void setPoint(Point point) {
        this.hasPoint = true;
        this.point = point;
    }

    public Point getPoint() {
        return point;
    }

    public boolean hasPoint() {
        return this.hasPoint;
    }

    public void setAddress(Address address) {
        this.hasAddress = true;
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public boolean hasAddress() {
        return this.hasAddress;
    }

    public String toXML() {
        String xml = "<" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":GeocodedAddress>";
        if (hasPoint())
            xml += point.toXML();
        if (hasAddress())
            xml += address.toXML();
        xml += "</" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":GeocodedAddress>";
        return xml;
    }
}

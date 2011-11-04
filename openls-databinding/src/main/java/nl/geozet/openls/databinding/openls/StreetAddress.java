package nl.geozet.openls.databinding.openls;

import nl.geozet.openls.databinding.common.XmlNamespaceConstants;

public class StreetAddress {
    /*
     * http://schemas.opengis.net/ols/1.2.0/ADT.xsd
     * 
     * <complexType name="StreetAddressType"> <annotation> <documentation>A set
     * of precise and complete data elements that cannot be subdivided and that
     * describe the physical location of a place.</documentation> </annotation>
     * <sequence> <element ref="xls:_StreetLocation" minOccurs="0"/> <element
     * ref="xls:Street" maxOccurs="unbounded"/> </sequence> <attribute
     * name="locator"> <annotation> <documentation>typically used for the street
     * number (e.g. 23) a. Can accommodate a number, or any other building
     * locator b. "windmill house", "24E" and "323" are acceptable uses of the
     * locator c. We will adopt the following conventions for representing
     * address ranges in the locator attribute: i. Discontinuous range example:
     * "1-9" means 1,3,5,7,9 ii. Two discontinous ranges: "1-9,2-10" implies
     * 1,3,5,7,9 on one side of block and 2,4,6,8,10 on other side of block iii.
     * Continuous range: "1...10" means 1,2,3,4,5,6,7,8,9,10 </documentation>
     * </annotation> </attribute> </complexType> <element name="StreetAddress"
     * type="xls:StreetAddressType"> <annotation> <documentation>Structured
     * street address.</documentation> </annotation> </element>
     */
    private Building building;
    private Street street;

    private boolean hasBuilding;
    private boolean hasStreet;

    public StreetAddress() {
        this.hasBuilding = false;
        this.hasStreet = false;
    }

    public void setBuilding(Building building) {
        this.hasBuilding = true;
        this.building = building;
    }

    public Building getBuilding() {
        return building;
    }

    public boolean hasBuilding() {
        return this.hasBuilding;
    }

    public void setStreet(Street street) {
        this.hasStreet = true;
        this.street = street;
    }

    public Street getStreet() {
        return street;
    }

    public boolean hasStreet() {
        return this.hasStreet;
    }

    public String toXML() {
        String xml = "<" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":StreetAddress>";
        if (hasBuilding())
            xml += building.toXML();
        if (hasStreet())
            xml += street.toXML();
        xml += "</" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":StreetAddress>";
        return xml;
    }
}

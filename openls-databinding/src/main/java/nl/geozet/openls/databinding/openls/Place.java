package nl.geozet.openls.databinding.openls;

import nl.geozet.openls.databinding.common.XmlNamespaceConstants;

public class Place {
    /*
     * http://schemas.opengis.net/ols/1.2.0/ADT.xsd
     * 
     * <element name="Place" type="xls:NamedPlaceType"> <annotation>
     * <documentation>Place represents a hierarchical set of geographic
     * regions/placenames: country subdivision, country secondary subdivision,
     * municipality, and municipality subdivision.</documentation> </annotation>
     * </element> <complexType name="NamedPlaceType"> <annotation>
     * <documentation> The NamedPlaceType defines a named place within an
     * AddressType. A named place has a classification (such as country, country
     * subdivision, or municipality). </documentation> </annotation>
     * <simpleContent> <extension base="string"> <attribute name="type"
     * type="xls:NamedPlaceClassification" use="required"/> </extension>
     * </simpleContent> </complexType>
     */
    private String type;
    private String place;

    private boolean hasType;
    private boolean hasPlace;

    public Place() {
        this.hasType = false;
        this.hasPlace = false;
    }

    public void setType(String type) {
        this.hasType = true;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean hasType() {
        return this.hasType;
    }

    public void setPlace(String place) {
        this.hasPlace = true;
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    public boolean hasPlace() {
        return this.hasPlace;
    }

    public String toXML() {
        String xml = "<" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":Place";
        if (hasType())
            xml += " type=\"" + getType() + "\"";
        xml += ">";
        if (hasPlace())
            xml += getPlace();
        xml += "</" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX + ":Place>";
        return xml;
    }
}

package nl.geozet.openls.databinding.openls;

import nl.geozet.openls.databinding.common.XmlNamespaceConstants;

public class Street {
    /*
     * http://schemas.opengis.net/ols/1.2.0/ADT.xsd
     * 
     * <complexType name="StreetNameType"> <annotation> <documentation>The data
     * elements that make up the name of a street. There are two valid methods
     * for encoding this information: 1). Use the structured elements and
     * attributes. 2). The element value may contain a simplified string (e.g.
     * West 83rd. Street). An example: <Street directionalPrefix="W"
     * officialName="83RD" typeSuffix="ST"/> </documentation> </annotation>
     * <simpleContent> <extension base="string"> <attribute
     * name="directionalPrefix" type="string" use="optional"> <annotation>
     * <documentation>The direction for a street (e.g., North), placed before
     * the official name.</documentation> </annotation> </attribute> <attribute
     * name="typePrefix" type="string" use="optional"> <annotation>
     * <documentation>The street type (e.g., Rd or Ave) specified before the
     * official name </documentation> </annotation> </attribute> <attribute
     * name="officialName" type="string" use="optional"> <annotation>
     * <documentation> The name for a street (e.g., Main). </documentation>
     * </annotation> </attribute> <attribute name="typeSuffix" type="string"
     * use="optional"> <annotation> <documentation>The street type (e.g., Rd or
     * Ave) specified after the official name</documentation> </annotation>
     * </attribute> <attribute name="directionalSuffix" type="string"
     * use="optional"> <annotation> <documentation>The direction for a street
     * (e.g., North), placed after the official name.</documentation>
     * </annotation> </attribute> <attribute name="muniOctant"
     * type="gml:CompassPointEnumeration" use="optional"/> </extension>
     * </simpleContent> </complexType> <element name="Street"
     * type="xls:StreetNameType"> <annotation> <documentation>Structured Street
     * Name.</documentation> </annotation> </element>
     */
    private String street;

    private boolean hasStreet;

    public Street() {
        this.hasStreet = false;
    }

    public void setStreet(String street) {
        this.hasStreet = true;
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    public boolean hasStreet() {
        return this.hasStreet;
    }

    public String toXML() {
        String xml = "<" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":Street>";
        if (hasStreet())
            xml += getStreet();
        xml += "</" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":Street>";
        return xml;
    }
}

package nl.geozet.openls.databinding.openls;

import nl.geozet.openls.databinding.common.XmlNamespaceConstants;

public class Building {
    /*
     * http://schemas.opengis.net/ols/1.2.0/ADT.xsd
     * 
     * <element name="Building" type="xls:BuildingLocatorType"
     * substitutionGroup="xls:_StreetLocation"> <annotation> <documentation>An
     * addressable place; normally a location on a street: number, subdivision
     * name and/or building name.</documentation> </annotation> </element>
     * <complexType name="BuildingLocatorType"> <annotation> <documentation>A
     * type of AbstractStreetLocatorType</documentation> </annotation>
     * <complexContent> <extension base="xls:AbstractStreetLocatorType">
     * <attribute name="number" type="string" use="optional"/> <attribute
     * name="subdivision" type="string" use="optional"/> <attribute
     * name="buildingName" type="string" use="optional"/> </extension>
     * </complexContent> </complexType>
     */
    private String number;
    private String subdivision;
    private String buildingName;

    private boolean hasNumber;
    private boolean hasSubdivision;
    private boolean hasBuildingName;

    public Building() {
        this.hasNumber = false;
        this.hasSubdivision = false;
        this.hasBuildingName = false;
    }

    public void setNumber(String number) {
        this.hasNumber = true;
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public boolean hasNumber() {
        return this.hasNumber;
    }

    public void setSubdivision(String subdivision) {
        this.hasSubdivision = true;
        this.subdivision = subdivision;
    }

    public String getSubdivision() {
        return subdivision;
    }

    public boolean hasSubdivision() {
        return this.hasSubdivision;
    }

    public void setBuildingName(String buildingName) {
        this.hasBuildingName = true;
        this.buildingName = buildingName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public boolean hasBuildingName() {
        return this.hasBuildingName;
    }

    public String toXML() {
        String xml = "<" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":Building";
        if (hasNumber())
            xml += " number=\"" + getNumber() + "\"";
        xml += ">";
        xml += "</" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":Building>";
        return xml;
    }
}

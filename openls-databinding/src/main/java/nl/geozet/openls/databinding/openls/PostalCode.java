package nl.geozet.openls.databinding.openls;

import nl.geozet.openls.databinding.common.XmlNamespaceConstants;

public class PostalCode {
    /*
     * http://schemas.opengis.net/ols/1.2.0/ADT.xsd
     * 
     * <element name="PostalCode" type="xls:PostalCodeType"> <annotation>
     * <documentation>A zipcode or international postal code as defined by the
     * governing postal authority.</documentation> </annotation> </element>
     * <simpleType name="PostalCodeType"> <annotation> <documentation> The
     * AbstractPostalCodeType is an abstract type for postal code within an
     * AddressType. We do this because the components of a postal code vary
     * greatly throughout the world. So that the schema can accommodate this
     * variation we create derived types such as the USZipCodeType which has the
     * components for a US zipcode </documentation> </annotation> <restriction
     * base="string"/> </simpleType>
     */
    private String postalCode;

    private boolean hasPostalCode;

    public PostalCode() {
        this.hasPostalCode = false;
    }

    public void setPostalCode(String postalCode) {
        this.hasPostalCode = true;
        this.postalCode = postalCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public boolean hasPostalCode() {
        return this.hasPostalCode;
    }

    public String toXML() {
        String xml = "<" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":PostalCode>";
        if (hasPostalCode())
            xml += getPostalCode();
        xml += "</" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":PostalCode>";
        return xml;
    }
}

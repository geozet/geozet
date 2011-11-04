package nl.geozet.openls.databinding.openls;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import nl.geozet.openls.databinding.common.XmlNamespaceConstants;

public class Address {
    /*
     * http://schemas.opengis.net/ols/1.2.0/ADT.xsd
     * 
     * <complexType name="AddressType"> <annotation> <documentation>Defines an
     * address</documentation> </annotation> <complexContent> <extension
     * base="xls:AbstractAddressType"> <choice> <element name="freeFormAddress"
     * type="string"> <annotation> <documentation>An unstructured free form
     * address.</documentation> </annotation> </element> <sequence> <element
     * ref="xls:StreetAddress"/> <element ref="xls:Place" minOccurs="0"
     * maxOccurs="unbounded"/> <element ref="xls:PostalCode" minOccurs="0"/>
     * </sequence> </choice> </extension> </complexContent> </complexType>
     */
    private String countryCode;
    private StreetAddress streetAddress;
    private PostalCode postalCode;
    private final Vector<Place> place = new Vector<Place>();
    private final Map<String, String> placeMap = new HashMap<String, String>();

    private boolean hasCountryCode;
    private boolean hasStreetAddress;
    private boolean hasPostalCode;

    public Address() {
        this.hasCountryCode = false;
        this.hasStreetAddress = false;
        this.hasPostalCode = false;
    }

    public Address(String countryCode, StreetAddress streetAddress,
            PostalCode postalCode) {
        this.hasCountryCode = true;
        this.countryCode = countryCode;
        this.hasStreetAddress = true;
        this.streetAddress = streetAddress;
        this.hasPostalCode = true;
        this.postalCode = postalCode;
    }

    public void setCountryCode(String countryCode) {
        this.hasCountryCode = true;
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public boolean hasCountryCode() {
        return this.hasCountryCode;
    }

    public void setStreetAddress(StreetAddress streetAddress) {
        this.hasStreetAddress = true;
        this.streetAddress = streetAddress;
    }

    public StreetAddress getStreetAddress() {
        return this.streetAddress;
    }

    public boolean hasStreetAddress() {
        return this.hasStreetAddress;
    }

    public void setPostalCode(PostalCode postalCode) {
        this.hasPostalCode = true;
        this.postalCode = postalCode;
    }

    public PostalCode getPostalCode() {
        return this.postalCode;
    }

    public boolean hasPostalCode() {
        return this.hasPostalCode;
    }

    public void addPlace(Place val) {
        this.place.add(val);
        if (!val.getType().equals("")) {
            this.placeMap.put(val.getType(), val.getPlace());
        }
    }

    public Place getPlaceAt(int i) {
        return this.place.get(i);
    }

    public int getPlaceSize() {
        return this.place.size();
    }

    public String getPlaceByType(String type) {
        return this.placeMap.get(type);
    }

    public boolean hasPlaceType(String type) {
        return (this.placeMap.get(type) != null);
    }

    public String toXML() {
        String xml = "<" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":Address";
        if (this.hasCountryCode()) {
            xml += " countryCode=\"" + this.getCountryCode() + "\"";
        }
        xml += ">";
        if (this.hasStreetAddress()) {
            xml += this.streetAddress.toXML();
        }
        for (final Place pla : this.place) {
            xml += pla.toXML();
        }
        if (this.hasPostalCode()) {
            xml += this.postalCode.toXML();
        }
        xml += "</" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":Address>";
        return xml;
    }
}

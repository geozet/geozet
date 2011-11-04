package nl.geozet.openls.databinding.openls;

import java.util.Vector;

import nl.geozet.openls.databinding.common.XmlNamespaceConstants;

public class GeocodeResponse {
    /*
     * http://schemas.opengis.net/ols/1.2.0/LocationUtilityService.xsd
     * 
     * <complexType name="GeocodeResponseType"> <annotation>
     * <documentation>GeocodeResponse. The addresses returned will be normalized
     * Address ADTs as a result of any parsing by the geocoder,
     * etc.</documentation> </annotation> <complexContent> <extension
     * base="xls:AbstractResponseParametersType"> <sequence> <element
     * ref="xls:GeocodeResponseList" maxOccurs="unbounded"/> </sequence>
     * </extension> </complexContent> </complexType>
     */
    private Vector<GeocodeResponseList> geocodeResponseList = new Vector<GeocodeResponseList>();

    public void addGeocodeResponseList(GeocodeResponseList val) {
        geocodeResponseList.add(val);
    }

    public GeocodeResponseList getGeocodeResponseListAt(int i) {
        return (GeocodeResponseList) geocodeResponseList.get(i);
    }

    public int getGeocodeResponseListSize() {
        return geocodeResponseList.size();
    }

    public String toXML() {
        String xml = "<" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":GeocodeResponse " + "xmlns:"
                + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX + "=\""
                + XmlNamespaceConstants.OPENLS_NAMESPACE_URI + "\" " + "xmlns:"
                + XmlNamespaceConstants.OGC_GML_NAMESPACE_PREFIX + "=\""
                + XmlNamespaceConstants.OGC_GML_NAMESPACE_URI + "\">";
        for (GeocodeResponseList gcrl : geocodeResponseList) {
            xml += gcrl.toXML();
        }
        xml += "</" + XmlNamespaceConstants.OPENLS_NAMESPACE_PREFIX
                + ":GeocodeResponse>";
        return xml;
    }
}

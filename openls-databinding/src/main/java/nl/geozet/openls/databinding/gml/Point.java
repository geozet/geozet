package nl.geozet.openls.databinding.gml;

import java.util.Vector;

import nl.geozet.openls.databinding.common.XmlNamespaceConstants;

public class Point {
    /*
     * http://schemas.opengis.net/gml/3.2.1/geometryBasic0d1d.xsd
     * 
     * <complexType name="PointType"> <complexContent> <extension
     * base="gml:AbstractGeometricPrimitiveType"> <sequence> <choice> <element
     * ref="gml:pos"/> <element ref="gml:coordinates"/> </choice> </sequence>
     * </extension> </complexContent> </complexType>
     */
    private Vector<Pos> pos = new Vector<Pos>();;
    private String srsName;

    private boolean hasSrsName;

    public Point() {
        this.hasSrsName = false;
    }

    public void addPos(Pos pos) {
        this.pos.add(pos);
    }

    public Pos getPosAt(int i) {
        return (Pos) this.pos.get(i);
    }

    public int getPosSize() {
        return pos.size();
    }

    public void setSrsName(String srsName) {
        this.hasSrsName = true;
        this.srsName = srsName;
    }

    public String getSrsName() {
        return srsName;
    }

    public boolean hasSrsName() {
        return this.hasSrsName;
    }

    public String toXML() {
        StringBuilder sb = new StringBuilder("<"
                + XmlNamespaceConstants.OGC_GML_NAMESPACE_PREFIX + ":Point");

        if (hasSrsName()) {
            sb.append(" srsName=\"").append(getSrsName()).append("\"");
        }
        sb.append(">");
        for (Pos p : pos) {
            sb.append(p.toXML());
        }
        sb.append("</" + XmlNamespaceConstants.OGC_GML_NAMESPACE_PREFIX
                + ":Point>");
        return sb.toString();

        // String xml = "<" + XmlNamespaceConstants.OGC_GML_NAMESPACE_PREFIX
        // + ":Point";
        // if (hasSrsName()) {
        // xml += " srsName=\"" + getSrsName() + "\"";
        // }
        // xml += ">";
        // for (Pos p : pos) {
        // xml += p.toXML();
        // }
        // xml += "</" + XmlNamespaceConstants.OGC_GML_NAMESPACE_PREFIX
        // + ":Point>";
        // return xml;
    }
}

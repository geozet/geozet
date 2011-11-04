package nl.geozet.openls.databinding.gml;

/**
 * http://schemas.opengis.net/gml/3.2.1/basicTypes.xsd
 * 
 * <pre>
 * <complexType name="CoordinatesType"> 
 * <annotation> 
 * <documentation>This
 * type is deprecated for tuples with ordinate values that are numbers.
 * CoordinatesType is a text string, intended to be used to record an array
 * of tuples or coordinates. While it is not possible to enforce the
 * internal structure of the string through schema validation, some optional
 * attributes have been provided in previous versions of GML to support a
 * description of the internal structure. These attributes are deprecated.
 * The attributes were intended to be used as follows: Decimal symbol used
 * for a decimal point (default="." a stop or period) cs symbol used to
 * separate components within a tuple or coordinate string (default="," a
 * comma) ts symbol used to separate tuples or coordinate strings
 * (default=" " a space) Since it is based on the XML Schema string type,
 * CoordinatesType may be used in the construction of tables of tuples or
 * arrays of tuples, including ones that contain mixed text and numeric
 * values.</documentation> 
 * </annotation> 
 * <simpleContent> 
 *  <extension base="string"> 
 *      <attribute name="decimal" type="string" default="."/>
 *      <attribute name="cs" type="string" default=","/> 
 *      <attribute name="ts" type="string" default="&#x20;"/> 
 *  </extension> 
 *  </simpleContent>
 * </complexType>
 * </pre>
 * 
 * @author strampel@atlis.nl
 */
public class CoordinatesType {

}

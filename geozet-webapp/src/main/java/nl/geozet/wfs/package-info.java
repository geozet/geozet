/**
 * Package voor de WFS clients van GEOZET viewer.
 * Hoewel de exacte functionaliteit per servlet verschilt is de algemene 
 * lijn voor de afhandeling van een request als volgt
 * <ul>
 * <li>verzoek komt binne op de <code>service</code> methode waar de request parameters worden gevalideerd</li>
 * <li>er wordt een filter gemaakt waarmee de bekendmaking(en) worden opgehaald bij de WFS met de methode <code>ophalenBekendmakingen()</code></li>
 * <li>De opgehaalde bekendmaking(en) worden gerenderd in de methode <code>renderHTMLResults()</code></li>
 * </ul>
 * 
 * @author prinsmc@minlnv.nl
 * @since 1.6
 * @since GeoTools API 2.7 
 * @opt shape package
 */
package nl.geozet.wfs;


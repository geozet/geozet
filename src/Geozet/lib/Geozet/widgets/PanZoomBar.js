/**
 * Copyright (c) 2010 PDOK
 *
 * Published under the Open Source GPL 3.0 license.
 * http://www.gnu.org/licenses/gpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 */

/**
 * Class: Geozet.PanZoomBar
 * A component which can be used for zooming and panning. Uses the GeoExt ZoomSlider.
 *
 * Inherits:
 *  - <Ext.BoxComponent>
 */
Geozet.PanZoomBar = Ext.extend(Ext.BoxComponent, {

    /**
     * Property: renderTo
     * {String} The id of the component in which the panzoombar needs to be rendered.
     */
    renderTo: null,

    /**
     * APIProperty: slideFactor
     * {Integer} Number of pixels by which we'll pan the map in any direction
     *     on clicking the arrow buttons.  If you want to pan by some ratio
     *     of the map dimensions, use <slideRatio> instead.
     */
    slideFactor: 50,

    /**
     * APIProperty: slideRatio
     * {Number} The fraction of map width/height by which we'll pan the map
     *     on clicking the arrow buttons.  Default is null.  If set, will
     *     override <slideFactor>. E.g. if slideRatio is .5, then the Pan Up
     *     button will pan up half the map height.
     */
    slideRatio: null,

    /**
     * Method: onRender
     * Called when this component is rendered. Create our DOM elements.
     */
    onRender: function() {
        Geozet.PanZoomBar.superclass.onRender.apply(this, arguments);
        this.el = Ext.get(this.renderTo);
        var ul = Ext.DomHelper.append(this.el, {tag: 'ul'});
        var zoombar = Ext.DomHelper.append(this.el, {tag: 'div', 'class': 'zoombar'});
        var options = this.sliderOptions || {};
        options.renderTo = zoombar;
        options.map = this.map;
        this.slider = new GeoExt.ZoomSlider(options);
        // pan buttons
        this.createButton(ul, 'panup', 'up', OpenLayers.i18n("panUpTitle"));
        this.createButton(ul, 'panleft', 'left', OpenLayers.i18n("panLeftTitle"));
        this.createButton(ul, 'panright', 'right', OpenLayers.i18n("panRightTitle"));
        this.createButton(ul, 'pandown', 'down', OpenLayers.i18n("panDownTitle")); 
        // zoomin and zoomout buttons
        this.createButton(ul, 'zoomin', 'zoom-plus', OpenLayers.i18n("zoominTitle"));
        this.createButton(ul, 'zoomout', 'zoom-min', OpenLayers.i18n("zoomoutTitle"));
        Ext.get(this.el).on('click', this.clickButton, this, {delegate: 'a'});
    },

    /**
     * APIMethod: destroy
     * Clean up
     */
    destroy: function() {
        Ext.get(this.el).un('click', this.clickButton, this, {delegate: 'a'});
        this.slider.destroy();
        this.slider = null;
        this.map = null;
        Geozet.PanZoomBar.superclass.destroy.apply(this, arguments);
    },

    /**
     * Function: getSlideFactor
     * Get the displacement for when moving the map.
     *
     * Parameters:
     * dim - {String} The dimension, width ("w") or height ("h")
     *
     * Returns:
     * {Integer} The number of pixels to slide.
     */
    getSlideFactor: function(dim) {
        if(!this.slideRatio){
            var slideFactorPixels = this.slideFactor;
            return slideFactorPixels;
        } else {
            var slideRatio = this.slideRatio;
            return this.map.getSize()[dim] * slideRatio;
        }
    },

    /**
     * Method: clickButton
     * Click handler for the buttons. Pan or zoom.
     *
     * Parameters:
     * evt - {Object} The event object.
     */
    clickButton: function(evt) {
        switch (Ext.get(evt.target).id) {
            case "panup":
                this.map.pan(0, -this.getSlideFactor("h"));
                break;
            case "pandown":
                this.map.pan(0, this.getSlideFactor("h"));
                break;
            case "panleft":
                this.map.pan(-this.getSlideFactor("w"), 0);
                break;
            case "panright":
                this.map.pan(this.getSlideFactor("w"), 0);
                break;
            case "zoomin":
                this.map.zoomIn();
                break;
            case "zoomout":
                this.map.zoomOut();
                break;
            default:
                break;
        }
        evt.stopEvent();
    },

    /**
     * Method: createButton
     * Create a button, which is a listitem with an anchor.
     *
     * Parameters:
     * parent - {DOMElement} The parent of this button
     * id - {String} The id to use for the anchor created
     * cls - {String} The css class to use on the listitem
     * title - {String} The title to use on the anchor
     */
    createButton: function(parent, id, cls, title) {
        var li = Ext.DomHelper.append(parent, {tag: 'li', cls: cls});
        var anchor = Ext.DomHelper.append(li, {tag: 'a', id: id, href: '#', html: title, title: title});
    }

});

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
 * Class: Geozet.plugins.ResizeMap
 * ResizeMap allows the user to resize the map. If maximized, it will fill up with
 * the available space on the right and bottom. If minimized, it will not get smaller
 * than a pre-defined minimum size. If maximized, it will listen to browser resize
 * events.
 *
 * Inherits:
 *  - <Ext.util.Observable>
 */
Geozet.plugins.ResizeMap = Ext.extend(Ext.util.Observable, {

    /**
     * Property: mapPanel
     * {<GeoExt.MapPanel>} The MapPanel this plugin is associated with.
     */
    mapPanel: null,

    /**
     * APIProperty: autoHeight
     * {Boolean} Do we want the map to start up with the available height?
     */
    autoHeight: null,

    /**
     * APIProperty: autoWidth
     * {Boolean} Do we want the map to start up with the available width?
     */
    autoWidth: null,

    /**
     * APIProperty: staticSize
     * {Array({Integer})} An array with width and height in pixels with
     * which the map is initially initiated.
     */
    staticSize: null,

    /**
     * Property: isMaximized
     * {Boolean} Has the map been maximized?
     */
    isMaximized: null,

    /**
     * APIProperty: scroll
     * {Integer} The size of a scrollbar in pixels, this is an estimate
     * which fits most browsers. Defaults to 42 pixels. IE6 needs this
     * large number to prevent scrollbars from appearing.
     */
    scroll: 42,

    /**
     * Constructor: Geozet.plugins.ResizeMap
     *
     * Parameters:
     * config - {Object} options for this plugin.
     */
    constructor: function(config) {
        Ext.apply(this, config);
    },

    /**
     * Method: init
     * Initialize this plugin with the parent container.
     *
     * Parameters:
     * mapPanel - {<GeoExt.MapPanel>}
     */
    init: function(mapPanel) {
        this.mapPanel = mapPanel;
        this.mapPanel.on('afterrender', this.addAnchor, this);
    },

    /**
     * Destructor: Geozet.plugins.ResizeMap
     */
    destroy : function() {
        this.mapPanel.un('afterrender', this.addAnchor, this);
        this.mapPanel = null;
        this.anchor.un('click', this.toggleResizeMap, this);
        this.anchor.update('');
        Ext.destroy(this.anchor);
        this.anchor = null;
        Ext.EventManager.un(window, "resize", this.resizeMapOnBrowserResize, this);
        Ext.EventManager.un(window, "resize", this.toggleAnchor, this);
        this.purgeListeners();
    },

    /**
     * Method: addAnchor
     * Add the anchor tag which will trigger the resizemap functionality.
     */
    addAnchor: function() {
        this.anchor = Ext.get(Ext.DomHelper.append(this.mapPanel.body, 
            {tag: 'a', href: '#', title: OpenLayers.i18n('resizeToggleTitle'), id: 'toggleMapSize'}));
        this.anchor.on('click', this.toggleResizeMap, this);
        var parent = this.mapPanel.el.parent();
        var maxSize = this.getMaxAvailableSize();
        if (this.autoWidth === true) {
            Ext.getBody().addClass('geozetMax');
            this.anchor.toggleClass('max');
            this.isMaximized = true;
            var w = Math.max(maxSize[0], this.staticSize[0]);
            parent.setWidth(w+parent.getBorderWidth("lr"));
            this.mapPanel.body.setWidth(w);
            var height = Math.max(Ext.get('geozetAside').getHeight(), Ext.get('geozetArticle').getHeight());
            Ext.get('geozetContent').setHeight(height);
            Ext.EventManager.on(window, "resize", this.resizeMapOnBrowserResize, this, {buffer: 100});
        }
        if (this.autoHeight === true) {
            var h = Math.max(maxSize[1], this.staticSize[1]);
            parent.setHeight(h+parent.getBorderWidth("bt"));
            this.mapPanel.body.setHeight(h);
        }
        this.mapPanel.map.updateSize();
        this.toggleAnchor();
        Ext.EventManager.on(window, "resize", this.toggleAnchor, this, {buffer: 100});
    },

    /**
     * Method: toggleAnchor
     * If the width of the map gets too small, do not show the anchor anymore.
     */
    toggleAnchor: function() {
        if (this.hidden === true || this.getMaxAvailableSize()[0] < this.staticSize[0]+this.anchor.getWidth()) {
            this.anchor.hide();
        } else {
            this.anchor.show();
        }
    },

    /**
     * Function: getMaxAvailableSize
     * Get the maximum available size for the map.
     *
     * Returns:
     * {Array({Integer})} An array with the width and height in pixels.
     */
    getMaxAvailableSize: function() {
        var size = Ext.getBody().getViewSize();
        var pos = Ext.get(this.mapPanel.el).getXY();
        var offset = 0;
        if (Ext.get(Geozet.config.attributionDiv)) {
            offset = Ext.get(Geozet.config.attributionDiv).getHeight();
        }
        return [(size.width-pos[0]-this.scroll), (size.height-pos[1]-this.scroll-offset)];
    },

    /**
     * Method: resizeMapOnBrowserResize
     * Intermediate function which is called when the browser window is resized.
     * In this case, we will resize the map.
     */
    resizeMapOnBrowserResize: function() {
        this.resizeMap(false, false);
    },

    /**
     * Method: resizeMap
     * Resize the map container, making sure it does not get smaller than the static size.
     *
     * Parameters:
     * static - {Boolean}
     */
    resizeMap: function(static, toggle) {
        var w, h;
        var parent = this.mapPanel.el.parent();
        var maxSize = this.getMaxAvailableSize();
        if (static !== true) {
            w = Math.max(maxSize[0], this.staticSize[0]);
            // only resize width
            h = this.mapPanel.body.getHeight();
        } else {
            w = this.staticSize[0]; /* border wordt bij breedte kaart erbij */
            if (this.autoHeight === true) {
                h = Math.max(maxSize[1], this.staticSize[1]);
            } else {
                h = this.staticSize[1];
            }
        }
        if (toggle === true) {
            Ext.getBody().toggleClass('geozetMax');
        }
        // bartvde, in IE6 mapPanel.setSize does not work properly, so we need to use internals here
        parent.setSize(w+parent.getBorderWidth("lr"), h+parent.getBorderWidth("bt"));
        this.mapPanel.body.setSize(w, h);
        this.mapPanel.map.updateSize();
    },

    /**
     * Method: toggleResizeMap
     * This function gets called when somebody clicks on the anchor. If already
     * maximized, switch back to the static size.
     */
    toggleResizeMap: function(evt) {
       this.anchor.toggleClass('max');
       if (this.isMaximized === true) {
            if (this.autoHeight !== true) {
                Ext.EventManager.un(window, "resize", this.resizeMapOnBrowserResize, this);
                Ext.get('geozetContent').dom.removeAttribute("style");
            }
            this.isMaximized = false;
            this.resizeMap(true, true);
        } else {
            this.isMaximized = true;
            var height = Math.max(Ext.get('geozetAside').getHeight(), Ext.get('geozetArticle').getHeight());
            Ext.get('geozetContent').setHeight(height);
            this.resizeMap(false, true);
            Ext.EventManager.on(window, "resize", this.resizeMapOnBrowserResize, this, {buffer: 100});
        }
        evt.stopEvent();
    }

});

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

// This is a mix of OpenLayers en Ext JS conventions
// Strategy, Renderer, Format and Control are OpenLayers terms, and start uppercase.
// plugins and widgets are Ext JS terms, and start lowercase.
Ext.namespace('Geozet', 'Geozet.Strategy', 'Geozet.Renderer', 'Geozet.plugins', 'Geozet.widgets', 'Geozet.Format', 'Geozet.Control');

/**
 * Class: Geozet.Viewer
 * The viewer object which will be created when the DOM is ready.
 * All GIS components will be created in this class.
 */
Geozet.Viewer = function() {
    // private space
    var map;
    var legend;
    var popup;
    var vlakgericht;
    var vectorLayer;
    var selectfeature;
    var search;
    var panzoombar;
    var resizemap;

    // public space
    return {
        /**
         * Constructor: create
         * Initalizes and constructs everything needed for the Geozet Viewer.
         */
        create: function(config) {
            this.config = config;
            $(window).unload(function() {
                Geozet.Viewer.destroy();
            });
            // the application is in Dutch
            OpenLayers.Lang.setCode('nl');
            OpenLayers.Number.decimalSeparator = ",";
            this.createMap();
            this.createPopup();
            this.createVlakgericht();
            this.createOverviewMap();
            this.createBaseLayer();
            this.createVectorLayers();
            this.createSelectFeatureControl();
            this.createSearch();
            this.createLegendPanel();
            // init app based on url
            // if we get a location through the hash, we need to postpone
            // initialization of the map panel until we now where to go
            var searchCb = function(req) {
                var returnObj = search.handleResponse(req, true);
                this.createMapPanel(returnObj.center, returnObj.zoom);
            };
            var cb = OpenLayers.Function.bind(searchCb, this);
            var startHash = document.location.hash;
            if (startHash != "" && startHash != "#"){
                search.setSearchForm("geozetCoreEntree");
                search.sendRequestFromHash(startHash, cb);
            } else {
                this.createMapPanel();
            }
            this.createPanZoomBar();
        },
        /**
         * Method: createLegendPanel
         * Create the legend panel for filtering categories
         *
         * Parameters:
         * visibleClasses - {Array({String})} A list of classes to initialize as visible (checked).
         *     If not provided, all classes will be visible.
         */
        createLegendPanel: function(visibleClasses) {
            if (Ext.get(this.config.legendDiv)) {
                legend = new Geozet.Legend({
                    map: map,
                    ulCls: this.config.legendUlCls,
                    renderTo: this.config.legendDiv,
                    visibleClasses: visibleClasses,
                    legendConfig: this.config.classificationInfo
                });
            }
        },
        /**
         * Method: createPanZoomBar
         * Create the panzoombar which is a control that can be used to pan and zoom
         * the map.
         */
        createPanZoomBar: function() {
            if (Ext.get(this.config.panZoomBarDiv)) {
                var options = Geozet.config.panZoomBarOptions;
                options.map = map;
                options.renderTo = Geozet.config.panZoomBarDiv;
                panzoombar = new Geozet.PanZoomBar(options);
            }
        },
        /**
         * Method: createMapPanel
         * Create the map panel.
         *
         * Parameters:
         * center - {<OpenLayers.LonLat>} The center point of the map.
         * zoom - {Integer} The zoom level for intializing the map panel.
         */
        createMapPanel: function(center, zoom) {
            resizemap = new Geozet.plugins.ResizeMap({
                hidden: !Geozet.config.containers.resizeMap, staticSize: this.config.staticMapSize,
                autoWidth: this.config.mapAutoWidth, autoHeight: this.config.mapAutoHeight});
            var options = OpenLayers.Util.extend({
                map: map,
                border: false,
                header: false,
                renderTo: this.config.mapDiv,
                width: (this.config.mapAutoWidth === true) ? undefined : this.config.staticMapSize[0],
                height: (this.config.mapAutoHeight === true) ? undefined : this.config.staticMapSize[1],
                plugins: [resizemap]
            }, this.config.mapPanel);
            if (center) {
                options.center = center;
            }
            if (zoom) {
                options.zoom = zoom;
            }
            mapPanel = new GeoExt.MapPanel(options);
        },
        /**
         * Method: createSelectFeatureControl
         * Create the select feature control. The select feature control is used to show popovers
         * on mouseover and popups on click. The events come in via the renderer. The correct layer
         * is dynamically attached to the SelectFeature control based on the scale of the map.
         */
        createSelectFeatureControl: function() {
            var mouseOver = function(feature) {
                var viewport = Ext.get(map.viewPortDiv);
                if (!viewport.hasClass('olDragDown') && !viewport.hasClass('olDrawBox')) {
                    popup.openPopoverFor(feature);
                }
            };
            var mouseOut = function(feature) {
                popup.hidePopover();
            };
            var mouseClick = function(feature) {
                popup.openPopupFor(feature);
                return false;
            };
            var callbacks = {
                "over": mouseOver,
                "out": mouseOut,
                "click": mouseClick
            };
            selectfeature = new OpenLayers.Control.SelectFeature(vectorLayer, 
                {hover: true, autoActivate: true, callbacks: callbacks});
            map.addControl(selectfeature);
        },
        /**
         * Method: createVectorLayers
         * Create the vector layers of the application. They all come from the bekendmakingen WFS.
         */
        createVectorLayers: function() {
            vectorLayer = new OpenLayers.Layer.Vector(null, this.config.bekendmakingenLayer);
            map.addLayer(vectorLayer);
            var keys = ['provincie', 'gemeente', 'wijk'];
            for (var i=0; i<keys.length; i++) {
                var options = this.config.clusterLayer[keys[i]];
                var lyr = new OpenLayers.Layer.Vector(options.title, options);
                map.addLayer(lyr);
            }
        },
        /**
         * Method: createBaseLayer
         * Create the base layer of the map, the topographic map coming from a tile cache.
         */
        createBaseLayer: function() {
            map.addLayer(this.config.backgroundLayer);
        },
        /**
         * Method: createPopup
         * Create the popup component. There is only 1 popup in the entire viewer.
         */
        createPopup: function() {
            popup = new Geozet.Popup({
                map: map,
                iconSize: this.config.iconSize,
                clusterZoom: this.config.clusterZoom,
                attributes: this.config.popupAttributes, 
                templates: this.config.templates
            });
        },
        /**
         * Method: createVlakgericht
         * Create the vlakgericht component. 
         */
        createVlakgericht: function() {
            vlakgericht = new Geozet.Vlakgericht({
                map: map
            });
        },
        /**
         * Method: createOverviewMap
         * Create the overview map control where people can see the context of where their
         * map is currently at.
         */
        createOverviewMap: function() {
            var div = Ext.get(this.config.overviewDiv);
            if (div) {
                this.config.overview.div = div.dom;
                var overview = new OpenLayers.Control.OverviewMap(this.config.overview);
                map.addControl(overview);
            }
        },
        /**
         * Method: createSearch
         * Create the search component that interacts with the Geocoder.
         */
        createSearch: function() {
            search = new Geozet.Search(this.config, map);			
        },
        /**
         * Method: createMap
         * Create the {<OpenLayers.Map>} object for the main map.
         */
        createMap: function() {
            map = new OpenLayers.Map(this.config.mapDiv, this.config.map);
            var options = Geozet.config.scaleBar;
            options.div = Ext.get(options.id).dom;
            if (options.div) {
                map.addControl(new Geozet.Control.ScaleBar(options));
            }
            if (Geozet.config.attributionDiv) {
                map.addControl(new OpenLayers.Control.Attribution({div: Ext.get(Geozet.config.attributionDiv).dom}));
            }
            // we cannot use an array of layers on the select feature control
            // since this will create a RootContainer vector layer in SVG or VML
            map.events.register("zoomend", this, this.attachLayerToControl);
        },
        /**
         * Method: destroy
         * Clean up.
         */
        destroy: function() {
            if (map && map.events) {
                map.events.unregister("zoomend", this, this.attachLayerToControl);
            }
            if (popup) { 
                popup.destroy();
                popup = null;
            }
            if (vlakgericht) {
                vlakgericht.destroy();
                vlakgericht = null;
            }
            if (legend) {
                legend.destroy();
                legend = null;
            }
            if (panzoombar) {
                panzoombar.destroy();
                panzoombar = null;
            }
            if (search) {
                search.destroy();
                search = null;
            }
            if (resizemap) {
                resizemap.destroy();
                resizemap = null;
            }
            if (mapPanel) {
                mapPanel.destroy();
                mapPanel = null;
            }
            if (map) {
                map.destroy();
                map = null;
            }
        },
        /**
         * Method: attachLayerToControl
         * Attach the correct vector layer to the SelectFeature Control based on the scale
         * of the map.
         */
        attachLayerToControl: function() {
            for (var i=0, len=map.layers.length; i<len; i++) {
                var layer = map.layers[i];
                if (layer instanceof OpenLayers.Layer.Vector && layer.inRange) {
                    selectfeature.setLayer(layer);
                    break;
                }
            }
        },
        /**
         * APIFunction: getPopup
         * Get the popup.
         *
         * Returns:
         * {<Geozet.Popup>}
         */
        getPopup: function() {
            return popup;
        },
        /**
         * APIFunction: getMap
         * Get the OpenLayers Map object of this viewer.
         *
         * Returns:
         * {<OpenLayers.Map>}
         */
        getMap: function() {
            return map;
        },
        /**
         * APIFunction: getLegend
         * Get the Legend object of this viewer application.
         *
         * Returns:
         * {<Geozet.Legend>}
         */
        getLegend: function() {
            return legend;
        }
    };

}();

/**
 * Constant: VERSION_NUMBER
 */
Geozet.VERSION_NUMBER="1.1";

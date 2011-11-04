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
 * Class: Geozet.Popup
 * A popup which will be shown on click, and a popover which will be
 * shown on mouseover.
 */
Geozet.Popup = OpenLayers.Class({

    /**
     * Property: map
     * {<OpenLayers.Map>} The map object to interact with.
     */
    map: null,

    /**
     * Property: attributes
     * {Object} The feature attributes configuration object.
     */
    attributes: null,

    /**
     * Property: templates
     * {Object} The templates this popup will use for presentation.
     */
    templates: null,

    /**
     * Property: clusterZoom
     * {Object} zoom level to zoom to if clicked on a cluster.
     */
    clusterZoom: null,

    /**
     * Property: iconSize
     * {Object} icon size for a set of categories.
     */
    iconSize: null,
    
    /**
     * Property: layer
     * {<OpenLayers.Layer.Vector>} The vector layer to interact with.
     */
    layer: null,

    /**
     * Property: popuppedFeature
     * {<OpenLayers.Feature.Vector>} The feature attached to this popup.
     */
    popuppedFeature: null,

    /**
     * Property: feature
     * {<OpenLayers.Feature.Vector>} An internal reference to the feature.
     */    
    feature: null,

    /**
     * Property: popupDiv
     * {DOMElement} The DOM element used for the popup.
     */
    popupDiv: null,
    
    /**
     * Property: popoverDiv
     * {DOMElement} The DOM element used for the popover.
     */
    popoverDiv: null,
   
    /**
     * Property: itemLength
     * {Integer} The number of features this popup is paging.
     */ 
    itemLength: 0,
    
    /**
     * Property: itemCurrent
     * {Integer} The current item which is being paged.
     */
    itemCurrent: 1,

    /**
     * Constructor: Geozet.Popup
     * Create a new popup.
     *
     * Parameters:
     * options - {Object} Optional object whose properties will be set on the
     *     control. Required is map property with a reference to the {<OpenLayers.Map}>.
     */ 
    initialize: function(options) {
        OpenLayers.Util.extend(this, options);
        var closeClick = OpenLayers.Function.bind(this.hidePopup, this);
        $('body').delegate('#geozetPopup .close', 'click', closeClick);
        this.popupDiv = Ext.DomHelper.append(Ext.getBody(), {tag: 'div', id: 'geozetPopup'});
        this.popoverDiv = Ext.DomHelper.append(Ext.getBody(), {tag: 'div', id: 'geozetPopover'});
        Ext.get(this.popoverDiv).on('click', this.onPopoverClick, this);
        this.map.events.register('move', this, this.move);
        $('body').delegate('#geozetPopupForward','click', function() { Geozet.Viewer.getPopup().go(true); return false; });
        $('body').delegate('#geozetPopupBack','click', function() { Geozet.Viewer.getPopup().go(false); return false; });
    },

    /**
     * Function: getFeatureType
     * Get the type of feature
     * 
     * Parameters:
     * feature - {<OpenLayers.Feature.Vector>}
     *
     * Returns:
     * {String} The type of feature, one of "feature", "serversidecluster" or "cluster".
     */
    getFeatureType: function(feature) {
        if (!feature.cluster && feature.attributes[this.attributes.categorie] || 
            (feature.cluster && feature.attributes[this.attributes.count] === 1)) {
                return "feature";
        } else if (!feature.cluster) {
            return "serversidecluster";
        } else {
            return "cluster";
        }
    },

    /**
     * Function: getTitleForFeature
     * Get a plain text version of the popover to use in the title attribute of the anchor
     *
     * Parameters:
     * feature - {<OpenLayers.Feature.Vector>}
     *
     * Returns:
     * {String}
     */
    getTitleForFeature: function(feature) {
        var templateKey = this.getFeatureType(feature) + "PopoverText";
        if (feature.layer && feature.layer.name) {
            feature.attributes.layername = feature.layer.name;
        }
        return this.templates[templateKey].applyTemplate(feature.attributes);
    },

    /**
     * Method: openPopoverFor
     * Show the popover for a feature.
     *
     * Parameters:
     * feature - {<OpenLayers.Feature.Vector>}
     */ 
    openPopoverFor: function(feature) {
        if (feature == this.popuppedFeature){
            // this is the feature that currently has a popup, NO popover for this one
            return;
        }
        this.feature = feature;
        if (feature.layer) {
            this.layer = feature.layer;
        }
        var featurePos = this.featurePosOfPoint(feature);
        if (featurePos !== null) {
            var templateKey = this.getFeatureType(feature) + "Popover";
            if (feature.layer && feature.layer.name) {
                feature.attributes.layername = feature.layer.name;
            }
            this.templates[templateKey].overwrite(Ext.get('geozetPopover'), feature.attributes);
            var categorie = feature.attributes[this.attributes.categorie] || "cluster";
            $('#geozetPopover span').removeClass().addClass(categorie);
            $('#geozetPopover').css({'display': 'block', 'top' : featurePos.top, 'left' : featurePos.left});
        }
    },

    /**
     * Method: removeActiveState
     * Remove the active state of the feature, this is used for printing, to show a 
     * different css style around the feature which is associated with the popup.
     */
    removeActiveState: function() {
        if (this.popuppedFeature) {
            delete this.popuppedFeature.attributes.active;
            // only draw if the feature is still in the layer!
            var layer = this.popuppedFeature.layer;
            if (layer) {
                layer.drawFeature(this.popuppedFeature);
            }
        }
    },

    /**
     * Method: addActiveState
     * Add the active state of the feature, this is used for printing, to show a 
     * different css style around the feature which is associated with the popup.
     */
    addActiveState: function() {
        if (this.popuppedFeature) {
            this.popuppedFeature.attributes.active = true;
            // only draw if the feature is still in the layer!
            var layer = this.popuppedFeature.layer;
            if (layer) {
                layer.drawFeature(this.popuppedFeature);
            }
        }
    },
    
    /**
     * APIMethod: openPopupFor
     * Open ups the popup for a certain feature.
     *
     * Parameters:
     * feature - {<OpenLayers.Feature.Vector>}
     */
    openPopupFor: function(feature) {
        this.removeActiveState();
        this.map = feature.layer.map;
        // hide potentially showing popover
        this.hidePopover();
        this.feature = feature;
        if(feature.layer){
            this.layer = feature.layer;
        }
        this.popuppedFeature = feature;
        this.addActiveState();
        // if server-side cluster, we need to zoom centered on the feature
        if (!feature.attributes[this.attributes.categorie]) {
            var center = new OpenLayers.LonLat(this.feature.geometry.x, this.feature.geometry.y);
            // determine zoom level
            this.map.setCenter(center, this.clusterZoom[this.feature.attributes.cluster]);
            return;
        }
        var ft = this.getFeatureType(feature);
        if (ft === "feature") {
            this.templates["featurePopupHeader"].overwrite(Ext.get('geozetPopup'), feature.attributes);
            this.templates["featurePopupContent"].append(Ext.get('geozetPopup'), feature.attributes);
        }
        if (ft === "cluster") {
            this.itemLength = feature.attributes[this.attributes.count];
            var html = this.templates["clusterPopupHeader"].applyTemplate(feature.attributes);
            var first=true;
            for (var i=0;i<feature.cluster.length;i++){
                var child = feature.cluster[i];
                if (!(child.style && 'none'==child.style.display)){
                    child.attributes.activeClass = (first === true) ? "active" : "";
                    first = false;
                    html += this.templates['clusterChildPopupHeader'].applyTemplate(child.attributes);
                    html += this.templates['featurePopupContent'].applyTemplate(child.attributes);
                    html += this.templates['clusterChildPopupFooter'].applyTemplate();
                }
            }
            html += this.templates["clusterPopupFooter"].applyTemplate(feature.attributes);
            Ext.get("geozetPopup").update(html);
        }
        $('#geozetPopup .close').focus();
        this.move();
    },

    /**
     * APIMethod: go
     * When paging multiple features, move forward or backward.
     *
     * Parameters:
     * next  - {Boolean} If true, move forward. If false, move backward.
     */ 
    go: function(next){
        var to = next?this.itemCurrent+1:this.itemCurrent-1;
        if ((to > 0 ) && (to <= this.itemLength)) {
            this.itemCurrent=to;
            $('#geozetPopup ul.cluster li').removeClass('active');
            $('#geozetPopup p.btn a').removeClass().attr('disabled','');
            $('#geozetPopup p.btn span').html(this.itemCurrent);
            $('#geozetPopup ul.cluster li:nth-child('+this.itemCurrent+')').addClass('active');
            // disable next button
            if (this.itemCurrent == this.itemLength) { $('#geozetPopupForward').addClass('inactive').attr('disabled','disabled'); }
            // disable back button
            if (this.itemCurrent == 1) { $('#geozetPopupBack').addClass('inactive').attr('disabled','disabled'); }
        }
        this.move();
        return false;
    },

    /**
     * Method: onPopoverClick
     * When somebody clicks on the popover, we need to open up the popup.
     */
    onPopoverClick: function(){
        this.openPopupFor(this.feature);
    },

    /**
     * Method: hidePopover
     * Hide the popover.
     */ 
    hidePopover:function() {
        $('#geozetPopover').css('display','none');
    },

    /**
     * APIMethod: hidePopup
     * Hide the popup.
     *
     * Parameters:
     * evt - {Object} The event object
     * soft - {Boolean} If soft is true, we will just move the popup to a location
     *     that can't be seen, otherwise we will really close it.
     */ 
    hidePopup:function(evt, soft){
        if(document.getElementById('geozetPopup')){
            $('#geozetPopup').css('left','-999999px');
        }
        if (!soft) {
            // clear content, needed for print!
            Ext.get('geozetPopup').update('');
            if (this.popuppedFeature) {
                var featureElement=Ext.get(this.popuppedFeature.id);
                if (featureElement){
                    featureElement.focus();
                }
            }
            this.removeActiveState();
            this.popuppedFeature=null;
            this.itemLength=0;
            this.itemCurrent=1;
        }
        return false;
    },

    /**
     * Method: move
     * When the map is moved, we need to update the popup.
     */
    move:function(){
        // it's possible that there is a popover showing, but user is moving map 
        // with cursor keys: just hide an existing popover
        this.hidePopover();
        // if there is a popup move it
        if (this.popuppedFeature) {
            // during clustering all features are removed, so theoretically
            // it's possible that a clustered feature isn't a clustered feature anymore
            // but it can be split into its sub features
            // that's why we check on xy of the features
            if (!this.popuppedFeature.layer){
                // we have had a reclustering ...
                // check if we can find a feature which is still on the same place
                var features = this.layer.features;
                var x = this.popuppedFeature.geometry.x;
                var y = this.popuppedFeature.geometry.y;
                var newPopuppedFeature=null;
                for (var i=0;i<features.length;i++){
                    if(x==features[i].geometry.x && y==features[i].geometry.y){
                        newPopuppedFeature=features[i];
                    }
                }
                // let's close the old popup (we maybe have to open another one, because of subfeatures changed)
                // not earlier because we lose this.popuppedFeature with this
                this.hidePopup();
                if (newPopuppedFeature){
                    this.popuppedFeature=newPopuppedFeature;
                    this.openPopupFor(this.popuppedFeature);
                }            
            } else {
                var featurePos = this.featurePosOfPoint(this.popuppedFeature);
                // check if feature is in view AND still visisble
                if ( (!this.popuppedFeature.onScreen(true) || (featurePos === null)) ||
                    !this.popuppedFeature.layer.calculateInRange() )
                {
                    // if not, remove popup, soft since we want the feature to come back again after panning
                    // it back into the viewport
                    this.hidePopup(null, true);
                } else {
                    var height = $('#geozetPopup').height();
                    // overcome ghost popup divs in the application
                    if (height > 20) {
                        var left = featurePos.left-10;
                        var top = featurePos.top-height-20;
                        $('#geozetPopup').css({'top' : top, 'left' : left});
                    }
                }
            }
        }
    },

    /**
     * Function: featurePosOfPoint
     * Get the pixel location of a feature.
     *
     * Parameters:
     * feature - {<OpenLayers.Feature.Vector>}
     *
     * Returns:
     * {Object} With x and y properties with the pixel location. 
     */
    featurePosOfPoint: function(feature) {
        if(feature.geometry != null) {
            var lonlat = new OpenLayers.LonLat(feature.geometry.x, feature.geometry.y)
            var OpenLayersPixel = feature.layer.map.getPixelFromLonLat(lonlat);
            var posMap = Ext.get(this.map.div).parent().getXY();
            var pos = {};
            pos.left = parseInt(OpenLayersPixel.x + posMap[0], 10)-Math.round(this.iconSize['default'].w/2);
            pos.top = parseInt(OpenLayersPixel.y + posMap[1], 10)-Math.round(this.iconSize['default'].h/2);
            return pos;
        }
        else {
            return null;
        }
    },
    
    /**
     * APIMethod: destroy
     * Clean up.
     */
    destroy: function() {
        if (this.map && this.map.events) {
            this.map.events.unregister('move', this, this.move);
        }
        this.map=null;
        this.layer=null;
        this.popuppedFeature=null;
        this.feature=null;
        Ext.destroy(this.popupDiv);
        this.popupDiv=null;
        Ext.get(this.popoverDiv).un('click', this.onPopoverClick, this);
        Ext.destroy(this.popoverDiv);
        this.popoverDiv=null;
        this.attributes = null;
        this.templates = null;
        this.clusterZoom = null;
        this.iconSize = null;
    },

    CLASS_NAME: "Geozet.Popup"

});

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
 * Class: Geozet.Renderer.Anchor
 * A renderer for vector layers which will create anchors in the DOM.
 * This will mean the features are accessible by keyboard.
 *
 * Inherits:
 *  - <OpenLayers.Renderer>
 */
Geozet.Renderer.Anchor = OpenLayers.Class(OpenLayers.Renderer, {

    /**
     * Property: imgPath
     * {String} The base url for icon images.
     */
    imgPath: null,

    /**
     * Property: iconSize
     * {Object} An object with the sizes of icons for different classes.
     */
    iconSize: null,

    /**
     * Property: classificationInfo
     * {Object} Configuration object for the classification.
     */
    classificationInfo: null,

    /**
     * Constructor: Geozet.Renderer.Anchor
     *
     * Parameters:
     * containerID - {<String>}
     * options - {Object} options for this renderer. 
     */
    initialize: function(containerID, options) {
        OpenLayers.Renderer.prototype.initialize.apply(this, arguments);
        OpenLayers.Util.extend(this, options);
        this.root = this.container;
    },

    /**
     * APIMethod: destroy
     */
    destroy: function() {
        this.clear();
        this.root = null;
        this.imgPath = null;
        this.iconSize = null;
        this.classificationInfo = null;
        OpenLayers.Renderer.prototype.destroy.apply(this, arguments);
    },

    /**
     * Method: drawFeature
     * Draw the feature.  The optional style argument can be used
     * to override the feature's own style.  This method should only
     * be called from layer.drawFeature().
     *
     * Parameters:
     * feature - {<OpenLayers.Feature.Vector>}
     * style - {<Object>}
     *
     * Returns:
     * {Boolean} true if the feature has been drawn completely, false if not,
     *     undefined if the feature had no geometry
     */
    drawFeature: function(feature, style) {
        if(style == null) {
            style = feature.style;
        }
        if (feature.geometry) {
            var bounds = feature.geometry.getBounds();
            if(bounds) {
                if (!bounds.intersectsBounds(this.extent)) {
                    style = {display: "none"};
                }
                // clusters are rendered differently
                if (feature.cluster !== undefined) {
                    style.cluster = true;
                }
                var popup = Geozet.Viewer.getPopup();
                var title = null;
                if (popup) {
                    title = popup.getTitleForFeature(feature);
                }
                var rendered = this.drawGeometry(feature.geometry, style, feature.id, title);
                if(style.display != "none" && style.label && rendered !== false) {
                    // we do not yet support labelXOffset and labelYOffset, since we do not need it as yet
                    this.drawText(feature.id, style, location);
                } else {
                    this.removeText(feature.id);
                }
                return rendered;
            }
        }
    },

    /**
     * Method: clear
     * Remove all the elements from the root
     */
    clear: function() {
        if (this.root) {
            while (this.root.childNodes.length > 0) {
                this.root.removeChild(this.root.firstChild);
            }
        }
    },

    /**
     * Method: getFeatureIdFromEvent
     *
     * Parameters:
     * evt - {Object} An <OpenLayers.Event> object
     *
     * Returns:
     * {String} The featureId of the object where the event happened
     */
    getFeatureIdFromEvent: function(evt) {
        var target = evt.target;
        var useElement = target && target.correspondingUseElement;
        var node = useElement ? useElement : (target || evt.srcElement);
        var featureId = node._featureId;
        return featureId;
    },

    /**
     * APIMethod: supported
     *
     * Returns:
     * {Boolean} Whether or not the browser supports this renderer
     */
    supported: function() {
        return true;
    },

    /**
     * Method: setExtent
     *
     * Parameters:
     * extent - {<OpenLayers.Bounds>}
     * resolutionChanged - {Boolean}
     *
     * Returns:
     * {Boolean} true to notify the layer that the new extent does not exceed
     *     the coordinate range, and the features will not need to be redrawn.
     *     False otherwise.
     */
    setExtent: function(extent, resolutionChanged) {
        OpenLayers.Renderer.prototype.setExtent.apply(this, arguments);
        var resolution = this.getResolution();
        this.left = -extent.left / resolution;
        this.top = extent.top / resolution;
        return false;
    },

    /**
     * Method: drawText
     * This method is only called by the renderer itself.
     *
     * Parameters:
     * featureId - {String}
     * style -
     * location - {<OpenLayers.Geometry.Point>}
     */
    drawText: function(featureId, style, location) {
        var node = document.getElementById(featureId);
        if (node && style.label !== "undefined") {
            if (node.children.length === 1) {
                var text = document.createElement('span');
                text._featureId = featureId;
                text.innerHTML = style.label;
                node.appendChild(text);
            } else if (node.children.length == 2) {
                node.children[1].innerHTML = style.label;
            }
        }
    },

    /**
     * Method: eraseGeometry
     * Erase a geometry from the renderer. We look for a node with the
     *     featureId and remove it from the DOM.
     *
     * Parameters:
     * geometry - {<OpenLayers.Geometry>}
     * featureId - {String}
     */
    eraseGeometry: function(geometry, featureId) {
        var element = document.getElementById(featureId);
        if (element && element.parentNode) {
           element.parentNode.removeChild(element);
        }
    },

    /**
     * Method: removeText
     * Removes a label
     *
     * Parameters:
     * featureId - {String}
     */
    removeText: function(featureId) {
        // since text is part of the actual DOM element, we do not need to remove it
    },

    /**
     * Function: getLength
     * Get the length of a cluster label. 1000 will yield 4, 10000 will yield 5. This
     * is used for the proportional cluster symbols.
     *
     * Parameters:
     * label - {String}
     *
     * Returns:
     * {Integer}
     */
    getLength: function(label) {
        var len = ("" + label).length;
        if (len > 5) {
            len = 5;
        }
        return len;
    },

    /**
     * Method: appendImage
     * Append the img tag to the anchor node and set its source.
     *
     * Parameters:
     * node - {DOMElement}
     * style - {Object}
     */
    appendImage: function(node, style) {
        var extension = Ext.isIE6 ? "gif" : "png";
        var baseName = '';
        var cls = '';
        // serverside clusters will show as a cluster with a count of 1
        if (style.cluster === true && (style.label > 1 || style.serverside === true)) {
            cls = baseName = style.cssClass + "-" + this.getLength(style.label);
            if (style.popupActive === "true") {
                cls += ' active'; 
            }
        } else {
            baseName = style.cssClass;
            if (style.popupActive === "true") {
                cls = 'active';
            }
        }
        node.className = cls;
        var imgUrl = this.imgPath + baseName + "." + extension;
        var title = this.classificationInfo[style.cssClass].label;
        var icon = null;
        if (node.childNodes.length < 1) {
            icon = document.createElement('img');
            node.appendChild(icon);
        } else {
            icon = node.childNodes[0];
        }
        icon.setAttribute("alt", title);
        icon.setAttribute("src", imgUrl);
    },

    /**
     * Function: getIconSize
     * Get the size of the icon for a certain feature.
     *
     * Parameters:
     * style - {Object} The style object
     *
     * Returns:
     * {<OpenLayers.Size>}
     */
    getIconSize: function(style) {
        var key;
        if (style.cluster === true && style.label > 1) {
            key = style.cssClass + "-" + this.getLength(style.label);
        } else {
            key = 'default';
        }
        return this.iconSize[key];
    },

    /**
     * Method: drawGeometry
     * Draw the geometry, creating new nodes, setting featureId on the node. 
     *     This method should only be called by the renderer itself.
     *
     * Parameters:
     * geometry - {<OpenLayers.Geometry>}
     * style - {Object}
     * featureId - {String}
     * title - {String}
     *
     * Returns:
     * {Boolean} true if the geometry has been drawn completely; null if
     *     incomplete; false otherwise
     */
    drawGeometry: function(geometry, style, featureId, title) {
        var rendered = false;
        if (geometry instanceof OpenLayers.Geometry.Point) {
            var iconSize = this.getIconSize(style);
            var node = document.getElementById(featureId);
            if (node) {
                if (style.display != "none") {
                    var resolution = this.getResolution();
                    var x = Math.round((geometry.x / resolution + this.left) - iconSize.w/2);
                    var y = Math.round((this.top - geometry.y / resolution) - iconSize.h/2);
                    this.appendImage(node, style);
                    node.style.left = x + "px";
                    node.style.top = y + "px";
                    rendered = true;
                }
            } else {
                if (style.display == "none") { 
                    return rendered; 
                } else {
                    node = document.createElement("a");
                    node.setAttribute('title', title);
                    node.setAttribute('href', '#');
                    node.id = featureId;
                    this.appendImage(node, style);
                    node._featureId = featureId;
                    node.childNodes[0]._featureId = featureId;
                    var resolution = this.getResolution();
                    var x = Math.round((geometry.x / resolution + this.left) - iconSize.w/2);
                    var y = Math.round((this.top - geometry.y / resolution) - iconSize.h/2);
                    node.style.left = x + "px";
                    node.style.top = y + "px";
                    this.root.appendChild(node);
                    rendered = true;
                }
            }
            if (rendered == false) {
                this.root.removeChild(node);
            }
            return rendered;
        }
    },

    CLASS_NAME: "GeoZet.Renderer.Anchor"

});

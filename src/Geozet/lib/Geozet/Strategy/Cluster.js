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
 * Class: Geozet.Strategy.Cluster
 * We have adapted the OpenLayers cluster strategy a bit to help it serve
 * our needs better. When creating a cluster, it should get the proper cssClass.
 * Also, only visible subitems should be taken into account when counting the
 * number of subfeatures. The cluster function should even be called when
 * zoomChanged is false, otherwise features will start disappearing. Lastly,
 * when the visibility of themes is changed, the clusters need to be updated.
 * This means changing the count, but also having clusters change into normal
 * features again when they only have 1 visible subfeature.
 *
 * Inherits:
 *  - <OpenLayers.Strategy.Cluster>
 */
Geozet.Strategy.Cluster = OpenLayers.Class(OpenLayers.Strategy.Cluster, {

    /**
     * Property: attributeName
     * {String} The name of the attribute which needs to be set on the cluster
     * feature's attributes.
     */
    attributeName: null,

    /**
     * Property: attributeValue
     * {String} The value of the attribute which needs to be set on the cluster
     * feature's attributes.
     */
    attributeValue: null,

    /**
     * Constructor: Geozet.Strategy.Cluster
     *
     * Parameters:
     * options - {Object} options for this strategy.
     */
    initialize: function(options) {
        OpenLayers.Strategy.Cluster.prototype.initialize.apply(this, [options]);
    },

    /**
     * Method: cluster
     * Cluster features based on some threshold distance.
     *
     * Parameters:
     * event - {Object} The event received when cluster is called as a
     *     result of a moveend event.
     */
    cluster: function(event) {
        OpenLayers.Strategy.Cluster.prototype.cluster.apply(this, [event]);
        // do some postprocessing
        var features = this.layer.features;
        if (Geozet.Viewer.getLegend()) {
            var state = Geozet.Viewer.getLegend().getState();
            for (var key in state) {
                this.onUpdateThemeVisibility(key, state[key]);
            }
        }
    },

    /**
     * Method: addToCluster
     * Add a feature to a cluster.
     *
     * Parameters:
     * cluster - {<OpenLayers.Feature.Vector>} A cluster.
     * feature - {<OpenLayers.Feature.Vector>} A feature.
     */
    addToCluster: function(cluster, feature) {
        cluster.cluster.push(feature);
        if (feature.style == null || feature.style.display != "none") {
            cluster.attributes.count += 1;
        }
    },

    /**
     * Method: createCluster
     * Given a feature, create a cluster.
     *
     * Parameters:
     * feature - {<OpenLayers.Feature.Vector>}
     *
     * Returns:
     * {<OpenLayers.Feature.Vector>} A cluster.
     */
    createCluster: function(feature) {
        var cluster = OpenLayers.Strategy.Cluster.prototype.createCluster.apply(this, [feature]);
        var attributes = {};
        attributes[this.attributeName] = this.attributeValue;
        OpenLayers.Util.extend(cluster.attributes, attributes);
        return cluster;
    },

    /**
     * APIMethod: onUpdateThemeVisibility
     * When the visiblity of themes changes, the clusters need to be updated.
     * In the future, this should be done using the new OpenLayers.Strategy.Filter
     *
     * Parameters:
     * cls - {String} The css class
     * visible - {Boolean} Should the affected css class be hidden or not?
     */
    onUpdateThemeVisibility: function(cls, visible) {
        for (var i=0, len=this.layer.features.length; i<len; i++) {
            var feature = this.layer.features[i];
            var cssClass = null;
            var attributes = null;
            if (feature.cluster) {
                var cnt = 0;
                for (var j=0, lenj=feature.cluster.length; j<lenj; j++) {
                    var subFeature = feature.cluster[j];
                    if (subFeature.attributes[this.attributeName] === cls) {
                        if (visible === false) {
                            if (subFeature.style == null) {
                                subFeature.style = {};
                            }
                            subFeature.style.display = "none";
                        } else {
                            cssClass = subFeature.attributes[this.attributeName];
                            attributes = subFeature.attributes;
                            cnt++;
                            delete subFeature.style;
                        }
                    } else if (subFeature.style == null || subFeature.style.display != "none") {
                        cssClass = subFeature.attributes[this.attributeName];
                        attributes = subFeature.attributes;
                        cnt++;
                    }
                }
                if (cnt !== feature.attributes.count) {
                    feature.attributes.count = cnt;
                    if (cnt === 1) {
                        feature.attributes = OpenLayers.Util.applyDefaults(feature.attributes, attributes);
                        feature.attributes[this.attributeName] = cssClass;
                    } else {
                        feature.attributes[this.attributeName] = this.attributeValue;
                    }
                    if (cnt === 0) {
                        if (feature.style == null) {
                            feature.style = {};
                        }
                        feature.style.display = "none";
                    } else if (feature.style) {
                        delete feature.style;
                    }
                    feature.layer.drawFeature(feature);
                }
           } else {
               if (visible === false && feature.attributes[this.attributeName] === cls) {
                   if (!feature.style) {
                       feature.style = {};
                   }
                   feature.style.display = "none";
                   feature.layer.drawFeature(feature);
               } else if (feature.style && visible === true && feature.attributes[this.attributeName] === cls) {
                   delete feature.style;
                   feature.layer.drawFeature(feature);
               }
           }
        }
    },

    CLASS_NAME: 'Geozet.Strategy.Cluster'

});

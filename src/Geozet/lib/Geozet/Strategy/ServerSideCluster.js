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
 * Class: Geozet.Strategy.ServerSideCluster
 * This strategy will make sure that the server-side cluster layers will
 * have the correct number of features in the cluster features according to 
 * the visibility of the filters.
 *
 * Inherits:
 *  - <OpenLayers.Strategy>
 */
Geozet.Strategy.ServerSideCluster = OpenLayers.Class(OpenLayers.Strategy, {

    /**
     * Property: attribute
     * {String} The name of the attribute which is used for the count. 
     */
    attribute: null,

    /**
     * Property: prefix
     * {String} The server-side clusters have attributes per filter type with
     *     the number of sub features in that filter category. Those attributes
     *     start with a prefix, to which an underscore and the filter category is 
     *     appended.
     */
    prefix: null,

    /**
     * Constructor: Geozet.Strategy.ServerSideCluster
     *
     * Parameters:
     * options - {Object} options for this strategy.
     */
    initialize: function(options) {
        OpenLayers.Strategy.prototype.initialize.apply(this, [options]);
    },

    /**
     * Method: activate
     * Make sure the numbers are correct before the features are added to the
     * layer.
     */
    activate: function() {
        this.layer.events.on({
            "beforefeaturesadded": this.updateVisibility,
            scope: this
        });
    },

    /**
     * Method: deactivate
     * Unregister the beforefeaturesadded event listener.
     */
    deactivate: function() {
        this.layer.events.un({
            "beforefeaturesadded": this.updateVisibility,
            scope: this
        });
    },

    /**
     * APIFunction: getFilterState
     * Return the filter state.
     *
     * Returns:
     * {Object} An object with the filter state.
     */
    getFilterState: function() {
        if (Geozet.Viewer.getLegend()) {
            return Geozet.Viewer.getLegend().getState();
        }
    },

    /**
     * Method: updateVisibility
     * Update the numbers depending on the state of the legend.
     */
    updateVisibility: function(evt) {
        var features = evt.features;
        var state = this.getFilterState();
        for (var key in state) {
            this.onUpdateThemeVisibility(key, state[key], features, true, true);
        }
    },

    /**
     * APIMethod: onUpdateThemeVisibility
     * When the visiblity of themes changes, the clusters need to be updated.
     *
     * Parameters:
     * cls - {String} The css class
     * visible - {Boolean} Should the affected css class be hidden or not?
     * features - {Array({<OpenLayers.Feature.Vector>})} The features to change.
     * noDraw - {Boolean} If true, do not redraw the features.
     * substractOnly - {Boolean} If true, only decrease the count, do not increase.
     */
    onUpdateThemeVisibility: function(cls, visible, features, noDraw, subtractOnly) {
        var featureCollection = features || this.layer.features;
        for (var i=0, len=featureCollection.length; i<len; i++) {
            var feature = featureCollection[i];
            var cnt = parseInt(feature.attributes[this.attribute]);
            if (feature.attributes[this.prefix + '_' + cls]) {
                var difference = parseInt(feature.attributes[this.prefix + '_' + cls]);
                if (visible === true) {
                    if (subtractOnly !== true) {
                        cnt += difference;
                    }
                } else {
                    cnt -= difference;
                }
                // tell the renderer we do not want to draw server-side clusters of count 0
                if (!feature.style && cnt === 0) {
                    feature.style = {display: 'none'};
                } else if (feature.style) {
                    delete feature.style;
                }
                feature.attributes[this.attribute] = cnt;
                if (noDraw !== true) {
                    feature.layer.drawFeature(feature);
                }
            }
        }
    },

    CLASS_NAME: 'Geozet.Strategy.ServerSideCluster'

});

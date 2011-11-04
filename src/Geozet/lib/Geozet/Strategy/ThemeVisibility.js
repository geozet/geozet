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
 * Class: Geozet.Strategy.ThemeVisibility
 * This strategy will make sure that the layer will initialize in the correct
 * state according to the visibility of items in the legend. It is not suitable
 * for server-side cluster layers.
 *
 * Inherits:
 *  - <OpenLayers.Strategy>
 */
Geozet.Strategy.ThemeVisibility = OpenLayers.Class(OpenLayers.Strategy, {

    /**
     * APIProperty: attributeName
     * {String} Feature attribute to use for determining visibility of a
     *     feature based on legend state.
     */
    attributeName: null,

    /**
     * Constructor: Geozet.Strategy.ThemeVisibility
     *
     * Parameters:
     * options - {Object} options for this strategy.
     */
    initialize: function(options) {
        OpenLayers.Strategy.prototype.initialize.apply(this, [options]);
    },

    /**
     * Method: activate
     * Before the features are added, make sure the state is taken into
     * account.
     */
    activate: function() {
        this.layer.events.on({
            "beforefeaturesadded": this.updateVisibility,
            scope: this
        });
    },

    /**
     * Method: deactivate
     * Unregister the event listener.
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
     * Make sure the visibility of features matches the state of the
     * filters.
     *
     * Parameters:
     * evt - {Object} The event object.
     */
    updateVisibility: function(evt) {
        var features = evt.features;
        var state = this.getFilterState();
        for (var i=0, len=features.length; i<len; i++) {
            var feature = features[i];
            var cls = feature.attributes[this.attributeName];
            if (state[cls] === false) {
                feature.style = {display: "none"};
            }
        }
    },

    CLASS_NAME: "Geozet.Strategy.ThemeVisibility"

});

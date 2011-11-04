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
 * Class: Geozet.Strategy.Sort
 * It is the job of the sort strategy to sort the features in a vector
 * layer, so that tabbing through features will get the desired user
 * experience (keyboard accessibility).
 *
 * Inherits:
 *  - <OpenLayers.Strategy>
 */
Geozet.Strategy.Sort = OpenLayers.Class(OpenLayers.Strategy, {

    /**
     * APIProperty: sortHeight
     * {Integer} The height of rows used for sorting the features in pixels.
     * Defaults to 25 pixels.
     */
    sortHeight: 25,

    /**
     * APIProperty: sortWidth
     * {Integer} The width of columns used for sorting the features in pixels.
     * Defaults to 15 pixels.
     */
    sortWidth: 15,

    /**
     * Constructor: Geozet.Strategy.Sort
     *
     * Parameters:
     * options - {Object} options for this strategy.
     */
    initialize: function(options) {
        OpenLayers.Strategy.prototype.initialize.apply(this, [options]);
    },

    /**
     * Method: activate
     * Set up strategy so that it can intervene with the feature order before
     * the features are actually added to the layer.
     */
    activate: function() {
        this.layer.events.on({
            "beforefeaturesadded": this.sort,
            scope: this
        });
    },

    /**
     * Method: deactivate
     * Unregister the event listener.
     */
    deactivate: function() {
        this.layer.events.un({
            "beforefeaturesadded": this.sort,
            scope: this
        });
    },

    /**
     * Method: sort
     * Sort the features based on a grid of rows and columns in pixels. The
     * features which are outside of the map will be added last.
     *
     * Parameters:
     * evt - {Object} The event object.
     */
    sort: function(evt) {
        var features = evt.features;
        // get the upper left corner of the map
        var map = this.layer.map;
        var bounds = map.getExtent();
        // convert sortHeight and sortWidth to meters
        var sortHeight = (this.sortHeight/map.getSize().h)*bounds.getHeight();
        var sortWidth = (this.sortWidth/map.getSize().w)*bounds.getWidth();
        var sort = [];
        var outOfMap = [];
        for (var i=0, len=features.length; i<len; i++) {
            var feature = features[i];
            var distX = feature.geometry.x - bounds.left;
            var distY = bounds.top - feature.geometry.y;
            var idx = Math.ceil(distY/sortHeight)-1;
            var idx_col = Math.ceil(distX/sortWidth)-1;
            if (idx_col < 0 || idx < 0) {
                outOfMap.push(feature);
            } else {
                if (!sort[idx]) {
                    sort[idx] = [];
                }
                if (!sort[idx][idx_col]) {
                    sort[idx][idx_col] = [];
                }
                sort[idx][idx_col].push(feature);
            }
        }
        var result = [];
        var printId = 0;
        for (var i=0, len=sort.length; i<len; i++) {
            var row = sort[i];
            if (row) {
                for (var j=0, lenj=row.length; j<lenj; j++) {
                    if (row[j]) {
                        for (var k=0, lenk=row[j].length; k<lenk; k++) {
                            printId++;
                            row[j][k].attributes.printid = printId;
                            result.push(row[j][k]);
                        }
                    }
                }
            }
        }
        for (var i=0, len=outOfMap.length; i<len; i++) {
            printId++;
            outOfMap[i].attributes.printid = printId;
        }
        // add the outOfMap features at the end
        evt.features = result.concat(outOfMap);
    },

    CLASS_NAME: "Geozet.Strategy.Sort"

});

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
 * Class: Geozet.Control.Click
 * A control with a click handler.
 *
 * Inherits:
 *  - <OpenLayers.Control>
 */
Geozet.Control.Click = OpenLayers.Class(OpenLayers.Control, {

    /**
     * APIProperty: clickFunction
     * {Function} Function which will be called on click.
     */
    clickFunction: null,

    /**
     * Property: autoActivate
     * {Boolean} Activate the control when it is added to a map.  Default is
     *     true.
     */
    autoActivate: true,

    /**
     * Constructor: Geozet.Control.Click
     *
     * Parameters:
     * options - {Object}
     */
    initialize: function(options) {
        OpenLayers.Control.prototype.initialize.apply(this, arguments);
        this.handler = new OpenLayers.Handler.Click(this, 
            {'click': this.clickFunction}
        );
    },

    CLASS_NAME: "Geozet.Control.Click"

});

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
 * Class: Geozet.Legend
 * A legend component with checkboxes for the filters.
 *
 * Inherits:
 *  - <Ext.BoxComponent>
 */
Geozet.Legend = Ext.extend(Ext.BoxComponent, {

    /**
     * APIProperty: visibleClasses
     * {Array({String})} A list of classes to initialize as visible
     * If not provided, all classes will be visible.
     */
    visibleClasses: null,

    /**
     * Property: map
     * {<OpenLayers.Map>}
     */
    map: null,

    /**
     * APIProperty: ulCls
     * {String} css class to use for the unordered list
     */
    ulCls: null,

    /**
     * Method: onRender
     * Is called when this components is rendered. Adds all content to the DOM.
     */
    onRender: function() {
        Geozet.Legend.superclass.onRender.apply(this, arguments);
        Ext.get(this.container).on('click', this.handleClick, this, {delegate: 'input'}); 
        this.header = Ext.DomHelper.append(this.container, {tag: 'h3'});
        var html = '<span>' + OpenLayers.i18n("legendTitle") + '</span><a class="geozetInfo" href="#">info</a>';
        Ext.get(this.header).update(html);
        var info = Ext.DomHelper.append(this.container, {tag: 'p', 'class': 'geozetInfo'});
        Ext.get(info).update(OpenLayers.i18n("legendInfoText"));
        var list = Ext.DomHelper.append(this.container, {tag: 'ul', 'class': this.ulCls});
        for (var key in this.legendConfig) {
            var filter = this.legendConfig[key];
            var item = Ext.DomHelper.append(list, {tag: "li", "class": filter.displayClass});
            if (filter.showCheckbox === false) {
                Ext.get(item).update(filter.label);
            } else {
                var elConfig = {tag: "input", "type": "checkbox", id: "geo-"+filter.displayClass};
                if (this.visibleClasses == null) {
                    elConfig.checked = "checked";
                } else if (this.visibleClasses.indexOf(filter.displayClass) !== -1) {
                    elConfig.checked = "checked";
                }
                var cb =Ext.DomHelper.append(item, elConfig);
                var label = Ext.DomHelper.append(item, {tag: "label", html: filter.label, "for": "geo-"+filter.displayClass});
            }
        }
    },

    /**
     * APIMethod: getState
     * Returns the current state of the legend component, so which filters are visible and
     * which ones are invisible.
     *
     * Returns:
     * {Object} The current state.
     */
    getState: function() {
        var state = {};
        Ext.each(Ext.get(this.container).query('[type=checkbox]'), function(cb) {
            var cls = cb.id.substring(4);
            state[cls] = cb.checked;
        });
        return state;
    },

    /**
     * APIMethod: destroy
     * Clean up.
     */
    destroy: function() {
        Ext.get(this.container).un('click', this.handleClick, this, {delegate: 'input'});
        Ext.destroy(this.container);
        this.container = null;
        Ext.destroy(this.header);
        this.header = null;
        this.map = null;
        Geozet.Legend.superclass.destroy.apply(this, arguments);
    },

    /**
     * Method: handleClick
     * Perform the necessary actions when somebody clicks on a checkbox.
     *
     * Parameters:
     * evt - {Object} The event object.
     */
    handleClick: function(evt) {
        var target = Ext.get(evt.target);
        var cls = target.parent().dom.className;
        var visible = target.dom.checked;
        var popup = Geozet.Viewer.getPopup();
        if (popup) {
            popup.hidePopup();
        }
        for (var i=0, len=this.map.layers.length; i<len; i++) {
            var layer = this.map.layers[i];
            if (layer instanceof OpenLayers.Layer.Vector) {
                for (var j=0, lenj=layer.strategies.length; j<lenj; j++) {
                    var strategy = layer.strategies[j];
                    if (strategy instanceof Geozet.Strategy.Cluster || 
                      strategy instanceof Geozet.Strategy.ServerSideCluster) {
                        strategy.onUpdateThemeVisibility(cls, visible, null, !layer.inRange);
                        break;
                    }
                }
            }
        }
    }

});

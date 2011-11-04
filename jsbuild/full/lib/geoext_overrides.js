// prevent destroy errors in IE
// see http://trac.geoext.org/ticket/357
GeoExt.ZoomSlider.prototype.unbind = function() {
    if(this.map && this.map.events) {
        this.map.events.un({
            zoomend: this.update,
            changebaselayer: this.initZoomValues,
            scope: this
        });
    }
};

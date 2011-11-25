// http://trac.openlayers.org/ticket/2850
OpenLayers.Control.prototype.destroy = function () {
    if(this.events) {
        if(this.eventListeners) {
            this.events.un(this.eventListeners);
        }
        this.events.destroy();
        this.events = null;
    }
    this.eventListeners = null;

    // eliminate circular references
    if (this.handler) {
        this.handler.destroy();
        this.handler = null;
    }
    if(this.handlers) {
        for(var key in this.handlers) {
            if(this.handlers.hasOwnProperty(key) &&
               typeof this.handlers[key].destroy == "function") {
                this.handlers[key].destroy();
            }
        }
        this.handlers = null;
    }
    if (this.map) {
        this.map.removeControl(this);
        this.map = null;
    }
    this.div = null;
};

// http://trac.openlayers.org/ticket/2806
// This has not been fixed in OpenLayers 2.10 as yet!
OpenLayers.Tile.Image.prototype.initImgDiv = function() {

        var offset = this.layer.imageOffset;
        var size = this.layer.getImageSize(this.bounds);

        if (this.layerAlphaHack) {
            this.imgDiv = OpenLayers.Util.createAlphaImageDiv(null,
                                                           offset,
                                                           size,
                                                           null,
                                                           "relative",
                                                           null,
                                                           null,
                                                           null,
                                                           true);
        } else {
            this.imgDiv = OpenLayers.Util.createImage(null,
                                                      offset,
                                                      size,
                                                      null,
                                                      "relative",
                                                      null,
                                                      null,
                                                      true);
        }

        this.imgDiv.className = 'olTileImage';

        /* checkImgURL used to be used to called as a work around, but it
           ended up hiding problems instead of solving them and broke things
           like relative URLs. See discussion on the dev list:
           http://openlayers.org/pipermail/dev/2007-January/000205.html

        OpenLayers.Event.observe( this.imgDiv, "load",
            OpenLayers.Function.bind(this.checkImgURL, this) );
        */
        this.frame.style.zIndex = this.isBackBuffer ? 0 : 1;
        this.frame.appendChild(this.imgDiv);
        this.layer.div.appendChild(this.frame);

        if(this.layer.opacity != null) {

            OpenLayers.Util.modifyDOMElement(this.imgDiv, null, null, null,
                                             null, null, null,
                                             this.layer.opacity);
        }

        // we need this reference to check back the viewRequestID
        this.imgDiv.map = this.layer.map;

        //bind a listener to the onload of the image div so that we
        // can register when a tile has finished loading.
        var onload = function() {

            //normally isLoading should always be true here but there are some
            // right funky conditions where loading and then reloading a tile
            // with the same url *really*fast*. this check prevents sending
            // a 'loadend' if the msg has already been sent
            //
            if (this.isLoading) {
                this.isLoading = false;
                this.events.triggerEvent("loadend");
            }
        };

        if (this.layerAlphaHack) {
            OpenLayers.Event.observe(this.imgDiv.childNodes[0], 'load',
                                     OpenLayers.Function.bind(onload, this));
        } else {
            OpenLayers.Event.observe(this.imgDiv, 'load',
                                 OpenLayers.Function.bind(onload, this));
        }


        // Bind a listener to the onerror of the image div so that we
        // can registere when a tile has finished loading with errors.
        var onerror = function() {

            // if this happens, we know we are running before OpenLayers.Util.onImageLoadError
            if (this.imgDiv._attempts === undefined) {
                this._runningFirst = true;
            }

            // If we have gone through all image reload attempts, it is time
            // to realize that we are done with this image. Since
            // OpenLayers.Util.onImageLoadError already has taken care about
            // the error, we can continue as if the image was loaded
            // successfully.
            if ((this._runningFirst === true && this.imgDiv._attempts >= OpenLayers.IMAGE_RELOAD_ATTEMPTS) ||
                (this._runningFirst === undefined && this.imgDiv._attempts > OpenLayers.IMAGE_RELOAD_ATTEMPTS)) {
                    onload.call(this);
            }
        };
        OpenLayers.Event.observe(this.imgDiv, "error",
                                 OpenLayers.Function.bind(onerror, this));
    };


// http://trac.openlayers.org/ticket/2065
// This has been fixed in OpenLayers 2.10
OpenLayers.Request.issue = function(config) {
    // apply default config - proxy host may have changed
    var defaultConfig = OpenLayers.Util.extend(
        this.DEFAULT_CONFIG,
        {proxy: OpenLayers.ProxyHost}
    );
    config = OpenLayers.Util.applyDefaults(config, defaultConfig);

    // create request, open, and set headers
    var request = new OpenLayers.Request.XMLHttpRequest();
    var url = config.url;
    if(config.params) {
        var paramString = OpenLayers.Util.getParameterString(config.params);
        if(paramString.length > 0) {
            var separator = (url.indexOf('?') > -1) ? '&' : '?';
            url += separator + paramString;
        }
    }
    if(config.proxy && (url.indexOf("http") == 0)) {
        if(typeof config.proxy == "function") {
            url = config.proxy(url);
        } else {
            url = config.proxy + encodeURIComponent(url);
        }
    }
    request.open(
        config.method, url, config.async, config.user, config.password
    );
    for(var header in config.headers) {
        request.setRequestHeader(header, config.headers[header]);
    }

    var events = this.events;

    // we want to execute runCallbacks with "this" as the
    // execution scope
    var self = this;

    request.onreadystatechange = function() {
        if(request.readyState == OpenLayers.Request.XMLHttpRequest.DONE) {
            var proceed = events.triggerEvent(
                "complete",
                {request: request, config: config, requestUrl: url}
            );
            if(proceed !== false) {
                self.runCallbacks(
                    {request: request, config: config, requestUrl: url}
                );
            }
        }
    };

    // send request (optionally with data) and return
    // call in a timeout for asynchronous requests so the return is
    // available before readyState == 4 for cached docs
    if(config.async === false) {
        request.send(config.data);
    } else {
        window.setTimeout(function(){
            if (request._aborted !== true) { 
                request.send(config.data);
            }
        }, 0);
    }
    return request;
};

// set alt for images
// no OpenLayers ticket, since this is not very generic
OpenLayers.Util.createImage = function(id, px, sz, imgURL, position, border,
                                       opacity, delayDisplay) {
    
    var image = document.createElement("img");

    //set generic properties
    if (!id) {
        id = OpenLayers.Util.createUniqueID("OpenLayersDiv");
    }
    if (!position) {
        position = "relative";
    }
    OpenLayers.Util.modifyDOMElement(image, id, px, sz, position,
                                     border, null, opacity);

    if(delayDisplay) {
        image.style.display = "none";
        OpenLayers.Event.observe(image, "load",
            OpenLayers.Function.bind(OpenLayers.Util.onImageLoad, image));
        OpenLayers.Event.observe(image, "error",
            OpenLayers.Function.bind(OpenLayers.Util.onImageLoadError, image));

    }
   
    //set special properties
    image.style.alt = id;
    image.galleryImg = "no";
    if (imgURL) {
        image.src = imgURL;
    }
    // bartvde
    image.alt = "";

    return image;
};

// http://trac.openlayers.org/ticket/2735
// This was fixed in OpenLayers 2.10
OpenLayers.Handler.Drag.prototype.mousedown = function (evt) {
    var propagate = true;
    this.dragging = false;
    if (this.checkModifiers(evt) && OpenLayers.Event.isLeftClick(evt)) {
        this.started = true;
        this.start = evt.xy;
        this.last = evt.xy;
        OpenLayers.Element.addClass(
            this.map.viewPortDiv, "olDragDown"
        );
        this.down(evt);
        this.callback("down", [evt.xy]);
        OpenLayers.Event.stop(evt);

        if(!this.oldOnselectstart) {
            this.oldOnselectstart = (document.onselectstart) ? document.onselectstart : OpenLayers.Function.True;
        }
        document.onselectstart = OpenLayers.Function.False;
        propagate = !this.stopDown;
    } else {
        this.started = false;
        this.start = null;
        this.last = null;
    }
    return propagate;
};

// see http://trac.openlayers.org/ticket/2761
// This was not added in OpenLayers 2.10
OpenLayers.Control.OverviewMap.prototype.setRectPxBounds = function(pxBounds) {
    var top = Math.max(pxBounds.top, 0);
    var left = Math.max(pxBounds.left, 0);
    var bottom = Math.min(pxBounds.top + Math.abs(pxBounds.getHeight()),
                          this.ovmap.size.h - this.hComp);
    var right = Math.min(pxBounds.left + pxBounds.getWidth(),
                         this.ovmap.size.w - this.wComp);
    var width = Math.max(right - left, 0);
    var height = Math.max(bottom - top, 0);
    if (this.maxRectSize && (width > this.maxRectSize || height > this.maxRectSize)) {
        OpenLayers.Element.hide(this.extentRectangle);
    }
    else if(width < this.minRectSize || height < this.minRectSize) {
        OpenLayers.Element.show(this.extentRectangle);
        this.extentRectangle.className = this.displayClass +
                                         this.minRectDisplayClass;
        var rLeft = left + (width / 2) - (this.minRectSize / 2);
        var rTop = top + (height / 2) - (this.minRectSize / 2);
        this.extentRectangle.style.top = Math.round(rTop) + 'px';
        this.extentRectangle.style.left = Math.round(rLeft) + 'px';
        this.extentRectangle.style.height = this.minRectSize + 'px';
        this.extentRectangle.style.width = this.minRectSize + 'px';
    } else {
        OpenLayers.Element.show(this.extentRectangle);
        this.extentRectangle.className = this.displayClass +
                                         'ExtentRectangle';
        this.extentRectangle.style.top = Math.round(top) + 'px';
        this.extentRectangle.style.left = Math.round(left) + 'px';
        this.extentRectangle.style.height = Math.round(height) + 'px';
        this.extentRectangle.style.width = Math.round(width) + 'px';
    }
    this.rectPxBounds = new OpenLayers.Bounds(
        Math.round(left), Math.round(bottom),
        Math.round(right), Math.round(top)
    );
};

// see http://trac.openlayers.org/ticket/2669
// This was fixed in OpenLayers 2.10
OpenLayers.Layer.Vector.prototype.assignRenderer = function()  {
    for (var i=0, len=this.renderers.length; i<len; i++) {
        var renderer = this.renderers[i];
        if (typeof renderer == "string") {
            var rendererClass = OpenLayers.Renderer[this.renderers[i]];
            if (rendererClass && rendererClass.prototype.supported()) {
                this.renderer = new rendererClass(this.div,
                    this.rendererOptions);
                break;
            }
        } else if (typeof renderer == "function" && renderer.prototype.supported()) {
            this.renderer = new renderer(this.div, this.rendererOptions);
        }
    }
};

// http://trac.openlayers.org/ticket/2693
// This was fixed in OpenLayers 2.10
OpenLayers.Renderer.prototype.eraseFeatures = function(features) {
    if(!(features instanceof Array)) {
        features = [features];
    }
    for(var i=0, len=features.length; i<len; ++i) {
        this.eraseGeometry(features[i].geometry, features[i].id);
        this.removeText(features[i].id);
    }
};

// http://trac.openlayers.org/ticket/2718
// This has not yet been added in OpenLayers 2.10!
OpenLayers.Protocol.WFS.v1_1_0.prototype.read = function(options) {
    OpenLayers.Protocol.prototype.read.apply(this, arguments);
    options = OpenLayers.Util.extend({}, options);
    OpenLayers.Util.applyDefaults(options, this.options || {});
    var response = new OpenLayers.Protocol.Response({requestType: "read"});
    if (this.method === "POST") {
        var data = OpenLayers.Format.XML.prototype.write.apply(
            this.format, [this.format.writeNode("wfs:GetFeature", options)]
        );
        response.priv = OpenLayers.Request.POST({
            url: options.url,
            callback: this.createCallback(this.handleRead, response, options),
            params: options.params,
            headers: options.headers,
            data: data
        });
    } else {
        response.priv = OpenLayers.Request.GET({
            url: options.url,
            callback: this.createCallback(this.handleRead, response, options),
            headers: options.headers,
            params: {
                REQUEST: 'GetFeature',
                VERSION: this.version,
                TYPENAME: options.featureType,
                BBOX: (options.filter && options.filter.type === OpenLayers.Filter.Spatial.BBOX)
                    ? options.filter.value.toBBOX() : null,
                OUTPUTFORMAT: options.outputFormat
            }
        });
    }      
    return response;
};

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
 * Class: Geozet.Vlakgericht
 * Class which handles the showing of a 
 * popup/dialog and the building an firing of a wfs request.
 * Received features will be show in a pageble list in popup.
 */
Geozet.Vlakgericht = OpenLayers.Class({

    /**
     * Property: ITEMSPERPAGE
     * {Integer} The number of items per page
     */
    ITEMSPERPAGE: 5,
    
    /**
     * Property: pageCurrent
     * {Integer} The current item page which is being paged.
     */
    pageCurrent: 1,
    
    /**
     * Property: pageLength
     * {Integer} number of pages with items
     */
    pageLength: 1,

    /**
     * Property: map
     * {<OpenLayers.Map>} The map object to interact with.
     */
    map: null,

    /**
     * Constructor: Geozet.Vlakgericht
     * Create a Vlakgericht object.
     * 
     * Parameters:
     * options - {Object} Optional object whose properties will be set on the
     *     control.
     */
    initialize: function(options) {
        OpenLayers.Util.extend(this, options);       
        var me = this;
        $('body').delegate('#geozetAlert','click', function(){ me.geozetAlertClick(me.map); return false; } );	
        $('body').delegate('#geozetStartForward','click', function() { me.go(true); return false; });
        $('body').delegate('#geozetStartBack','click', function() { me.go(false); return false; });	

    },
    
    /**
     * APIMethod: go
     * When paging multiple features, move forward or backward over
     * the pages with ITEMSPERPAGE items per page
     *
     * Parameters:
     * next  - {Boolean} If true, move forward. If false, move backward.
     */ 
    go: function(next){
        var to = next?this.pageCurrent+1:this.pageCurrent-1;
        if ((to > 0 ) && (to <= this.pageLength)) {
            // activate set's of li's in set's of ITEMSPERPAGE
            for(var i=1;i<=this.ITEMSPERPAGE;i++){
                $('#geozetFlatList li:nth-child('+(this.ITEMSPERPAGE*(this.pageCurrent-1)+i)+')').removeClass('active');
                $('#geozetFlatList li:nth-child('+(this.ITEMSPERPAGE*(to-1)+i)+')').addClass('active');
            }
            this.pageCurrent=to;
            // setting the number y in page numbers: "x van y"
            $('#geozetStart p.btn span').html(this.pageCurrent);
            // first remove disable class from both buttons
            $('#geozetStart p.btn a').removeClass().attr('disabled','');
            // now disable next button if needed
            if (this.pageCurrent == this.pageLength) { $('#geozetStartForward').addClass('inactive').attr('disabled','disabled'); }
            // or disable back button if needed
            if (this.pageCurrent == 1) { $('#geozetStartBack').addClass('inactive').attr('disabled','disabled'); }
        }
        return false;
    },
    
    /**
     * Function: geozetAlertClick
     * hides temporarily a popup (if available)
     *
     * Parameters:
     * req - {XMLHttpRequest} XMLHttpRequest Object with response
     *
     * Returns:
     * {boolean} false 
     */
    geozetAlertClick: function(map){
        // popover can get stuck, so hide it
        var popup = Geozet.Viewer.getPopup();
        if (popup) {
            popup.hidePopover();
        }
        // if open popup, hide it temporarily
       if($("#geozetPopup").html().length>0){
            $("#geozetPopup").hide();
       }
        
        if ( $('#geozetContent.start').length !== 0 ){
            $('#geozetMap').append('<div id="geozetStart"><div class="geozetStart"><a href="#" class="geozetClose" title="'+OpenLayers.i18n("geozetStartCloseTitle")+'">'+OpenLayers.i18n("geozetStartCloseText")+'</a><'+
                'h3 class="title-main">'+OpenLayers.i18n("vlakgerichtIntroTitle")+'</h3>'+        
                '<p>'+OpenLayers.i18n("vlakgerichtIntroText")+'</p>'+
                '<h3 id="vlakkentitle" class="title-main">'+OpenLayers.i18n("vlakgerichtBusyText")+'<img src="static/css/img/loading.gif"/></h3>'+
                '</div>');
            $('a.geozetClose').focus();
            // http://10.10.2.69:8080/geozetviewer/vlakbekendmakingen?bbox=176000,316000,178000,318000&format=json
            var bbox = null;
            var extent = map.getExtent();
            if (extent) {
                bbox = extent.toBBOX();
            }
            if (bbox !== null) {
                OpenLayers.Request.GET({
                    url: Geozet.config.vlakgerichteBekendmakingenService,
                    params: {format:'json', bbox:bbox},
                    scope: this,
                    success: this.handleResponse,
                    failure: this.handleError
                });
            }
        }
        // in IE6 mask does not get big enough, no css fix, so use js fix
        // http://standaardbeheer.geonovum.nl/view.php?id=3797
        if (Ext.isIE6) {
            Ext.get('geozetStart').setHeight(Ext.get('geozetMap').getHeight());
        }
        return false;
    },
    
    /**
     * Function: handleResponse
     * handle ajax response from AJAX request
     *
     * Parameters:
     * req - {XMLHttpRequest} XMLHttpRequest Object with response
     *
     * Returns:
     * {boolean} false 
     */
    handleResponse: function(req){
        var response = eval('(' + req.responseText + ')');
        if (response.vlakken){
            var vlakken = response.vlakken;
            var vlakkentitle = Geozet.config.templates['vlakgerichtTitle'].applyTemplate(vlakken);            
            var vlakkenhtml = "";
            for (var i=0; i<vlakken.length; i++){
                vlakken[i].activeClass = (i<this.ITEMSPERPAGE) ? 'active' : '';
                vlakkenhtml += Geozet.config.templates['vlakgerichtItem'].applyTemplate(vlakken[i]);
            }
            if(vlakken.length>0){
                this.pageCurrent = 1;
                // set page size
                this.pageLength = Math.ceil(vlakken.length/this.ITEMSPERPAGE);
                // append title and ul of list (ul only if there are bekendmakingen)
                $('#vlakkentitle').html(vlakkentitle).after('<ul id="geozetFlatList"></ul>');
                // append list with items     
                $('#geozetFlatList').append(vlakkenhtml);
                // buttons (if just one page, disable both buttons)
                $('div .geozetStart').append(Geozet.config.templates['vlakgerichtFooter'].applyTemplate({pageLength: this.pageLength}));
            }else{
                $('#vlakkentitle').html(OpenLayers.i18n("vlakgerichtNoResults"));
                this.pageLength = 1;
            }
        }
        else{
            // fail silently
            // alert("geen vlakken ontvangen");
        }
        return false;
    },
    
    /**
     * private function: handleError -- handles errors of AJAX request,
     * at this moment fail silently 
     *  
     * Parameters:
     * req - {XMLHttpRequest} XMLHttpRequest Object with response
     */
    handleError: function(req){
        $('#vlakkentitle').html(OpenLayers.i18n("vlakgerichtError"));
        this.pageLength = 1;
        return false;
    },
    
    /**
     * APIMethod: destroy
     * Clean up.
     */
    destroy: function() {
        this.map=null;
    },

    CLASS_NAME: "Geozet.Vlakgericht"
    
});

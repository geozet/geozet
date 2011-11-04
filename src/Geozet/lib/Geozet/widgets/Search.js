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
 * Class: Geozet.Search
 * 
 * Class which hanldes interaction with the geocoder, and builds 
 * the result lists.
 */

Geozet.Search = OpenLayers.Class({

    /**
     * Property: config
     * {Object} Geozet Configuration object with for example url's
     * for services etc
     */ 
    config: null,
    
    /**
     * Property: gazetteerUrl
     * {String} url of gazetteer service
     */ 
    gazetteerUrl: null,
    
    /**
     * Property: searchFormId
     * {String} id of form which is actually used
     */    
    searchFormId: null,

    /**
     * Constructor: Geozet.Search
     * Create the Search object which 
     * 
     * Parameters:
     * options - {Object} Optional object whose properties will be set on the
     *     control.
     */
    initialize: function(config, map) {
        this.config = config;
        this.map = map;
        this.gazetteerUrl =  this.config['gazetteer'].url;
        this.gazetteerParam =  this.config['gazetteer'].param;
        Ext.get('geozetContent').on('submit', this.onSubmit, this, {delegate: 'form'});
        // stop keydown event on search input to prevent panning of map when using arrowkeys
        $('#geozetEnhancedSearch, #geozetCoreEntree').delegate('input', 'keydown', function (evt) { evt.stopPropagation(); });
        
        // attach click to the li-suggestions (if available)
        $('#geozetEnhancedSearch, #geozetCoreEntree').delegate('li/a', 'click', function () {
            // link example: <a href="#">noordwijk zh (plaats) <span class="x">90552.9</span> <span class="y">472845</span> <span class="z">8</span></a>
            var hash = $("span.hash", this).text();
            var x = $("span.x", this).text();
            var y = $("span.y", this).text();
            var z = $("span.z", this).text();
            if(x && y){
                map.setCenter(new OpenLayers.LonLat(x, y), z);
                $('#geozetStart').remove();
                 document.location.hash = hash;
            }
            else {
                alert("fout met coordinaten");
            }
            return false;
        });
    },

    /**
     * APIMethod: setSearchForm
     *
     * Parameters:
     * elementId - {String} id of element from which search is inited/fired
     */
    setSearchForm: function(elementId) {
        this.searchFormId = elementId;
    },
    
    /**
     * Function: onSubmit
     * Get the type of feature
     * 
     * Parameters:
     * evt - {<OpenLayers.Feature.Vector>}
     * elm - 
     *
     * Returns:
     * {boolean} false
     */
    onSubmit: function(evt, elm) {
        this.search(elm.id || evt.target.id);
        evt.stopEvent();
        return false;
    },

    /**
     * APIMethod: search
     * 
     * When paging multiple features, move forward or backward.
     *
     * Parameters:
     * source - {String} element Id of form that search is fired
     * searchString - {String} search string to sent to geocoder
     * 
     */ 
    search: function(source, searchString){
        this.setSearchForm(source);
        $('.geozetError, .geozetSuggestions').remove();
        // searchString either from parameter OR from Form input
        if(!searchString){ 
            searchString = $('#'+this.searchFormId+' input:first').val();
        }
        else{
            // rebuild search string for geocoder based on hash from url
            var urlItems = Geozet.config.urlitems;
            var hashItems = searchString.split('/');
            searchString=''; // empty searchString
            for ( var i=0;i<urlItems.length; i++ ){
                // if one item from the hash of the url in is the urlItems
                var index = $.inArray(urlItems[i], hashItems);
                if (index>=0) {
                        searchString += (hashItems[index+1] + ' ');
                }
            }
        }
        // search title is the title of the input of the search (== hint)
        var searchTitle = $('#'+source+" input:first").attr('title');
        if(!searchString || searchString.length<3 || searchString==searchTitle){
            this.showError(OpenLayers.i18n("shortSearchString"));
        }
        else{
            this.sendRequest(searchString);
        }
        return false;
    },

    /**
     * Method: showError
     * will show the error msg in current searchform
     * 
     * Parameters:
     * msg {String} - msg to show
     */  
    showError: function(msg) {
        //$('#'+this.searchFormId).children(':first-child').append('<p class="geozetError">'+msg+'</p>');
        $('#'+this.searchFormId).prepend('<p class="geozetError">'+msg+'</p>');
    },
    
    /**
     * Method: sendRequest
     * will send a GET request to the geocoder
     * 
     * Parameters:
     * searchString {String} - free form search string
     * handleResponse {Function} - optional (external) handle response
     *  function. If given, the handleResponse-function will be use,
     *  otherwise the this.handleResponse will be used
     * 
     * Returns:
     * boolean - false 
     * 
     */    
    sendRequest: function(searchString, handleResponse) { 
        var params = {request: 'geocode'};
        params[this.gazetteerParam] = searchString;
        if (searchString && searchString.length>0){            
            OpenLayers.Request.GET({
                url: this.gazetteerUrl,
                params: params,
                scope: this,
                success: (handleResponse) ? handleResponse : this.handleResponse,
                failure: this.handleError
            });
        }
        return false;
    },
    
    /**
     * Method: sendRequestFromHash
     * Special case of sending a POST request, based on the 
     * hash string from the viewer
     * 
     * Parameters:
     * hashAddressString {String} - hash like:
     *      #/provincie/ZUID-HOLLAND/gemeente/NOORDWIJKERHOUT/plaats/NOORDWIJKERHOUT/postcode/2211ZE/straat/Kraaierslaan/huisnummer/20/toevoeging/B
     * handleResponse {Function} - optional (external) handle response
     *  function. If given, the handleResponse-function will be use,
     *  otherwise the this.handleResponse will be used
     * 
     * Returns:
     * boolean - false 
     */
    sendRequestFromHash: function(hashAddressString, handleResponse) { 
        /*
        // example address response
        <xls:Address countryCode="NL">
            <xls:StreetAddress>
                <xls:Building number="20" subdivision="B"></xls:Building>
                <xls:Street>Kraaierslaan</xls:Street>
            </xls:StreetAddress>
            <xls:Place type="MunicipalitySubdivision">NOORDWIJKERHOUT</xls:Place>
            <xls:Place type="Municipality">NOORDWIJKERHOUT</xls:Place>
            <xls:Place type="CountrySubdivision">ZUID-HOLLAND</xls:Place>
            <xls:PostalCode>2211ZE</xls:PostalCode>
        </xls:Address>
        
        // example request
        <GeocodeRequest xmlns="http://www.opengis.net/xls" xsi:schemaLocation="http://www.opengis.net/xls http://schemas.opengis.net/ols/1.1.0/LocationUtilityService.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <Address countryCode="NL">
                <StreetAddress>
                    <Building number="23" subdivision="B"/>
                    <Street>riouwstraat</Street>
                </StreetAddress>
                <Place type="Municipality">haarlem</Place>
                <Place type="MunicipalitySubdivision">haarlem</Place>
                <PostalCode>2022ZJ</PostalCode>
            </Address>
        </GeocodeRequest>
        */
        
        var addressWords=hashAddressString.split('/');
        // build address object
        var address = {};
        address.street = {}
        address.street = this.findUrlHashValue(addressWords, "straat");
        address.building = {
            number:this.findUrlHashValue(addressWords, "huisnummer"), 
            subdivision:this.findUrlHashValue(addressWords, "toevoeging")
        };
        address.place = {};
        address.place.CountrySubdivision = this.findUrlHashValue(addressWords, "provincie");
        address.place.CountrySecondarySubdivision = null;
        address.place.Municipality = this.findUrlHashValue(addressWords, "gemeente");
        address.place.MunicipalitySubdivision = this.findUrlHashValue(addressWords, "plaats")
        address.postalCode = this.findUrlHashValue(addressWords, "postcode");
        // to xls-address to requestbody
        var xlsAddress = new Geozet.Format.XLSAddress("NL", address);
        var xlslusFormat = new Geozet.Format.XLSLUS();
        var requestBody = xlslusFormat.writeGeocodeRequest(xlsAddress);
        
        OpenLayers.Request.POST({
            url: this.gazetteerUrl,
            data:requestBody,
            scope: this,
            success: (handleResponse) ? handleResponse : this.handleResponse,
            failure: this.handleError
        });  
            
        return false;
    },
    
    /**
     * private function: findUrlHashValue - find a value from an array which
     * is formed by splitting the address hash on '/'
     *  
     * Parameters:
     * thisArray - {Array} 
     * keyToFind - {String}
     */
    findUrlHashValue: function(thisArray, keyToFind){
        var i = thisArray.indexOf(keyToFind);
        if (i>=0 && (i+1) < thisArray.length ){
            return thisArray[i+1];
        }
        else{
            return "";
        }
    },
    
    /**
     * CallBack function for search OR can be called from outside also
     * 
     * Parameters:
     * req - {XMLHttpRequest} XMLHttpRequest Object with response
     * returnCoords - Boolean if False or None map will be zoomed/panned
     *  if True map will not change, but coordinates will be returned
     * 
     * Returns:
     * {OpenLayers.LonLat} - if returnCoords == True  one hit/result 
     * Boolean - if other == False OR more or no results
     * 
     */
    handleResponse: function(req, returnCoords){
        // suggestions already removed these in this.search, 
        // BUT hitting submit too fast apparently the remove there is aborted ???
        $('.geozetError, .geozetSuggestions').remove();
        var responseText = req.responseText;
        if (responseText && (responseText.indexOf('FAILED') != -1 ||
            responseText.indexOf('Exception') != -1 ||
            responseText.indexOf('No more credits') != -1)) {
            // fail silently
            return false;
        }

        var xlslusFormat = new Geozet.Format.XLSLUS();
        var xlslus = xlslusFormat.read(req.responseXML || req.responseText);
        var hits=xlslus[0].numberOfGeocodedAddresses;       
        if (hits==0){
            // zero responses
            this.showError(OpenLayers.i18n("noLocationFound"));
        }
        else{
            // > 1 hit show suggestions
            if(hits>1){
                $('#'+this.searchFormId+' p.button').before('<h3 class="geozetSuggestions">'+OpenLayers.i18n("suggestionsTitle")+'</h3><ul class="geozetSuggestions"></ul>');
            }
            for (i=0;i<hits;i++){
                var suggestion='';
                var geom = xlslus[0].features[i].geometry;
                var address = xlslus[0].features[i].attributes.address;                 
                var plaats = address.place.MunicipalitySubdivision; // toont evt provincie afkorting
                var gemeente = address.place.Municipality;
                var prov = address.place.CountrySubdivision;
                var adres = '';
                var postcode = '';
                var hash = "";
                // determine zoom and hash
                var zoom = null;
                if (address.street && address.street.length>0){
                    adres = address.street+' - ';
                    hash += "/straat/"+ encodeURIComponent(address.street);
                    if (address.building){
                        var toevoeging = '';
                        if (address.building.subdivision){
                            toevoeging = address.building.subdivision
                        }
                        adres += address.building.number+toevoeging+' - ';
                        hash  += "/huisnummer/"+encodeURIComponent(address.building.number);
                        hash  += "/toevoeging/"+encodeURIComponent(toevoeging);
                    }
                    if(!zoom){zoom='adres'}
                }
                if (address.postalCode){
                    adres += address.postalCode+' - ';
                    hash = "/postcode/"+encodeURIComponent(address.postalCode)+hash;
                    if(!zoom){zoom='postcode'}
                }
                if(plaats){
                    suggestion=adres+plaats+' (plaats)';
                    if(!zoom){zoom='plaats'}
                }
                else if(gemeente){
                    suggestion=adres+gemeente+' (gemeente)';
                    if(!zoom){zoom='gemeente'}
                }
                else if(prov){
                    suggestion=prov+' (provincie)';
                    if(!zoom){zoom='provincie'}
                }
                if(!zoom){zoom='standaard'}
                // for hash
                if(plaats){
                    hash = "/plaats/"+encodeURIComponent(plaats)+hash;
                }
                if(gemeente){
                    hash = "/gemeente/"+encodeURIComponent(gemeente)+hash;
                }
                if(prov){
                    hash = "/provincie/"+encodeURIComponent(prov)+hash;
                }
                if(hits>1){
                    // hack to be able to handle results without geom
                    var x = geom?geom.x:150000;
                    var y = geom?geom.y:450000;
                    var z = geom?this.config.zoomScale[zoom]:this.config.zoomScale['provincie'];
                    $("ul.geozetSuggestions").append('<li><a href="#">'+suggestion+' <span class="hash">'+hash+'</span>'+' <span class="x">'+x+'</span> <span class="y">'+y+'</span> <span class="z">'+z+'</span></a></li>');
                    // set (calculated) height for the result div
                    var height = Math.max(Ext.get('geozetAside').getHeight(), Ext.get('geozetArticle').getHeight());
                    Ext.get('geozetContent').setHeight(height);
                }
                else{
                    // hack to be able to handle results without geom
                    var x = geom?geom.x:150000;
                    var y = geom?geom.y:450000;
                    var z = geom?this.config.zoomScale[zoom]:this.config.zoomScale['provincie'];
                    if (returnCoords === true) {
                        return {
                            center: new OpenLayers.LonLat(x, y),
                            zoom: z
                        };
                    } 
                    else 
                    {
                        this.map.setCenter(new OpenLayers.LonLat(x, y), z);
                        $('#geozetStart').remove();
                        document.location.hash=hash;
                    }
                }
            }
            $("ul.geozetSuggestions").show();
        }
        return false;
    },

    /**
     * private function: handleError -- handles errors of AJAX request,
     * at this moment fail silently     * 
     *  
     * Parameters:
     * req - {XMLHttpRequest} XMLHttpRequest Object with response
     */
    handleError: function(req){
        //alert('error in geocoder handling');
        return false;
    },


    /**
     * APIMethod: destroy
     * Clean up.
     */
    destroy: function() {
        this.searchFormId = null;
        Ext.get('geozetContent').un('submit', this.onSubmit, this, {delegate: 'form'});
        this.map=null;
        this.config=null;
        this.gazetteerUrl=null;
    },

    CLASS_NAME: "Geozet.Search"
});

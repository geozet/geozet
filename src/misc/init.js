Ext.onReady(function(){
    // create the Geozet Viewer
    Geozet.Viewer.create(Geozet.config);
});

var geozetENHANCED = {
    init: function() {
        OpenLayers.Lang.setCode('nl');
        this.resetHTML();
        this.delegation();
        this.autopopulate();
    },    
    resetHTML: function() {
        // LINK NAAR CORE VERSIE IN ENHANCED VERSIE (WEBRICHTLIJN: IEMAND MET JS ZONDER CSS BV)
        $('#geozetMap').append('<a class="geozetCSS" href="'+Geozet.config.coreOnly+'">'+OpenLayers.i18n("noJSText")+'</a>');
        
        // Vlakgerichtebekendmakinge blokje rechtsonderin
		$('#geozetMap').append('<div id="geozetAlert"><a href="#">'+OpenLayers.i18n("vlakgerichtTitle")+'</a><p>'+OpenLayers.i18n("vlakgerichtText")+'</p></div>');
        		
        // COPYRIGHT
        $('#geozetEnhanced').append('<div id="copy"></div>');
        // SCHAALSTOK
        if (Geozet.config.containers.scale === true) {
            $('#geozetMap').append('<div id="geozetScale"></div>');
        }
		
        // PAN ZOOMBAR
        if (Geozet.config.containers.panZoomBar === true) {
            $('#geozetMap').append('<div id="geozetPanZoomBar"></div>');
        }
        if (Geozet.config.containers.miniMap === true) {
            $('#geozetAsideEnhanced').append('<div id="geozetMiniMap"></div>');
        }
        if (Geozet.config.containers.filter === true) {
            $('#geozetAsideEnhanced').append('<div id="geozetFilter"></div>');
        }
        if ((Geozet.config.containers.miniMap === false) && (Geozet.config.containers.filter === false)) { 
            Geozet.config.staticMapSize = [948, 550];
            $('#geozetContent').addClass('geozetMapOnly');
        }
		
        // INDIEN DE MINIMAP IN DE HTML VOORKOMT DAN GAAN WE DEZE OOK VOORZIEN VAN DE JUISTE HTML
        if (Geozet.config.containers.miniMap === true) {
			$('#geozetMiniMap').html('<h2>'+OpenLayers.i18n("searchTitle")+'</h2><div class="geozetMiniBG"><div id="geozetMiniNL"></div></div><form id="geozetEnhancedSearch" action="#"><p><input type="text" title="'+OpenLayers.i18n("searchFieldTitle")+'" name="locatie-enhanced" id="locatie-enhanced" class="autopopulate" /></p><p class="button"><button type="submit">'+OpenLayers.i18n("searchButtonTitle")+'</button></p></form>');
		}
		
        
        // INTRODUCTIE UIT CORE VERSIE OVER ENHANCED KAART ZETTEN (INDIEN BEKEND ADRES DAN NIET TONEN)
        if ( $('#geozetContent.start').length !== 0 ){
            var startC = $('#geozetCore').html();
            $('#geozetCore, #geozetAsideCore').remove();
            if (document.location.hash === "" || document.location.hash === "#") {
                $('#geozetMap').append('<div id="geozetStart"><div class="geozetStart"><a href="#" class="geozetClose" title="'+OpenLayers.i18n("geozetStartCloseTitle")+'">'+OpenLayers.i18n("geozetStartCloseText")+'</a>'+startC+'</div>');
                $('#adres').addClass('autopopulate').attr('title',OpenLayers.i18n("searchFieldTitle"));
				$('a.geozetClose').focus();
            }
        }
    },
    delegation: function() {
        // OPEN LINK IN NIEUW WINDOW
        $('body').delegate('a.newWindow','click',function(){
            window.open(this.getAttribute('href'));
            return false;
        });
        // mouseover 'info' image
        var showInfo = function(){
            var left = $(this).position().left+20;
            var top = $(this).position().top;
            $(this).parent().next().css({'display' : 'block', 'left' : left, 'top' : top});
        };
        $('body').delegate('a.geozetInfo','mouseover', showInfo);
        $('body').delegate('a.geozetInfo','focus', showInfo); 
        // mouseout 'info' image
        var hideInfo = function() {
            // remove the hover class
            $(this).parent().next().css('display', 'none');
        };
        $('body').delegate('a.geozetInfo','mouseout', hideInfo); 
        $('body').delegate('a.geozetInfo','blur', hideInfo);
        // close and remove startpopover
        $('body').delegate('#geozetStart .geozetClose','click',function(){
            $('#geozetStart').remove();
            // if we closed a popup while opening this geozetStart, reopen it
            if($("#geozetPopup").html().length>0){
                $("#geozetPopup").show();
           }
            return false;
        });
		// ZODRA MEN IN DE KAART BEGINT TE TABBEN DAN LIGHTBOX WEGHALEN (review accessibility 29.9.2010 issue 9.4.1)
        $('body').delegate('a.geozetCSS','focus',function(){
			var geozetStart = $('#geozetStart');
            if ( geozetStart.length !== 0 ){
				geozetStart.remove();
			}
				
        });
    },
    autopopulate: function() {
        // AUTOMATISCH OMZETTEN VAN TITLE ATTRIBUUT SITE NAAR PREFILLED TEXT
        if(!document.getElementById||!document.createTextNode){return;}
        var arrInputs=$('input.autopopulate');
        var iInputs=arrInputs.length;
        var oInput;for(var i=0;i<iInputs;i++){oInput=arrInputs[i];if(oInput.type!='text'){continue;}
        
        if((oInput.value=='')&&(oInput.title!='')){oInput.value=oInput.title;$(oInput).addClass('default');}
        if(oInput.value==oInput.title){$(oInput).addClass('default');}
        
        $('input.autopopulate').focus(function(){if(this.value==this.title){this.value='';this.select();$(this).val('').removeClass('default');}});
        $('input.autopopulate').blur(function(){if(!this.value.length){this.value=this.title;$(this).addClass('default');} else {if(this.value!=this.title){$(this).removeClass('default');}}});}
    }
};
geozetENHANCED.init();

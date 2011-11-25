/* ======================================================================
    settings.js
   ====================================================================== */

Ext.namespace('Geozet.config');

// root path from where the icons for in the map can be retrieved
Geozet.config.imgPath = "static/img/";

OpenLayers.ImgPath = Geozet.config.imgPath; 
OpenLayers.IMAGE_RELOAD_ATTEMPTS = 2;
OpenLayers.ProxyHost = 'proxy.jsp?';

// urls
Geozet.config.bekendmakingenWFS = 'http://test.geodata.nationaalgeoregister.nl/pdok/wfs?';
Geozet.config.WMSC = 'http://test.geodata.nationaalgeoregister.nl/wmsc?';
Geozet.config.gazetteer = {url:"http://test.geodata.nationaalgeoregister.nl/geocoder/Geocoder?", param:"zoekterm"};
Geozet.config.vlakgerichteBekendmakingenService = "vlakbekendmakingen?";

OpenLayers.Lang.nl = OpenLayers.Util.extend(
    {
        'resizeToggleTitle': "Vergroot of verklein de kaart",
        'legendTitle': 'Onderwerpen',
        'legendInfoText': "U kunt één of meer onderwerpen op de kaart verbergen. Dit doet u door het onderwerp uit te vinken.",
        'overviewMapTitle': "Kaart van Nederland",
        'shortSearchString': "Geen resultaat. Vul een postcode of plaatsnaam in.",
        'noLocationFound': "Locatie niet gevonden",
        'suggestionsTitle': "Bedoelde u:",
        'panUpTitle': "Naar boven",
        'panLeftTitle': "Naar links",
        'panRightTitle': "Naar rechts",
        'panDownTitle': "Naar beneden",
        'zoominTitle': "Inzoomen",
        'zoomoutTitle': "Uitzoomen",
        'vlakgerichtTitle': "Overige bekendmakingen",
        'vlakgerichtText': "Bekijk de bekendmakingen die voor een gebied gelden en niet op de kaart worden weergegeven.",
        'vlakgerichtIntroTitle': "Bekendmakingen voor grotere gebieden",
        'vlakgerichtIntroText': "Naast locatie-specifieke bekendmakingen die voor &eacute;&eacute;n adres gelden, geven overheden ook bekendmakingen uit voor grotere gebieden, bijvoorbeeld voor een provincie.",
        'vlakgerichtBusyText': "Bekendmakingen worden opgehaald ...",
        'vlakgerichtNoResults': "Er zijn geen bekendmakingen gevonden op dit zoomnivo.<br/>Zoom in of uit, om opnieuw te zoeken.",
        'vlakgerichtError': "Er is een fout opgetreden bij het ophalen van de bekendmakingen voor grotere gebieden. Sluit dit venster om door te gaan.",
        'noJSText': "Wordt deze pagina niet goed weergegeven? Klik dan hier.",
        'searchTitle': "Zoek op locatie",
        'searchFieldTitle': "Postcode of plaatsnaam",
        'searchButtonTitle': "Zoek locatie",
        'geozetStartCloseTitle': "Sluiten",
        'geozetStartCloseText': "Sluit"
    }, 
    OpenLayers.Lang.nl
);

Geozet.config.classificationInfo = {
    'consument': {displayClass: "consument", label: "Consumentenzaken"},
    'maatschappij': {displayClass: "maatschappij", label: "Cultuur, sport en vrije tijd"},
    'gezin': {displayClass: "gezin", label: "Familie, jeugd en gezin"},
    'natuur': {displayClass: "natuur", label: "Natuur en milieu"},
    'overheid': {displayClass: "overheid", label: "Overheid en democratie"},
    'rechtspraak': {displayClass: "rechtspraak", label: "Rechtspraak en veiligheid"},
    'verkeer': {displayClass: "verkeer", label: "Verkeer, voertuigen en wegen"},
    'wonen': {displayClass: "wonen", label: "Wonen en leefomgeving"},
    'cluster': {displayClass: "cluster", showCheckbox: false, label: "Meerdere bekendmakingen"}
};

// attributes used by the popup
// if datamodel changes, change the values, not the keys
Geozet.config.popupAttributes = {
    'categorie': 'categorie',
    'count': 'count'
};

Geozet.config.templates = {
    "vlakgerichtTitle": new Ext.XTemplate('{length} bekendmaking',
        '<tpl if="length &gt; 1">',"en",'</tpl>'),
    "vlakgerichtItem": new Ext.Template('<li class="{activeClass}"><a href="{url}" class="extern">{titel}<span>Bron: {overheid}</span></a></li>'),
    "vlakgerichtFooter": new Ext.XTemplate('<p class="btn"><span>1</span> van {pageLength} ',
        '<a class="inactive" id="geozetStartBack" href="#" disabled="disabled">Terug</a>',
        '<a id="geozetStartForward" href="#"',
        '<tpl if="pageLength == 1">',' class="inactive" disabled="disabled"','</tpl>',
        '>Heen</a></p>'),
    "clusterPopover": new Ext.Template("<span></span><h3>{count} Bekendmakingen</h3><strong>Op deze locatie zijn meerdere bekendmakingen</strong>"),
    "clusterPopoverText": new Ext.Template("{count} bekendmakingen. Op deze locatie zijn meerdere bekendmakingen"),
    "featurePopover": new Ext.Template("<span></span><h3>{titel}</h3><strong><em>{onderwerp}</em> ({categorie:this.getLabel})</strong>",
        {getLabel: function(value) { return Geozet.config.classificationInfo[value].label; } }),
    "featurePopoverText": new Ext.Template("{titel} {onderwerp} ({categorie:this.getLabel})",
        {getLabel: function(value) { return Geozet.config.classificationInfo[value].label; } }),
    "serversideclusterPopover": new Ext.XTemplate('<span></span><h3>{totaal_aantal} Bekendmaking',
        '<tpl if="totaal_aantal &gt; 1">',"en",'</tpl>', '</h3><strong>in {layername} {title}</strong>'),
    "serversideclusterPopoverText": new Ext.XTemplate('{totaal_aantal} bekendmaking',
        '<tpl if="totaal_aantal &gt; 1">',"en",'</tpl>', ' in {layername} {title}'),
    "featurePopupHeader": new Ext.Template('<h2>{titel}</h2><a class="close" title="Sluiten" href="#">Sluiten</a><span class="geozetNeedle"></span>'),
    "featurePopupContent": new Ext.XTemplate(
        '<div><p><strong>{onderwerp:capitalize}</strong><br/>{categorie:this.getLabel}<br/>',
        '<tpl if="straat.length &gt; 0">',
        '{straat}, ',
        '</tpl>',
        '{plaats}<br/>',
        '{datum:this.getDate}',
        '</p><p>{beschrijving:this.getDescription}</p>',
        '<p><a class="extern" href="{url}">{overheid}</a></p></div>',
        {
            getDate: function(value) { 
                var date = Date.parseDate(value, "c");
                if (date) {
		    return date.format('d-m-Y');
                } 
            },
            getLabel: function(value) { return Geozet.config.classificationInfo[value].label; },
            getDescription: function(value) { return Ext.util.Format.htmlDecode(value); }
        }),
    "clusterPopupHeader": new Ext.Template('<h2>{count} resultaten</h2><a class="close" href="#"></a><span class="geozetNeedle"></span><ul class="cluster">'),
    "clusterPopupFooter": new Ext.Template('</ul><p class="btn"><span>1</span> van {count}<a href="#" id="geozetPopupBack" class="inactive">Terug</a><a href="#" id="geozetPopupForward">Heen</a></p>'),
    "clusterChildPopupHeader": new Ext.Template('<li class="{activeClass}"><h3>{titel}</h3>'),
    "clusterChildPopupFooter": new Ext.Template('</li>')
};

Geozet.config.iconSize = {
    'default': new OpenLayers.Size(21, 21),
    'cluster-1': new OpenLayers.Size(21, 21),
    'cluster-2': new OpenLayers.Size(24, 24),
    'cluster-3': new OpenLayers.Size(31, 31),
    'cluster-4': new OpenLayers.Size(41, 41),
    'cluster-5': new OpenLayers.Size(51, 51)
};

// css class to use on the <ul> created by the legend
Geozet.config.legendUlCls = 'geozetFilter';

// div for the copyright attribution text
Geozet.config.attributionDiv = 'copy';

// settings voor de overzichtskaart
Geozet.config.overviewDiv = 'geozetMiniNL';
Geozet.config.overview = {
    isSuitableOverview: function() { return true; },
    mapOptions: {projection: "EPSG:28992", theme: null, maxExtent: new OpenLayers.Bounds(0, 300000, 280000, 625000), maxResolution: 800},
    layers: [new OpenLayers.Layer.Image(null, "static/img/kaart-nl.gif", new OpenLayers.Bounds(7216,301377,284047,622933), new OpenLayers.Size(153, 179),
        {eventListeners: {'loadend': function(evt) { this.tile.imgDiv.alt = OpenLayers.i18n("overviewMapTitle"); } }})],
    minRectSize: 7,
    maxRectSize: 151,
    size: new OpenLayers.Size(153, 179)
};

Geozet.config.scaleBar = {
   id: 'geozetScale',
   minWidth: 70,
   maxWidth: 120,
   abbreviateLabel: true,
   divisions: 1,
   displayClass: 'olControlScaleBar'
};

// settings voor de hoofdkaart
Geozet.config.map = {
    projection: "EPSG:28992",
    units: "m",
    theme: null,
    maxExtent: new OpenLayers.Bounds(-65200.96, 242799.04, 375200.96, 683200.96),
    restrictedExtent: new OpenLayers.Bounds(0, 300000, 280000, 625000),
    resolutions: [/*3440.640, 1720.320,*/860.160, 430.080, 215.040, 107.520, 53.760, 26.880, 13.440, 6.720, 3.360, 1.680, 0.840, 0.420],
    controls: [
        new Geozet.Control.Click({clickFunction: function() {
                var popup = Geozet.Viewer.getPopup();
                if (popup) {
                    popup.hidePopup();
                }
            }
        }),
        new OpenLayers.Control.LoadingPanel(),
        new OpenLayers.Control.Navigation({zoomWheelEnabled: false}),
        new OpenLayers.Control.KeyboardDefaults()
    ]
};

Geozet.config.mapPanel = {
    center: new OpenLayers.LonLat(148037, 469056),
    zoom: 0
};

Geozet.config.backgroundLayer = new OpenLayers.Layer.WMS(null, Geozet.config.WMSC,
{layers: 'brtachtergrondkaart', format: 'image/png8'},
{buffer: 1, isBaseLayer: true, attribution: 'Kaartgegevens: © <a href="http://www.cbs.nl">CBS</a>, <a href="http://www.kadaster.nl">Kadaster</a>, <a href="http://openstreetmap.org">OpenStreetMap</a><span class="printhide">-auteurs (<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>).</span>'}

);

// title will be used in popover
Geozet.config.clusterLayer = {  
    'provincie': 
        {
            title: 'provincie',
            styleMap: new OpenLayers.StyleMap(
                {
                    'default': {
                        cluster: true,
                        serverside: true,
                        cssClass: "cluster",
                        label: "${totaal_aantal}"
                    }
                }
            ),
            renderers: [Geozet.Renderer.Anchor],
            rendererOptions: {iconSize: Geozet.config.iconSize, imgPath: Geozet.config.imgPath, classificationInfo: Geozet.config.classificationInfo},
            strategies: [
                new OpenLayers.Strategy.BBOX(),
                new Geozet.Strategy.ServerSideCluster({prefix: 'aantal', attribute: 'totaal_aantal'}),
                new Geozet.Strategy.Sort({sortHeight: Geozet.config.sortHeight})
            ],
            maxResolution: 1720.320,
            minResolution: 215.040,
            protocol: new OpenLayers.Protocol.WFS({
                url: Geozet.config.bekendmakingenWFS,
                version: "1.1.0",
                featureType: "prov_clr_cte_totalen",
                geometryName: "centroid",
                method: "GET",
                featureNS: "http://pdok.geonovum.nl"
            })
        },
    'gemeente':
        {
            title: 'gemeente',
            styleMap: new OpenLayers.StyleMap(
                {
                    'default': {
                        cluster: true,
                        serverside: true,
                        cssClass: "cluster",
                        label: "${totaal_aantal}"
                    }
                }
            ),
            renderers: [Geozet.Renderer.Anchor],
            rendererOptions: {iconSize: Geozet.config.iconSize, imgPath: Geozet.config.imgPath, classificationInfo: Geozet.config.classificationInfo},
            strategies: [
                new OpenLayers.Strategy.BBOX(),
                new Geozet.Strategy.ServerSideCluster({prefix: 'aantal', attribute: 'totaal_aantal'}),
                new Geozet.Strategy.Sort({sortHeight: Geozet.config.sortHeight})
            ],
            minResolution: 53.760,
            maxResolution: 107.520,
            protocol: new OpenLayers.Protocol.WFS({
                url: Geozet.config.bekendmakingenWFS,
                version: "1.1.0", 
                featureType: "gem_clr_cte_totalen",
                geometryName: "centroid",
                method: "GET",
                featureNS: "http://pdok.geonovum.nl"
            })
        },
    'wijk': 
        {
            title: '', // PDOK feature title already has Wijk in it!
            styleMap: new OpenLayers.StyleMap(
                {
                    'default': {
                        cluster: true,
                        serverside: true,
                        cssClass: "cluster",
                        label: "${totaal_aantal}"
                    }
                }
            ),
            renderers: [Geozet.Renderer.Anchor],
            rendererOptions: {iconSize: Geozet.config.iconSize, imgPath: Geozet.config.imgPath, classificationInfo: Geozet.config.classificationInfo},
            strategies: [
                new OpenLayers.Strategy.BBOX(),
                new Geozet.Strategy.ServerSideCluster({prefix: 'aantal', attribute: 'totaal_aantal'}),
                new Geozet.Strategy.Sort({sortHeight: Geozet.config.sortHeight})
            ],
            minResolution: 13.440,
            maxResolution: 26.880,
            protocol: new OpenLayers.Protocol.WFS({
                url: Geozet.config.bekendmakingenWFS,
                version: "1.1.0", 
                featureType: "wijk_clr_cte_totalen",
                geometryName: "centroid",
                method: "GET",
                featureNS: "http://pdok.geonovum.nl"
            })
        }
};

Geozet.config.bekendmakingenLayer = {
    styleMap: new OpenLayers.StyleMap(
        {
            'default': {
                cssClass: "${categorie}",
                label: "${count}",
                popupActive: "${active}"
            }
        }
    ),
    protocol: new OpenLayers.Protocol.WFS({
        url: Geozet.config.bekendmakingenWFS,
        featureType: "bekendmakingen_punt",
        geometryName: "locatie",
        version: "1.1.0",
        method: "GET",
        featureNS: "http://pdok.geonovum.nl"
    }),
    maxResolution: 6.720,
    minResolution: 0.210,
    renderers: [Geozet.Renderer.Anchor],
    rendererOptions: {iconSize: Geozet.config.iconSize, imgPath: Geozet.config.imgPath, classificationInfo: Geozet.config.classificationInfo},
    strategies: [
        new Geozet.Strategy.ThemeVisibility({attributeName: 'categorie'}),
        new Geozet.Strategy.Sort({sortHeight: Geozet.config.sortHeight}),
        new Geozet.Strategy.Cluster({attributeName: 'categorie', attributeValue: 'cluster', threshold: 2, distance: 21}),
        new OpenLayers.Strategy.BBOX()
        /*new Geozet.Strategy.Grid({safe: false, buffer: 0, tileSize: new OpenLayers.Size(768,768)})*/
    ]
};

// the id of the map div
Geozet.config.mapDiv = 'geozetMap';

// the id of the legend div
Geozet.config.legendDiv = 'geozetFilter';

// the id of the panzoombar div
Geozet.config.panZoomBarDiv = 'geozetPanZoomBar';

// config of the panzoombar
Geozet.config.panZoomBarOptions = {
    slideRatio: 0.33,
    sliderOptions: {
        vertical: true,
        height: 130
    }
};

// zoom level to zoom to if clicked on a cluster
Geozet.config.clusterZoom = {
    provincies: 3,
    gemeenten: 5,
    wijken: 7
};

Geozet.config.zoomScale = {
    adres: 11,
    postcode: 9,
    plaats: 7,
    gemeente: 7,
    provincie: 2,
    standaard: 8
};

Geozet.config.mapAutoHeight = true;
Geozet.config.mapAutoWidth = false;
// minimum size for the map
Geozet.config.staticMapSize = [688, 550];

// ordening van features in de kaart op basis van instelbare rijhoogte in pixels
Geozet.config.sortHeight = 50;

// urlitems are the items that can be in the url in the order of importance (provincie more important then gemeente):
Geozet.config.urlitems = ["provincie","gemeente","plaats","wijk","buurt","postcode","straat","huisnummer","straal"];

// WELKE CONTAINERS AAN OF UIT?
Geozet.config.containers =
{
    miniMap: true,
    filter: true,
    panZoomBar: true,
    scale: true,
    resizeMap: true
};

// GEOZET URL NAAR DE CORE INDIEN MEN TOCH JAVASCRIPT HEEFT AANSTAAN (WEBRICHTLIJN)
Geozet.config.coreOnly = '?coreonly=true';

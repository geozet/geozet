/**
 * @project Overheid.nl
 * @author	Stichting ICTU, e-Overheid voor Burgers
 * @since	march 2010
 * @name	Options: contrast & fontsize
 * @version	1
 */

if (window.addEventListener) window.addEventListener("load", init, false); 
else if (window.attachEvent) window.attachEvent("onload", init);
else window.onload = init;

var fontsizes = ['.5em','.6em','.7em','.8em','.9em'];
var cssContrast = 'contrast.css';
var fontsize;

/**
 * Initial loads
 */
function init()
{
	if(document.getElementById('menu'))
	{
		var obj = document.getElementById('menu');
		delExistingFontSizeControls(obj);
		if(attachFontSizeControls(obj))
		{
			if (!readCookie('fontsize')) createCookie('fontsize', 2);
			fontsize = readCookie('fontsize');
			setFontSize();
		}
		if(attachContrastControls(obj))
		{		
			setContrastCSSLocation();
			if (!readCookie('contrast')) createCookie('contrast', 'hoog');
			document.contrast = readCookie('contrast');
			changeContrast();
		}		
	}
}

/**
 * Set the fontsize of the body
 * Set global variable fontsize;
 */
function setFontSize()
{
	fontsize = parseFloat(fontsize);
	var size = 0;
	if(this.size != undefined) size = parseFloat(this.size);

	if(fontsizes[fontsize + size] != undefined)
	{
		document.body.style.fontSize = fontsizes[fontsize += size];
		createCookie('fontsize', fontsize);
	}
	return false;
}

/**
 * Delete hardcoded fontsize controls
 * @param {HTML divelement} header
 */
function delExistingFontSizeControls(header)
{
	var dl = header.getElementsByTagName('dl');
	if(dl.length > 0) dl[dl.length - 1].parentNode.removeChild(dl[dl.length - 1]);
}

/**
 * Attach fontsize controls tot header div
 * @param {HTML divelement} header
 */
function attachFontSizeControls(header)
{	
	var dt = document.createElement('dt');
	dt.appendChild(document.createTextNode('Tekstgrootte'));
	
	var a_kleiner = document.createElement('a');
	a_kleiner.appendChild(document.createTextNode('-'));
	a_kleiner.size = -1;
	a_kleiner.onclick = setFontSize;
	a_kleiner.href="#";
	
	var a_groter = document.createElement('a');
	a_groter.appendChild(document.createTextNode('+'));
	a_groter.size = 1;
	a_groter.onclick = setFontSize;
	a_groter.href="#";
	
	var dd_kleiner = document.createElement('dd');
	dd_kleiner.appendChild(a_kleiner);
	
	var dd_groter = document.createElement('dd');
	dd_groter.appendChild(a_groter);
	
	var dl = document.createElement('dl');
	dl.appendChild(dt);
	dl.appendChild(dd_kleiner);
	dl.appendChild(dd_groter);
	
	if(header.appendChild(dl)){
		return true;
	}else{
		return false;
	}
}

/**
 * Returns cookie according to name
 * @param {String} name
 */
function readCookie(name)
{
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) 
    {
        var c = ca[i];
        while (c.charAt(0) == ' ') 
            c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0) 
		{
			return c.substring(nameEQ.length, c.length);
		}
    }
    return null;
}

/**
 * Saves local cookie
 * @param {String} name
 * @param {String} value
 */
function createCookie(name, value)
{
    var exdate=new Date();
	exdate.setDate(exdate.getDate()+365);
	var expires = "; expires= " + exdate.toGMTString(); // 1 jaar
	var path = "; path=/";
	
	if(window.location.host.search('overheid.nl') >= 0) var domain = '; domain=overheid.nl'; // productie
	else if(window.location.host.search('overheid.nl') >= 0) var domain = '; domain=asp4all.nl'; // acceptatie
	else var domain = ""; // werkt niet in subdomain

	document.cookie = name + "=" + value + expires + path + domain;
}

/**
 * Attach contrast controls tot header div
 * @param {HTML divelement} header
 */
function attachContrastControls(header)
{
	if(document.getElementById('contrast'))
	{
		var a = document.getElementById('contrast');
	}
	else
	{
		var a = document.createElement('a');
		a.appendChild(document.createTextNode('Hoog contrast'));
	}


	a.onclick = changeContrast;
	a.id = "contrast";
	a.href = "#";
	
	document.contrast = 'normaal';
	
	if(header.appendChild(a)) return true;
	else return false;
}

/**
 * Change contrast text
 * Load or unload contrast stylesheet
 */
function changeContrast()
{
	var obj = document.getElementById('contrast');
	createCookie('contrast', document.contrast);
	
	// set contrast normaal -> hoog
	if(document.contrast == 'normaal')
	{
		if(attachStyleSheet(cssContrast))
		{
			document.contrast = 'hoog';
			obj.childNodes[0].nodeValue = 'Normaal contrast';
		}
	}
	// set contrast hoog -> normaal
	else
	{
		removeStyleSheet(cssContrast);
		document.contrast = 'normaal';
		obj.childNodes[0].nodeValue = 'Hoog contrast';
	}

	changeHeaderImage();
	return false;
}

function changeHeaderImage()
{
	var obj = document.getElementsByTagName('h1')[0];
	if(obj) var img = obj.getElementsByTagName('img')[0];
	if(img)
	{
		if(document.contrast == 'hoog') img.src = img.src.replace('overheid.nl.png','overheid.nl-zw.png');
		else img.src = img.src.replace('overheid.nl-zw.png','overheid.nl.png');
	}
}

/**
 * Loads extra stylesheet
 * @param {String} css
 */
function attachStyleSheet(css)
{
	var link = document.createElement('link');
	link.href = css;
	link.rel = 'stylesheet';
	link.type = 'text/css';
	
	var head = document.getElementsByTagName('head')[0];
	if (head.appendChild(link)) {
	    return true;
	} else {
	    return false;
	}
}

/**
 * Removes stylesheet
 * @param {String} css
 */
function removeStyleSheet(css)
{
	var link = document.getElementsByTagName('link');
	for(var i=0; i<link.length; i++)
	{
		if(link[i].href.search(css) >= 0)
		{
			if(link[i].parentNode.removeChild(link[i])) return true;
		}
	}
	return false;
}

/**
 * Set de location of the contrast stylesheet
 */
function setContrastCSSLocation()
{
	var link = document.getElementsByTagName('link');
	for(var i=0; i<link.length; i++)
	{
		if(link[i].href.search('main.css') >= 0) var tmpcss = link[i].href;
	}
	if(tmpcss) cssContrast = tmpcss.replace(/main.css/i, cssContrast)
	else cssContrast = 'css/' + cssContrast; /* Default map */
}

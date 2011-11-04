/**
 * @project Overheid.nl
 * @author	Stichting ICTU, e-Overheid voor Burgers
 * @since	march 2010
 * @name	Questionaire
 * @version	1
 */

// global variables
var messagefile = 'http://' + location.hostname + '/Templates/v1/bericht/bericht.html';
var cookieName = 'ONLbericht';

// onload
if (window.addEventListener) window.addEventListener("load", init, false); 
else if (window.attachEvent) window.attachEvent("onload", init);
else window.onload = init;

/**
 * First among equals
 */
function init()
{
	if(navigator.cookieEnabled)
	{
		if(!readMessageCookie(cookieName)) loadExternalFile(messagefile);
		setMessageStartlink();
	}
}

/**
 * Get the links in linkitems which should open the popup
 * link: <li class="open_popup"><a href="#">Open [...]</a></li>
 */
function setMessageStartlink()
{
	var li = document.getElementsByTagName('li');
	for(var i=0; i<li.length; i++)
	{
		if(li[i].className.search('open_popup') >= 0)
		{
			var a = li[i].getElementsByTagName('a')[0];
			if(a.href) messagefile = a.href;
			a.removeAttribute('href');
			li[i].onkeydown = li[i].onclick = function()
			{
				loadExternalFile(messagefile)
			}
		}
	}
}

/**
 * Get the popupactions
 */
function defineButtonActions()
{
	var input = document.getElementsByTagName('input');
	for(var i=0; i<input.length; i++)
	{
		switch(input[i].className)
		{
			//button: <input class="start_enquete" type="submit" value="Start enquete" />
			case "start_enquete":
				input[i].onclick = input[i].onkeydown = function()
				{
					createMessageCookie(cookieName, 'Start enquete', false);
				}
				break;
				
			//button: <input class="sluit_popup" type="submit" value="Sluit enquete" />
			case "sluit_popup":
				input[i].onclick = input[i].onkeydown = function()
				{
					createMessageCookie(cookieName, 'Geen enquete', false);
					closePopup();
					return false;
				}
				break;
				
			//button: <input class="vraag_later" type="submit" value="Vraag mij dit later nog een keer" />
			case "vraag_later":
				input[i].onclick = input[i].onkeydown = function()
				{
					createMessageCookie(cookieName, 'Vraag later', true);
					closePopup();
					return false;
				}
				break;
		}
	}
}

/**
 * Create popup of given X(HT)ML snippet
 * @param {XML} response
 */
function createPopup(response)
{
	var div = document.createElement('div');
	div.innerHTML = response;
	
	var popup = document.createElement('div');
	popup.id = 'popup';
	popup.appendChild(div);
	document.body.appendChild(popup)
	document.body.insertBefore(popup, document.body.childNodes[1]);
	if(document.all) document.getElementsByTagName('html')[0].style.overflow = 'hidden';
	defineButtonActions();
}

/**
 * 
 */
function closePopup()
{
	if(document.getElementById('popup'))
	{
		var popup = document.getElementById('popup');
		popup.parentNode.removeChild(popup);
		if(document.all) document.getElementsByTagName('html')[0].style.overflow = 'auto';
	}
}


/**
 * Load external file, to use in function createPopup()
 * @param {String} url
 */
function loadExternalFile(url)
{
    var xmlRequest = null;
    
    if (window.XMLHttpRequest) xmlRequest = new XMLHttpRequest();
	else if (window.ActiveXObject) xmlRequest = new ActiveXObject("Microsoft.XMLHTTP");
    
    xmlRequest.onreadystatechange = function()
    {
        if (xmlRequest.readyState == 4) 
        {
            if (xmlRequest.status == 200) 
            {
                createPopup(xmlRequest.responseText);
            }
        }
    };
    xmlRequest.open("GET", url, true);
    xmlRequest.send(null);
}

/**
 * Returns cookie according to name
 * @param {String} name 
*/
function readMessageCookie(name)
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
 * @param {Boolean} show_again
 */
function createMessageCookie(name, value, show_again)
{
    var exdate=new Date();
	if(show_again)
	{
		exdate.setDate(exdate.getDate()+2); // 2 days
	}
	else
	{
		exdate.setDate(exdate.getDate()+(5*365)); // 5 years
	}
	var expires = "; expires= " + exdate.toGMTString();
	
	var path = "; path=/";
	
	if(window.location.host.search('overheid.nl') >= 0) var domain = '; domain=overheid.nl'; // productie
	else if(window.location.host.search('overheid.nl') >= 0) var domain = '; domain=asp4all.nl'; // acceptatie
	else var domain = ""; // werkt niet in subdomain

	document.cookie = name + "=" + value + expires + path + domain;
}

/**
  * Tests if the Object has a class classname
  * @param {ElementNode} obj
  * @param {String} classname
  */ 
function hasClass(obj, classname) 
{
	if ((obj.className===null) || (typeof obj=='undefined')) return false; 
	var classes=obj.className.split(" "); 
	for (i in classes) 
	{
		if(classes[i]==classname) return true; 
	}
	return false; 
} 

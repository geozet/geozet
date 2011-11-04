/**
 * @project Overheid.nl
 * @author	Stichting ICTU, e-Overheid voor Burgers
 * @since	march 2010
 * @name	General javascript
 * @version	1
 */

// ID's
var calendarID = "calendar";

// Classes
var cssCalenderClass = 'datepicker';

// Content
content = new Object();
content.months = Array (
			"Januari",
			"Februari",
			"Maart",
			"April",
			"Mei",
			"Juni",
			"Juli",
			"Augustus",
			"September",
			"Oktober",
			"November",
			"December"
		);
content.days = Array (
			"Zo",
			"Ma",
			"Di",
			"Wo",
			"Do",
			"Vr",
			"Za"
		);
content.nextMonth = "Volgende maand";
content.prevMonth = "Vorige maand"; 

/**
 * set calendar object to input
 */

function setCalendarToInput()
{
	var input = document.getElementsByTagName('input');
	for (var i=0; i<input.length; i++)
	{
		if(input[i].className.search(cssCalenderClass) != -1) addCalendarButton(input[i]);
	}
}

/**
 * Adds button for calendar pop-up to input-element
 * @param {Object} input HTML element
 */
function addCalendarButton(input)
{
    var x;
    var y;
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth();
	var button = document.createElement('a');
	
	// add buttons
	button.className = "calendarbutton";
	input.parentNode.insertBefore(button, input.nextSibling);
	button.target = input;
	button.href = "#";

	// button behaviour
	function calenderButton(e)
	{
        if (document.getElementById(calendarID) === null)
        {
			button.className = 'calendarbutton active';
            initCalendar(e, input);
        }
        else 
        {
            clearCalendar();
        }
		return false;
	}
	
	button.onclick = calenderButton;
    
    /**
     * Initialise calendar
     * @param {Mouse event} e
     */
    function initCalendar(e, input)
    {
        if (!document.getElementById(calendarID)) 
        {
            var calendar = document.createElement('div');
            calendar.id = calendarID;
            calendar.appendChild(drawHeader());
            calendar.appendChild(drawCalendar());
            document.body.appendChild(calendar);
        }
        positionCalendar(button);
        if (BrowserIsIE6()) {
            CreatePopupGuard(calendar);
        }       
    }

    function CreatePopupGuard(calendarDiv) {        
        var popupGuard = document.createElement('iframe');
        popupGuard.id = 'calendarPopupGuard';
        popupGuard.src = "javascript:void(0);";
        popupGuard.style.position = "absolute";
        popupGuard.style.zIndex = "99";
        calendarDiv.style.zIndex = "100";
        popupGuard.style.display = 'block';
        popupGuard.style.width = calendarDiv.offsetWidth + 1 + 'px';
        popupGuard.style.height = calendarDiv.offsetHeight + 1 + 'px';
        popupGuard.style.left = calendarDiv.offsetLeft + 'px';
        popupGuard.style.top = calendarDiv.offsetTop + 'px';
        document.body.appendChild(popupGuard);            
    }
    
    /**
     * Remove calendar
     */
    function clearCalendar()
    {
		button.className = 'calendarbutton';
        if (document.getElementById(calendarID)) 
        {
            var del = document.getElementById(calendarID);
            document.body.removeChild(del);
        }

        if (document.getElementById('calendarPopupGuard')) {
            var popupGuard = document.getElementById('calendarPopupGuard');
            document.body.removeChild(popupGuard);
        }
    }
    
    /**
     * Get the number of the first day of the month
     * @return {Number}
     */
    function getFirstDay()
    {
        var first = new Date();
        first.setFullYear(year);
        first.setMonth(month);
        first.setDate(1);
        return first.getDay();
    }
    
    /**
     * Return the number of days in a month
     */
    function getDaysinMonth()
    {		
		return 32 - new Date(year, month, 32).getDate();

    }
    
    /**
     * Render calender
     */
    function drawCalendar() {
        var table = document.createElement('table');       
        var thead = document.createElement('thead');
        var tbody = document.createElement('tbody');
        
        // table header
        var tr = document.createElement('tr');
        for (j = 0; j < 7; j++) 
        {			
			var th = document.createElement('th');
			th.appendChild(document.createTextNode(content.days[j]));
			tr.appendChild(th);
        }
        thead.appendChild(tr);
        
        // table cells
        var start = false;
        var d = 1;
		var len = getDaysinMonth();
        for (i = 0; i < ((len + getFirstDay()) / 6); i++) 
        {
            tr = document.createElement('tr');
            for (j = 0; j < 7; j++) 
            {
                if ((i * 7 + j) == getFirstDay()) 
                {
                    start = true;
                }
                if (d > getDaysinMonth()) 
                {
                    start = false;
                }
                var td = document.createElement('td');
                if (start) 
                {
                    var active = document.createElement('a');
                    active.appendChild(document.createTextNode(d));
                    active.title = d + " " + content.months[month] + " " + year;
                    active.id = d;
					
					function setCalendarValue()
					{
                        setValue(this);
                        clearCalendar();
					}
                    
                    active.onclick = setCalendarValue;
                    active.onkeydown = setCalendarValue;
					
                    d++;
                    td.appendChild(active);
                }
                tr.appendChild(td);
            }
            tbody.appendChild(tr);
        }
        table.appendChild(thead);
        table.appendChild(tbody);
        return table;
    }
    
    /**
     * Render celendar header
     */
    function drawHeader()
    {
        var header = document.createElement('div');
        
        // Title year
        var title_year = document.createElement('select');
		var year_now = new Date();
		for(var i=year_now.getFullYear() - 15; i<=year_now.getFullYear() +1; i++)
		{
			var option = document.createElement('option');
			option.value = i;
			option.appendChild(document.createTextNode(i));
			if(i == year) option.selected = "selected";
			title_year.appendChild(option);
		}
		title_year.onchange = function()
		{
			year = this.value;
            clearCalendar();
            initCalendar();		
		}
        
        // Title month
        var title_month = document.createElement('select');
		for(var i=0; i<content.months.length; i++)
		{
			var option  = document.createElement('option');
			option.value = i;
			if(content.months[i] == content.months[month]) option.selected = "selected";
			option.appendChild(document.createTextNode(content.months[i]));
			title_month.appendChild(option);
		}
		title_month.onchange = function()
		{
			month = this.value;
            clearCalendar();
            initCalendar();		
		}
        
        // Previous button
        var prev = document.createElement('a');
        prev.title = content.prevMonth;
        prev.id = "prev";
		
		var span1 = document.createElement('span');
		span1.appendChild(document.createTextNode(prev.title));
		prev.appendChild(span1);
		
		function previousMonth()
		{
            if (month === 0) 
            {
                month = content.months.length;
                year--;
            }
            month--;
            clearCalendar();
            initCalendar();
		}
        
        prev.onclick = previousMonth;
        prev.onkeydown = previousMonth;
        
        // Next button
        var next = document.createElement('a');
        next.title = content.nextMonth;
        next.id = "next";
		
		var span2 = document.createElement('span');
		span2.appendChild(document.createTextNode(next.title));
		next.appendChild(span2);
		
		function nextMonth()
		{
            month++;
            if (month == content.months.length) 
            {
                month = 0;
                year++;
            }
            clearCalendar();
            initCalendar();			
		}
        
        next.onclick = nextMonth;
        next.onkeydown = nextMonth;
        
        header.appendChild(prev);
        header.appendChild(title_month);
        header.appendChild(title_year);
        header.appendChild(next);
        
        return header;
    }
    
    /**
     * Position the calendar
     * @param {Mouse event} e
     */
    function positionCalendar(obj)
    {
        if (x === undefined && y === undefined) 
        {
            var xy = findPos(obj);
            x = xy[0];
            y = xy[1];
        }
        var c = document.getElementById(calendarID);
        
        c.style.top = y + "px";
        c.style.left = (x + 8) + "px";
    }
    
    /**
     * @param {String} v
     */
    function setValue(obj)
    {
        var m = parseInt(month) + 1;
		var nr = obj.id;
		
        if (m.toString().length == 1) 
        {
            m = "0" + m;
        }
        if (nr.length == 1) 
        {
            nr = "0" + nr;
        }
		input.value = nr + "-" + m + "-" + year;
		input.className = input.className.replace('inactive', '');
    }
}

/**
 * Set actions on infoblock
 */

function setInfoBlock()
{
	var p = document.getElementsByTagName('p');
	var div = document.getElementsByTagName('div');
	
	function createI(obj)
	{
		// create info node
		var i = document.createElement('a');
		var txt = document.createTextNode('info');
		i.appendChild(txt);
		i.className = ' info';
		i.href="#";
		i.obj = obj;
		
		// get target element
		var target = obj.previousSibling;
		
		if(
			obj.parentNode.nodeName.toLowerCase() == 'dd' ||
			obj.parentNode.nodeName.toLowerCase() == 'li'
		)	target = obj.parentNode;
		else if(obj.previousSibling.nodeName == '#text') target = obj.previousSibling.previousSibling;
		
		// attach to target element
		if (target != null) 
		{
			target.appendChild(i);
			
			i.onfocus = i.onmouseover = function()
			{
				var x = findPos(this)[0] + 13;
				var y = findPos(this)[1];
				
				this.obj.style.top = y + 'px';
				this.obj.style.left = x + 'px';
				this.obj.style.display = 'block';
			}
			i.onblur = i.onmouseout = function()
			{
				this.obj.style.display = 'none';
			}
			i.onclick = function()
			{
				return false;
			}
		}
	}
	
	for(var i=0; i<p.length; i++)
	{
		if(p[i].className == 'info')
		{
			createI(p[i]);
		}
	}
	
	for(var i=0; i<div.length; i++)
	{
		if(div[i].className == 'info')
		{
			createI(div[i]);
		}
	}
}

/**
 * Get the defined printlinks and adds the print-action
 * printlink: <li class="print"><a href="#">Print</a></li>
 */
function getPrintLinks()
{
	var li = document.getElementsByTagName('li');
	for(var i=0; i<li.length; i++)
	{
		if(li[i].className.search('print') >= 0)
		{
			li[i].removeAttribute('href');
			li[i].onkeydown = li[i].onclick = function()
			{
				print();
				return false;
			}
		}
	}
}

/**
 * get x/y coordinates
 * @param {Object} obj
 * @return {Array} [x-coordinate, y-coordinate]
 */
function findPos(obj)
{
	var curleft = curtop = 0;
	if (obj.offsetParent) 
	{
		do
		{
			curleft += obj.offsetLeft;
			curtop += obj.offsetTop;
		}
		while (obj = obj.offsetParent);
		return [curleft, curtop];
	}
}

/**
 * If popup exists body overflow is hidden
 */
function checkPopUp() {
	
	/*var containers = document.getElementsByTagName("div");	
	for(var i = 0; i < containers.length; i++)
	{		
		var potentialPopup = containers[i];
		
		if(potentialPopup.className == 'popup' && !BrowserIsIE6()){
			var html = document.getElementsByTagName('html')[0];
			html.style.overflow = 'hidden';
		}
	}*/
    var popupDiv = document.getElementById('popup');
    if (popupDiv) {
        var html = document.getElementsByTagName('html')[0];
        if (!BrowserIsIE6()) {
            html.style.overflow = 'hidden';
        }
	}
}

function BrowserIsIE6() {
    var browser = navigator.appName;
    var b_version = navigator.appVersion;
    var version = parseFloat(b_version);
    if (( browser == "Microsoft Internet Explorer") && (version < 7)) {
        return true;
    }
    else {
        return false;
    }
}

/**
 * 
 */
function setLoketGemeente()
{
	var input = document.getElementsByName('gemeente');
	for(var i=0; i<input.length; i++)
	{
		input[i].onkeyup = function()
		{
			var checkbox = this.parentNode.getElementsByTagName('input');
			if(this.value != '')
			{
				for(var j=0; j<checkbox.length; j++)
				{
					if (checkbox[j].type == 'checkbox') checkbox[j].checked = 'checked';
				}
			}
		}
	}
}

/**
 * label with class 'vulling' sets default value for input
 * OR
 * input with title sets title-value to default value for input
 */
function setDefaultValueInput()
{
	var label = document.getElementsByTagName('label');
	var input = document.getElementsByTagName('input');
	var del_label = [];
	
	// get default value out of labels
	function setLabelValueToInput(obj)
	{
		var id;
		for (var y = 0; y < obj.attributes.length; y++) 
		{
			if (obj.attributes[y].nodeName == 'for') 
			{
				id = obj.attributes[y].nodeValue;
			}
		}
		prepareInput(id, obj.childNodes[0].nodeValue);
	}
	
	// set initial value for input
	function prepareInput(id, value)
	{
		if (document.getElementById(id)) 
		{
			var input = document.getElementById(id);
			input.txt = value;
			if(input.value == '' || input.value == input.txt)
			{
				input.value = input.txt;
				input.className += ' inactive';
			}
			input.title = '';
			
			input.onblur = setBlur;
			input.onfocus = setFocus;

			var form = findParent(input, 'form');
			form.onsubmit = function()
			{
				if(input.value == input.txt) input.value = '';
			}
		}
	}
	
	// set focus behaviour for input element
	function setFocus()
	{
		this.className = this.className.replace('inactive','');
		if (this.value == this.txt) this.value = '';
	}
	
	// set blur behavriour for input element
	function setBlur()
	{
		if (this.value == '') 
		{
			this.value = this.txt;
			this.className += ' inactive';
		}		
	}
	
	// set behaviour on label-input pairs
	for(var i=0; i<label.length; i++)
	{
		if(label[i].className == 'vulling')
		{
			setLabelValueToInput(label[i]);
			del_label.push(label[i]);
		}
	}
	
	// set behaviour on input with title-attribute
	for(var i=0; i<input.length; i++)
	{
		if (input[i].title && input[i].title != '' && (!input[i].value || input[i].value == ''))
		{
			prepareInput(input[i].id, input[i].title);
			input.onblur = setBlur;
			input.onfocus = setFocus;
		}
	}
	
	// remove labels
	for(var j=0; j<del_label.length; j++)
	{
		del_label[j].parentNode.removeChild(del_label[j]);
	}
}

/**
 * Finds first parent with the specified tag
 * @param {HTMLelement} obj
 * @param {String} parent tagname
 * @return {HTMLelement} parent element
 */
function findParent(obj, tag)
{
	var parent = 0;
	
	function findParentObject()
	{
	    if (obj.nodeName.toLowerCase() == tag.toLowerCase()) 
		{
			parent = obj;
		}
		else
		{
			obj = obj.parentNode;
			findParentObject();
		}
	}
	findParentObject();

	return parent;	
}

/**
 * Starts the first batch of functions when the document is loaded
 */
function init()
{
	document.body.className = document.body.className + ' js';
	checkPopUp();
	setInfoBlock();
	setDefaultValueInput();
	setCalendarToInput();
	getPrintLinks();
	setLoketGemeente();
}

if (window.addEventListener) 
    window.addEventListener("load", init, false);
else if (window.attachEvent)
     window.attachEvent("onload", init);
else
    window.onload = init;


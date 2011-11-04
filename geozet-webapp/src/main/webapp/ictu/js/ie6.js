/**
 * @project Overheid.nl
 * @author	Stichting ICTU, e-Overheid voor Burgers
 * @since	march 2010
 * @name	IE6 specific stylesheet
 * @version	1
 */

// Make elements known
document.createElement('abbr');
document.createElement('q');

// Lock, load and fire
window.attachEvent("onload", initIE6);
function initIE6()
{
	// Clone CSS 2.1 by assigning custom classes
	//cloneCSS2InputTypeSelector(['checkbox','radio','submit'])
	cloneCSS2PseudoSelectors(['form','fieldset', 'p']);
	cloneCSS2InputSubmitHover();
}

/**
 * Assigns the type as a class to all input-elements
 * @param {Object} types Inputtypes
 */
function cloneCSS2InputTypeSelector(types)
{
	for(var i=0; i<types.length; i++)
	{
		var elements = document.getElementsByTagName('input');
		for(var j=0; j<elements.length; j++)
		{
			if(elements[j].type == types[i])
			{
				elements[j].className += ' ' + types[i];
			}
		}
	}
}

/**
 * Assigns 
	class 'first-child' to the element[]:first-child
	class 'last-child' to the element[]:last-child
 * @param {Object} element Array of HTML-elements
 */
function cloneCSS2PseudoSelectors(element)
{
	for(var i=0; i<element.length; i++)
	{
		var elements = document.getElementsByTagName(element[i]);
		for(var j=0; j<elements.length; j++)
		{
			var elems = elements[j].parentNode.getElementsByTagName(element[i]);
			
			elems[0].className += ' first-child';
			if (elems.length > 1) 
			{
				elems[1].className += ' second-child';
				elems[elems.length - 1].className += ' last-child';
			}
			if (elems.length == 1) 
			{
				elems[0].className += ' only-child';
			}
		}
	}
}

/**
 * Assigns class hover on input[type="submit"]:hover
 */
function cloneCSS2InputSubmitHover()
{
	var elements = document.getElementsByTagName('input');
	for(var j=0; j<elements.length; j++)
	{
		if(elements[j].type == 'submit')
		{
	        elements[j].onmouseover = elements[j].onfocus = function()
	        {
	            this.className += ' hover';
	        }
			
	        elements[j].onmouseout = elements[j].onblur = function()
	        {
	            this.className = this.className.replace(' hover', '');
	        }
		}		
	}
} 
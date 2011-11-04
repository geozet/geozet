<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="nl.geozet.common.CoreResources,nl.geozet.common.StringConstants,nl.geozet.common.DataCategorieen,nl.geozet.common.NumberConstants,java.util.Arrays,java.util.Collections,java.util.List"%>

<%-- Deze component is het filterformulier --%>

<%
    // resource bundel laden voor de tekst en headers
    CoreResources RESOURCES = new CoreResources(this
            .getServletContext().getInitParameter(
                    StringConstants.CONFIG_PARAM_RESOURCENAME.code));

    //om het formulier "sticky" te maken

    //coords
    String xcoord = (request
            .getParameter(StringConstants.REQ_PARAM_XCOORD.code) != null) ? request
            .getParameter(StringConstants.REQ_PARAM_XCOORD.code) : "";
    String ycoord = (request
            .getParameter(StringConstants.REQ_PARAM_YCOORD.code) != null) ? request
            .getParameter(StringConstants.REQ_PARAM_YCOORD.code) : "";

    //gevondenadres
    String gevonden = (request
            .getParameter(StringConstants.REQ_PARAM_GEVONDEN.code) != null) ? request
            .getParameter(StringConstants.REQ_PARAM_GEVONDEN.code)
            : "onbekend";

    //gevraagdadres
    String adres = (request
            .getParameter(StringConstants.REQ_PARAM_ADRES.code) != null) ? request
            .getParameter(StringConstants.REQ_PARAM_ADRES.code)
            : "";
                    
    //straal
    String selected = (request
            .getParameter(StringConstants.REQ_PARAM_STRAAL.code) != null) ? request
            .getParameter(StringConstants.REQ_PARAM_STRAAL.code)
            : NumberConstants.OPENLS_ZOOMSCALE_STANDAARD.toString();

    //filter
    String filterUsed = request
            .getParameter(StringConstants.REQ_PARAM_EXPLICITUSEFILTER.code);
    String[] chkBoxesChecked = request
            .getParameterValues(StringConstants.REQ_PARAM_FILTER.code);
    List<String> checkedList = DataCategorieen.keys();
    if (null != filterUsed && filterUsed.equalsIgnoreCase("true")) {
        // als REQ_PARAM_USEFILTER == true
        // filter instellen volgen request params
        checkedList = (chkBoxesChecked != null) ? Arrays
                .asList(chkBoxesChecked) : Collections.EMPTY_LIST;
    }

    // core only..
    String coreonly = (request
            .getParameter(StringConstants.REQ_PARAM_COREONLY.code) != null) ? request
            .getParameter(StringConstants.REQ_PARAM_COREONLY.code)
            : "false";
%>

<form action="#" class="geozetRefine">

	<fieldset>
	
	<legend><%=RESOURCES.getString("KEY_FILTERFORM_LEGEND_ONDERWERP")%></legend>
	<p class="intro"><%=RESOURCES.getString("KEY_FILTERFORM_INTRO")%></p>
	
	<ul class="geozetFilter">
	<%
	    for (String[] cat : DataCategorieen.elements()) {
	%>
		<li class="<%=cat[1]%>"><input name="filter" value="<%=cat[0]%>"
			type="checkbox" <%if (checkedList.contains(cat[0])) {%>
			checked="checked" <%}%> id="geo-<%=cat[0]%>" /><label
			for="geo-<%=cat[0]%>"><%=cat[2]%></label></li>
	<%
	    }
	%>
	</ul>
	</fieldset>
	<fieldset>
	<%-- voorlopig geen verschuif functie
	
	<legend>Verplaats</legend>
		<label>Ga <input type="text" name="verplaatsafstand" value="500" class="meter" /> meter naar </label>
		<ul>
			<li><label><input type="radio" value="noord" name="verplaatsrichting" />Noord</label></li>
			<li><label><input type="radio" value="oost" name="verplaatsrichting" />Oost</label></li>
			<li><label><input type="radio" value="zuid" name="verplaatsrichting" />Zuid</label></li>
			<li><label><input type="radio" value="west" name="verplaatsrichting" />West</label></li>
		</ul>
	 --%>
	 
	<legend><%=RESOURCES.getString("KEY_FILTERFORM_LEGEND_STRAAL")%></legend>
		<label for="straal"><%=RESOURCES.getString("KEY_FILTERFORM_LABEL_STRAAL")%> 
			<select name="straal" id="straal">
				<%--
				<option value="297000" <%if(("297000").equals(selected)){%>selected="selected"<%}%>>297 km (heel Nederland)</option>
				<option value="148000" <%if(("148000").equals(selected)){%>selected="selected"<%}%>>148 km</option>
				<option value="74000" <%if(("74000").equals(selected)){%>selected="selected"<%}%>>74 km</option>
				<option value="37000" <%if(("37000").equals(selected)){%>selected="selected"<%}%>>37 km</option>
				<option value="18000" <%if(("18000").equals(selected)){%>selected="selected"<%}%>>18 km</option>
				<option value="9200" <%if(("9200").equals(selected)){%>selected="selected"<%}%>>9,2 km</option>
				<option value="4600" <%if(("4600").equals(selected)){%>selected="selected"<%}%>>4,6 km</option>
				<option value="2300" <%if(("2300").equals(selected)){%>selected="selected"<%}%>>2,3 km</option>
				<option value="1200" <%if(("1200").equals(selected)){%>selected="selected"<%}%>>1,2 km</option>
				<option value="580" <%if(("580").equals(selected)){%>selected="selected"<%}%>>580 meter</option>
				<option value="290" <%if(("290").equals(selected)){%>selected="selected"<%}%>>290 meter</option>
				<option value="10" <%if(("10").equals(selected)){%>selected="selected"<%}%>>Exacte locatie</option>
				--%>
				<%-- TODO: evt. dynamisch maken op basis van de waarden in nl.geozet.common.NumberConstants --%>
				<option value="300000" <%if (("300000").equals(selected)) {%>selected="selected"<%}%>>300 km (heel Nederland)</option>
				<option value="150000" <%if (("150000").equals(selected)) {%>selected="selected"<%}%>>150 km</option>
				<option value="50000"  <%if (("50000").equals(selected)) {%>selected="selected"<%}%>>50 km</option>
				<option value="25000"  <%if (("25000").equals(selected)) {%>selected="selected"<%}%>>25 km</option>
				<option value="10000"  <%if (("10000").equals(selected)) {%>selected="selected"<%}%>>10 km</option>
				<option value="3000"   <%if (("3000").equals(selected)) {%>selected="selected"<%}%>>3 km</option>
				<option value="1500"   <%if (("1500").equals(selected)) {%>selected="selected"<%}%>>1,5 km</option>
				<option value="500"    <%if (("500").equals(selected)) {%>selected="selected"<%}%>>500 m</option>
				<option value="150"    <%if (("150").equals(selected)) {%>selected="selected"<%}%>>150 m</option>
				<option value="10"     <%if (("10").equals(selected)) {%>selected="selected"<%}%>>Exacte locatie</option>
			</select>
		</label>
	</fieldset>
	
	<p class="button">
		<%-- verborgen velden --%>
		<input type="hidden" name="<%=StringConstants.REQ_PARAM_ADRES%>" id="<%=StringConstants.REQ_PARAM_ADRES%>" value="<%=adres%>" />
		<input type="hidden" name="<%=StringConstants.REQ_PARAM_XCOORD%>" id="<%=StringConstants.REQ_PARAM_XCOORD%>" value="<%=xcoord%>" /> 
		<input type="hidden" name="<%=StringConstants.REQ_PARAM_YCOORD%>" id="<%=StringConstants.REQ_PARAM_YCOORD%>" value="<%=ycoord%>" />
		<input type="hidden" name="<%=StringConstants.REQ_PARAM_GEVONDEN%>" id="<%=StringConstants.REQ_PARAM_GEVONDEN%>" value="<%=gevonden%>" />
		
		<input type="hidden" name="<%=StringConstants.REQ_PARAM_EXPLICITUSEFILTER%>" id="<%=StringConstants.REQ_PARAM_EXPLICITUSEFILTER%>" value="true" />
		<input type="hidden" name="<%=StringConstants.REQ_PARAM_COREONLY%>" id="<%=StringConstants.REQ_PARAM_COREONLY%>" value="<%=coreonly%>" />
		
		<button type="submit"><span><%=RESOURCES.getString("KEY_FILTERFORM_SUBMIT")%></span></button>
	</p>				
</form>	

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="nl.geozet.common.CoreResources,nl.geozet.common.NumberConstants,nl.geozet.common.StringConstants"%>

<%
    CoreResources RESOURCES = new CoreResources(this
            .getServletContext().getInitParameter(
                    StringConstants.CONFIG_PARAM_RESOURCENAME.code));

    String straal = (request
            .getParameter(StringConstants.REQ_PARAM_STRAAL.code) != null) ? request
            .getParameter(StringConstants.REQ_PARAM_STRAAL.code)
            : NumberConstants.OPENLS_ZOOMSCALE_STANDAARD.toString();
            
    String ingevuld = (request
            .getParameter(StringConstants.REQ_PARAM_ADRES.code) != null) ? request
            .getParameter(StringConstants.REQ_PARAM_ADRES.code)
            : RESOURCES.getString("KEY_INTRO_VOORINGEVULD");
            // core param keys verwijderen
			for (String s:StringConstants.urlKeys()){
			 	ingevuld=ingevuld.replaceAll("/"+s+"/"," ");
			}

            

    String coreonly = (request
            .getParameter(StringConstants.REQ_PARAM_COREONLY.code) != null) ? request
            .getParameter(StringConstants.REQ_PARAM_COREONLY.code) : "";
%>
<%-- Deze component is het zoekformulier --%>
<form id="geozetCoreEntree"	action="<%=getServletContext().getInitParameter(StringConstants.CONFIG_PARAM_GEOZETSERVLET.code)%>" method="get" title="Zoekformulier"><%=RESOURCES.getString("KEY_ZOEKEN_TITEL")%>

<p>
	<label for="adres">Postcode of plaatsnaam</label>
	<input type="text" id="adres" name="<%=StringConstants.REQ_PARAM_ADRES%>" value="<%=ingevuld%>" />
</p>

<p class="button">
	<input type="hidden" name="<%=StringConstants.REQ_PARAM_STRAAL%>" value="<%=straal%>" />
	<input type="hidden" name="<%=StringConstants.REQ_PARAM_COREONLY%>" value="<%=coreonly%>" />
	<button type="submit">
		<span><%=RESOURCES.getString("KEY_ZOEKEN_SUBMIT")%></span>
	</button>
</p>

</form>


<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="nl.geozet.common.CoreResources,nl.geozet.common.StringConstants,nl.geozet.common.NumberConstants"%>
    
<%@ include file="corecheck.jsp"%>
    
<%
  CoreResources RESOURCES = new CoreResources(this
            .getServletContext().getInitParameter(
                    StringConstants.CONFIG_PARAM_RESOURCENAME.code));


String xcoord = (request
        .getParameter(StringConstants.REQ_PARAM_XCOORD.code) != null) ? request
        .getParameter(StringConstants.REQ_PARAM_XCOORD.code) : "";
String ycoord = (request
        .getParameter(StringConstants.REQ_PARAM_YCOORD.code) != null) ? request
        .getParameter(StringConstants.REQ_PARAM_YCOORD.code) : "";
      //straal
        String straal = (request
                .getParameter(StringConstants.REQ_PARAM_STRAAL.code) != null) ? request
                .getParameter(StringConstants.REQ_PARAM_STRAAL.code)
                : NumberConstants.OPENLS_ZOOMSCALE_STANDAARD.toString();

%>
<%-- hierboven komt servlet content, daarboven komt het begin van de pagina --%>



 

			<%-- EIND ZOEKRESULTATEN --%>


			</div>

			

			
			<div id="geozetEnhanced" class="hidden">
				<div id="geozetMap"></div>
			</div>
			

		</div>

		<div id="geozetAside">
		
<%--jsp:include page="ictu-zoeken.jsp" /--%>

			<div id="geozetAsideCore">
			
				<a class="back" href="index.jsp<%=coreOnlyEnabled(request)?("?"+StringConstants.REQ_PARAM_COREONLY.code + "=" + true):""%>"><%=RESOURCES.getString("KEY_BEKENDMAKINGEN_TERUG")%></a>

<jsp:include page="filterformulier.jsp" />								

			</div>
			
			<div id="geozetAsideEnhanced" class="hidden">

			
				<div id="geozetMiniMap"></div>

				<div id="geozetFilter"></div>	
			
			</div>

		</div>

</div>

<%--jsp:include page="ictu-footer.jsp" /--%>

<jsp:include page="javascript.jsp" />

</body>
</html>
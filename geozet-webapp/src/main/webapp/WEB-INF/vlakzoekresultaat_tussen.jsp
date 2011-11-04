<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="nl.geozet.common.CoreResources,nl.geozet.common.StringConstants"%>
    
<%@ include file="corecheck.jsp"%>
    
<%
  CoreResources RESOURCES = new CoreResources(this
            .getServletContext().getInitParameter(
                    StringConstants.CONFIG_PARAM_RESOURCENAME.code));


%>
<%-- hierboven komt servlet content, daarboven komt het begin van de pagina --%>

<%-- vlakgericht doet het nog niet
				
				<h2 class="title">Overige bekendmakingen</h2>
				<p><a href="core-resultaat-vlak.html">Bekijk de bekendmakingen die voor een gebied gelden</a></p>

 --%>

			<%-- EIND ZOEKRESULTATEN --%>


			</div>

			

			
			<div id="geozetEnhanced" class="hidden">
				<div id="geozetMap"></div>
			</div>
			

		</div>

		<div id="geozetAside">
		
<%--jsp:include page="ictu-zoeken.jsp" /--%>

			<div id="geozetAsideCore">
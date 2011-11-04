<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    
    
<%-- hierboven komt servlet content, daarboven komt het begin van de pagina --%>
    
    
<jsp:include page="zoekformulier.jsp" />

				<%-- EIND ZOEKFORMULIER --%>

			</div>
			
			<div id="geozetEnhanced" class="hidden">
				<div id="geozetMap"></div>
			</div>

		</div>

		<div id="geozetAside">

<%--jsp:include page="ictu-zoeken.jsp" /--%>

			<div id="geozetAsideCore"></div>
			
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
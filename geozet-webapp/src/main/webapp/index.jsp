<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	import="nl.geozet.common.CoreResources,nl.geozet.common.StringConstants"%>
<%
	    CoreResources RESOURCES = new CoreResources(this
	            .getServletContext().getInitParameter(
	                    StringConstants.CONFIG_PARAM_RESOURCENAME.code));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl" lang="nl">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Zoek in lokale bekendmakingen</title>

<%-- jsp:include page="WEB-INF/ictu-head.jsp" / --%>

	<link rel="stylesheet" href="static/css/style.css" type="text/css" media="all" />

	<!--[if lte IE 7]>
		<link rel="stylesheet" href="static/css/ie.css" type="text/css" media="all" />
	<![endif]-->
	<!--[if gte IE 8]>
		<link rel="stylesheet" href="static/css/ie-8.css" type="text/css" media="all" />
	<![endif]-->

	<link rel="stylesheet" href="static/css/print.css" type="text/css" media="print" />

	<script type="text/javascript">
		document.documentElement.className += ' js';
	</script>

</head>
<body>

<%-- jsp:include page="WEB-INF/ictu-header.jsp" / --%>

<%@ include file="WEB-INF/corecheck.jsp"%>

<div id="geozetContent" class="start <%=coreOnly(request)%>">

		<div id="geozetArticle">

			<div id="geozetCore">

			<%-- BEGIN ZOEKFORMULIER --%>

<%=RESOURCES.getString("KEY_INTRO_TITEL")%>
<%=RESOURCES.getString("KEY_INTRO_TEKST")%>
                	
<jsp:include page="WEB-INF/zoekformulier.jsp" />

<%=RESOURCES.getString("KEY_INTRO_BANNER")%>

				<%-- EIND ZOEKFORMULIER --%>

			</div>
			
			<div id="geozetEnhanced" class="hidden">
				<div id="geozetMap"></div>
			</div>

		</div>

		<div id="geozetAside">

<%-- jsp:include page="WEB-INF/ictu-zoeken.jsp" / --%>

			<div id="geozetAsideCore"></div>
			
			<div id="geozetAsideEnhanced" class="hidden">
			
			</div>

		</div>

</div>

<%-- jsp:include page="WEB-INF/ictu-footer.jsp" / --%>

<jsp:include page="WEB-INF/javascript.jsp" />

</body>
</html>
<%@ page isErrorPage="true" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	import="nl.geozet.common.CoreResources,nl.geozet.common.StringConstants"%>
<%
	    CoreResources RESOURCES = new CoreResources(this
	            .getServletContext().getInitParameter(
	                    StringConstants.CONFIG_PARAM_RESOURCENAME.code));

//bepaal de exception: ik weet niet meer precies waarom dit meestal werkt.
Throwable t = (exception != null) ? exception : (Throwable)request.getAttribute("javax.servlet.error.exception");

//log de exception en de timestamp
org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("ERROR.JSP");
if (t != null) {
	logger.error(t.getMessage(), t);
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl" lang="nl">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title><%=RESOURCES.getString("KEY_FOUT_SYSTEEMFOUT_TITEL")%></title>

<%-- jsp:include page="WEB-INF/ictu-head.jsp" / --%>

	<link rel="stylesheet" href="static/css/style.css" type="text/css" media="all" />

	<!--[if lte IE 7]>
		<link rel="stylesheet" href="static/css/ie.css" type="text/css" media="all" />
	<![endif]-->
	<!--[if gte IE 8]>
		<link rel="stylesheet" href="static/css/ie-8.css" type="text/css" media="all" />
	<![endif]-->

	<link rel="stylesheet" href="static/css/print.css" type="text/css" media="print" />

</head>
<body>

<%-- jsp:include page="WEB-INF/ictu-header.jsp" / --%>

<div id="geozetContent" class="start">

		<div id="geozetArticle">

			<div id="geozetCore">
            	
				<%=RESOURCES.getString("KEY_FOUT_SYSTEEMFOUT_TITEL")%>
				<%=RESOURCES.getString("KEY_FOUT_SYSTEEMFOUT_MELDING")%>
				<%=RESOURCES.getString("KEY_FOUT_SYSTEEMFOUT_WAT_NU")%>

			</div>
	
		</div>

		<div id="geozetAside">


			<div id="geozetAsideCore"></div>
			
			<div id="geozetAsideEnhanced" class="hidden">
			
			</div>

		</div>

</div>

<%-- jsp:include page="WEB-INF/ictu-footer.jsp" / --%>

</body>
</html>



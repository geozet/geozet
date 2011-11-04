<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="false" %>

<%-- hierboven komt de titel --%>

<%--jsp:include page="ictu-head.jsp" / --%>



<%-- BEGIN BENODIGDE HEAD SECTIE VOOR DE APPLICATIE --%>
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
<%-- EINDE BENODIGDE HEAD SECTIE VOOR DE APPLICATIE --%>

</head>
<body>

<%--jsp:include page="ictu-header.jsp" / --%>

<%@ include file="corecheck.jsp"%>

<div id="geozetContent" class="results <%=coreOnly(request)%>">

		<div id="geozetArticle">

			<div id="geozetCore">


			<%-- BEGIN ZOEKRESULTATEN --%>
			
<%-- hieronder komt servlet content, daaronder komt de afsluiting van de pagina --%>
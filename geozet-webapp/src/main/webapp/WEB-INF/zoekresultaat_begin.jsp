<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="nl.geozet.common.StringConstants"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl" lang="nl">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />

<%
    // controle voor null
    String titel = (request
            .getParameter(StringConstants.REQ_PARAM_GEVONDEN.code) != null) ? request
            .getParameter(StringConstants.REQ_PARAM_GEVONDEN.code) : "";
%>
<title>Zoekresultaten bekendmakingen - <%=titel%></title>


<%--jsp:include page="ictu-head.jsp" /--%>



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

<%--jsp:include page="ictu-header.jsp" /--%>

<%@ include file="corecheck.jsp"%>

<div id="geozetContent" class="results <%=coreOnly(request)%>">

		<div id="geozetArticle">

			<div id="geozetCore">


			<%-- BEGIN ZOEKRESULTATEN --%>

<%-- hieronder komt servlet content, daaronder komt de afsluiting van de pagina --%>

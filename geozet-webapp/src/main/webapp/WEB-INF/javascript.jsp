<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="nl.geozet.common.StringConstants"%>

<%@ include file="corecheck.jsp"%>
<%
    if (!coreOnlyEnabled(request)) {
%>
<%-- 
javascript includes als we coreonly doen
--%>
<script type="text/javascript" src="static/js/Geozet.js"></script>
<script type="text/javascript" src="static/js/settings.js"></script>
<script type="text/javascript" src="static/js/init.js"></script>
<%
    } else {
%>
<script type="text/javascript" src="static/js/coreonly.js"></script>
<%
    }
%>

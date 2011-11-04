<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="false"
	import="nl.geozet.common.StringConstants"%>

<%!/**
     * @return coreOnly of een lege string, afhankelijk van de request parameter waarde
     */
    public String coreOnly(HttpServletRequest request) {
        String coreOnlyVal = request
                .getParameter(StringConstants.REQ_PARAM_COREONLY.code);
        if ((null != coreOnlyVal) && (coreOnlyVal.equalsIgnoreCase("true"))) { return "coreOnly"; }
        return "";
    }

    /**
     * @return true als request parameter waarde voor coreonly=true
     */
    public boolean coreOnlyEnabled(HttpServletRequest request) {
        String coreOnlyVal = request
                .getParameter(StringConstants.REQ_PARAM_COREONLY.code);
        if ((null != coreOnlyVal) && (coreOnlyVal.equalsIgnoreCase("true"))) { return true; }
        return false;
    }%>
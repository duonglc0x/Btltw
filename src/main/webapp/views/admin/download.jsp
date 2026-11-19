<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:redirect url="/admin/category/image-preview">
    <c:param name="fname" value="${param.fname}"/>
</c:redirect>

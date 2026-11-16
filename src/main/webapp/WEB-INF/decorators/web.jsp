<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="theme-color" content="#e7f4f1">
<title><sitemesh:write property="title"/> | Châu Dưỡng Shop</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<sitemesh:write property="head"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/liquid-glass.css?v=1">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/video.css">
<script src="${pageContext.request.contextPath}/assets/js/storefront.js?v=1" defer></script>
</head>
<body class="liquid-glass storefront">
<a class="skip-link" href="#main-content">Đến nội dung chính</a>
<%@ include file="/commons/web/header.jsp"%>
<main class="site-main" id="main-content" tabindex="-1">
    <sitemesh:write property="body"/>
</main>
<%@ include file="/commons/web/footer.jsp"%>
</body>
</html>

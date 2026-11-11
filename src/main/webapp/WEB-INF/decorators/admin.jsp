<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="theme-color" content="#e7f4f1">
<title><sitemesh:write property="title"/> | Quản trị - Châu Dưỡng Shop</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<sitemesh:write property="head"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/liquid-glass.css?v=1">
<script src="${pageContext.request.contextPath}/assets/js/storefront.js?v=1" defer></script>
</head>
<body class="liquid-glass admin-shell">
<a class="skip-link" href="#main-content">Đến nội dung chính</a>
<div class="admin-layout">
    <%@ include file="/commons/admin/left.jsp"%>
    <div class="admin-main">
        <%@ include file="/commons/admin/header.jsp"%>
        <main class="admin-content" id="main-content" tabindex="-1">
            <sitemesh:write property="body"/>
        </main>
        <%@ include file="/commons/admin/footer.jsp"%>
    </div>
</div>
</body>
</html>

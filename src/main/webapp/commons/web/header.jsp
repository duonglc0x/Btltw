<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header class="site-header">
    <nav class="store-nav" aria-label="Điều hướng chính">
        <a class="store-brand" href="<c:url value='/home'/>">
            <svg class="brand-icon" viewBox="0 0 32 32" fill="none" aria-hidden="true"><path d="M8 11h16l2 16H6l2-16Z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><path d="M11 12V9a5 5 0 0 1 10 0v3" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>
            <span>Châu Dưỡng<span class="brand-caption">EVERYDAY, ELEVATED</span></span>
        </a>
        <button class="menu-toggle" type="button" aria-controls="store-menu" aria-expanded="false" aria-label="Mở menu">
            <svg viewBox="0 0 24 24" fill="none" aria-hidden="true"><path d="M4 7h16M4 12h16M4 17h16" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>
        </button>
        <div class="nav-panel" id="store-menu">
            <div class="nav-links">
                <a data-nav="video" href="<c:url value='/video'/>">Video</a>
                <a data-nav="home" href="<c:url value='/home'/>">Trang chủ</a>
                <a data-nav="product" href="<c:url value='/product'/>">Sản phẩm</a>
                <c:if test="${not empty sessionScope.account && sessionScope.account.role == 'ADMIN'}"><a href="<c:url value='/admin/home'/>">Quản trị</a></c:if>
            </div>
            <div class="nav-actions">
                <a class="nav-cart" href="<c:url value='/cart'/>">
                    <svg viewBox="0 0 24 24" fill="none" aria-hidden="true"><path d="m3 4 2 1 2 12h11l3-9H6M9 21h.01M17 21h.01" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    <span>Giỏ hàng</span><span id="cartCountBadge"><c:if test="${not empty sessionScope.cartCount && sessionScope.cartCount > 0}">(${sessionScope.cartCount})</c:if></span>
                </a>
                <c:choose>
                    <c:when test="${not empty sessionScope.account}">
                        <a class="nav-account" href="<c:url value='/profile'/>">
                            <c:choose>
                                <c:when test="${not empty sessionScope.account.avatar}">
                                    <c:url value="/image?fname=${sessionScope.account.avatar}" var="avatarUrl"/>
                                    <img class="nav-avatar" src="${avatarUrl}" alt="" data-fallback="avatar">
                                </c:when>
                                <c:otherwise><span class="account-dot" aria-hidden="true">◉</span></c:otherwise>
                            </c:choose>
                            <span><c:out value="${sessionScope.account.fullName}"/></span>
                        </a>
                        <a class="nav-logout" href="<c:url value='/logout'/>">Đăng xuất</a>
                    </c:when>
                    <c:otherwise>
                        <a class="nav-login" href="<c:url value='/login'/>">Đăng nhập</a>
                        <a class="nav-register" href="<c:url value='/register'/>">Đăng ký <span aria-hidden="true">↗</span></a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </nav>
</header>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><title>Trang chủ</title></head>
<body>
<div class="page-wrap home-page">
    <c:if test="${param.error == 'forbidden'}"><div class="alert alert-danger">Bạn không có quyền truy cập khu vực quản trị.</div></c:if>
    <section class="hero" aria-labelledby="hero-title">
        <div class="hero-copy">
            <span class="eyebrow">CHÂU DƯỠNG · EVERYDAY COLLECTION</span>
            <h1 id="hero-title">Những điều nhỏ.<br><em>Cảm hứng lớn.</em></h1>
            <p>Khám phá những món đồ bạn yêu thích, làm mới phong cách và thêm một chút cảm hứng cho mỗi ngày.</p>
            <div class="hero-actions">
                <a class="btn-primary-glass" href="<c:url value='/product'/>">Khám phá sản phẩm <span aria-hidden="true">↗</span></a>
                <a class="btn-glass" href="#latest-products">Có gì mới? <span aria-hidden="true">↓</span></a>
            </div>
            <div class="hero-footnote"><span aria-hidden="true">✧</span> Một không gian mua sắm, nhiều điều để khám phá.</div>
        </div>
        <div class="hero-visual" aria-hidden="true">
            <div class="visual-orbit"></div>
            <div class="floating-note note-top"><span class="note-icon">✧</span><div><strong>Chọn điều bạn thích</strong><small>Mang dấu ấn của riêng bạn</small></div></div>
            <div class="glass-bag">
                <svg viewBox="0 0 64 64" fill="none"><path d="m32 9 6 16 17 7-17 7-6 16-6-16-17-7 17-7 6-16Z" stroke="currentColor" stroke-width="1.5"/><path d="M32 20v24M20 32h24" stroke="currentColor" stroke-width="1"/></svg>
                <span class="bag-caption">Châu Dưỡng</span>
            </div>
            <div class="glass-bag small-bag"><svg viewBox="0 0 48 48" fill="none"><path d="M24 37 8 22a10 10 0 0 1 16-12 10 10 0 0 1 16 12L24 37Z" stroke="currentColor" stroke-width="1.6"/></svg></div>
            <div class="floating-note note-bottom"><span class="note-icon">↗</span><div><strong>Một chút mới mẻ</strong><small>Cho ngày thêm cảm hứng</small></div></div>
        </div>
    </section>
    <div class="store-promises">
        <div class="promise"><svg viewBox="0 0 24 24" fill="none" aria-hidden="true"><rect x="3" y="3" width="7" height="7" rx="2" stroke="currentColor" stroke-width="1.5"/><rect x="14" y="3" width="7" height="7" rx="2" stroke="currentColor" stroke-width="1.5"/><rect x="3" y="14" width="7" height="7" rx="2" stroke="currentColor" stroke-width="1.5"/><rect x="14" y="14" width="7" height="7" rx="2" stroke="currentColor" stroke-width="1.5"/></svg><div><strong>Dễ dàng khám phá</strong><small>Tìm sản phẩm theo danh mục</small></div></div>
        <div class="promise"><svg viewBox="0 0 24 24" fill="none" aria-hidden="true"><path d="m3 4 2 1 2 12h11l3-9H6M9 21h.01M17 21h.01" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg><div><strong>Giỏ hàng tiện lợi</strong><small>Lưu lại những món đồ bạn thích</small></div></div>
        <div class="promise"><svg viewBox="0 0 24 24" fill="none" aria-hidden="true"><circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="1.5"/><path d="M4 22v-2a8 8 0 0 1 16 0v2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg><div><strong>Dành riêng cho bạn</strong><small>Quản lý tài khoản cá nhân</small></div></div>
    </div>
    <section id="latest-products" aria-labelledby="latest-title" style="scroll-margin-top:130px">
        <div class="section-heading">
            <div><div class="section-kicker">FIND YOUR NEXT FAVORITE</div><h2 class="section-title" id="latest-title">Khám phá mới nhất</h2></div>
            <a class="section-link" href="<c:url value='/product'/>">Tất cả sản phẩm <span aria-hidden="true">↗</span></a>
        </div>
        <div class="category-filter" aria-label="Danh mục sản phẩm">
            <a class="active" href="<c:url value='/product'/>">Tất cả sản phẩm</a>
            <c:forEach items="${categories}" var="cate"><c:url value="/product" var="categoryUrl"><c:param name="cateId" value="${cate.id}"/></c:url><a href="${categoryUrl}"><c:out value="${cate.name}"/></a></c:forEach>
        </div>
        <c:choose>
            <c:when test="${empty latestProducts}">
                <div class="empty-box"><svg viewBox="0 0 48 48" fill="none" aria-hidden="true"><path d="M12 17h24l3 25H9l3-25Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/><path d="M17 19v-8a7 7 0 0 1 14 0v8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg><h3>Những điều mới đang đến</h3><p>Cửa hàng đang cập nhật sản phẩm. Hẹn bạn quay lại khám phá nhé!</p></div>
            </c:when>
            <c:otherwise>
                <div class="product-grid">
                    <c:forEach items="${latestProducts}" var="p">
                        <c:url value="/product/detail" var="detailUrl"><c:param name="id" value="${p.id}"/></c:url>
                        <c:url value="/image" var="imgUrl"><c:param name="fname" value="${p.image}"/></c:url>
                        <a class="product-card" href="${detailUrl}">
                            <img src="${imgUrl}" alt="<c:out value='${p.name}'/>" loading="lazy" data-fallback="product">
                            <div class="product-info"><div class="p-cate"><c:out value="${p.category.name}"/></div><div class="p-name"><c:out value="${p.name}"/></div><div class="p-price"><span><fmt:formatNumber value="${p.price}" type="number" groupingUsed="true"/> đ</span><span class="product-arrow" aria-hidden="true">↗</span></div></div>
                        </a>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
        <div class="view-all"><a href="<c:url value='/product'/>" class="btn-view-all">Xem tất cả sản phẩm <span aria-hidden="true">↗</span></a></div>
    </section>
    <section class="home-videos" aria-labelledby="home-video-title">
        <div class="section-heading"><div><div class="section-kicker">KHÁM PHÁ SẢN PHẨM</div><h2 class="section-title" id="home-video-title">Video giới thiệu và hướng dẫn</h2></div><a class="section-link" href="<c:url value='/video'/>">Tất cả video ↗</a></div>
        <c:choose><c:when test="${empty latestVideos}"><div class="empty-box">Chưa có video nào.</div></c:when><c:otherwise>
            <c:set var="videos" value="${latestVideos}" scope="request"/><jsp:include page="video-cards.jsp"/>
        </c:otherwise></c:choose>
    </section>
</div>
</body>
</html>

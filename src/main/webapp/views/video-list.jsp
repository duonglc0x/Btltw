<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><title>Video sản phẩm</title></head><body>
<main class="page-wrap video-page">
    <div class="section-heading"><div><div class="section-kicker">KHÁM PHÁ CÙNG CHÚNG TÔI</div><h1 class="section-title">Video sản phẩm</h1><p>Giới thiệu và hướng dẫn sử dụng sản phẩm.</p></div></div>
    <c:choose><c:when test="${empty videos}"><div class="empty-box">Chưa có video nào.</div></c:when><c:otherwise>
        <jsp:include page="video-cards.jsp"/>
        <c:if test="${totalPages > 1}"><nav class="video-pagination" aria-label="Phân trang video">
            <c:if test="${currentPage > 1}"><a href="<c:url value='/video'><c:param name='page' value='${currentPage - 1}'/></c:url>">Trước</a></c:if>
            <c:forEach begin="1" end="${totalPages}" var="i"><a class="${i == currentPage ? 'active' : ''}" ${i == currentPage ? 'aria-current="page"' : ''} href="<c:url value='/video'><c:param name='page' value='${i}'/></c:url>">${i}</a></c:forEach>
            <c:if test="${currentPage < totalPages}"><a href="<c:url value='/video'><c:param name='page' value='${currentPage + 1}'/></c:url>">Sau</a></c:if>
        </nav></c:if>
    </c:otherwise></c:choose>
</main></body></html>

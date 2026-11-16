<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="video-grid">
    <c:forEach items="${videos}" var="item">
        <c:url value="/video/detail" var="detailUrl"><c:param name="id" value="${item.id}"/></c:url>
        <article class="video-card">
            <a href="${detailUrl}" class="video-thumbnail" aria-label="Xem video: <c:out value='${item.title}'/>">
                <c:if test="${not empty item.thumbnailUrl}">
                    <c:choose><c:when test="${item.thumbnailUrl.startsWith('video-thumbnail/')}"><c:url value="/image" var="thumbnailSrc"><c:param name="fname" value="${item.thumbnailUrl}"/></c:url></c:when><c:otherwise><c:set var="thumbnailSrc" value="${item.thumbnailUrl}"/></c:otherwise></c:choose>
                    <img src="<c:out value='${thumbnailSrc}'/>" alt="" loading="lazy">
                </c:if>
                <span aria-hidden="true">▶</span>
            </a>
            <div class="video-card-body"><a class="video-title" href="${detailUrl}"><c:out value="${item.title}"/></a></div>
        </article>
    </c:forEach>
</div>

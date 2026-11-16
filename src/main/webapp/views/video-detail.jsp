<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><title><c:out value="${video.title}"/></title></head><body>
<main class="page-wrap video-page video-detail">
    <a class="section-link" href="<c:url value='/video'/>">← Tất cả video</a>
    <h1 class="section-title"><c:out value="${video.title}"/></h1>
    <c:if test="${not empty video.thumbnailUrl}"><c:choose><c:when test="${video.thumbnailUrl.startsWith('video-thumbnail/')}"><c:url value="/image" var="thumbnailSrc"><c:param name="fname" value="${video.thumbnailUrl}"/></c:url></c:when><c:otherwise><c:set var="thumbnailSrc" value="${video.thumbnailUrl}"/></c:otherwise></c:choose></c:if>
    <video controls playsinline preload="metadata" <c:if test="${not empty video.thumbnailUrl}">poster="<c:out value='${thumbnailSrc}'/>"</c:if>>
        <c:choose><c:when test="${video.videoUrl.startsWith('video/')}"><c:url value="/video/stream" var="videoSrc"><c:param name="id" value="${video.id}"/></c:url></c:when><c:otherwise><c:set var="videoSrc" value="${video.videoUrl}"/></c:otherwise></c:choose>
        <source src="<c:out value='${videoSrc}'/>" type="video/mp4">
        Trình duyệt của bạn không hỗ trợ phát video.
    </video>
    <p class="video-description"><c:out value="${video.description}"/></p>
</main></body></html>

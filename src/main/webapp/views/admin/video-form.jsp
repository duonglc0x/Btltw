<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><title>${editing ? 'Sửa video' : 'Thêm video'}</title></head><body>
<main class="video-admin"><h1>${editing ? 'Sửa video' : 'Thêm video'}</h1><p>Chọn tệp MP4 từ máy tính, tối đa 100 MB.</p>
<c:if test="${not empty error}"><p class="video-error" role="alert"><c:out value="${error}"/></p></c:if>
<c:url value="${editing ? '/admin/video/edit' : '/admin/video/add'}" var="formAction"/>
<form class="video-form" method="post" enctype="multipart/form-data" action="${formAction}">
    <input type="hidden" name="csrf" value="<c:out value='${sessionScope.videoCsrf}'/>">
    <c:if test="${editing}"><input type="hidden" name="id" value="${video.id}"></c:if>
    <label for="title">Tiêu đề *</label><input id="title" name="title" maxlength="255" required value="<c:out value='${video.title}'/>">
    <label for="videoFile">Tệp video MP4 ${editing ? '' : '*'}</label><input id="videoFile" name="videoFile" type="file" accept=".mp4,video/mp4" ${editing ? '' : 'required'}>
    <c:if test="${editing}"><small>Để trống nếu muốn giữ video hiện tại.</small></c:if>
    <label for="thumbnailFile">Ảnh đại diện</label><input id="thumbnailFile" name="thumbnailFile" type="file" accept="image/jpeg,image/png,.jpg,.jpeg,.png">
    <small>Chọn ảnh JPEG hoặc PNG, tối đa 5 MB. ${editing ? 'Để trống để giữ ảnh hiện tại.' : 'Có thể để trống.'}</small>
    <c:if test="${editing && not empty video.thumbnailUrl}">
        <c:choose><c:when test="${video.thumbnailUrl.startsWith('video-thumbnail/')}"><c:url value="/image" var="thumbnailSrc"><c:param name="fname" value="${video.thumbnailUrl}"/></c:url></c:when><c:otherwise><c:set var="thumbnailSrc" value="${video.thumbnailUrl}"/></c:otherwise></c:choose>
        <img class="video-form-preview" src="<c:out value='${thumbnailSrc}'/>" alt="Ảnh đại diện hiện tại">
    </c:if>
    <label for="description">Mô tả</label><textarea id="description" name="description" maxlength="2000" rows="5"><c:out value="${video.description}"/></textarea>
    <label class="video-check"><input type="checkbox" name="active" ${video.active ? 'checked' : ''}> Hiển thị cho khách hàng</label>
    <div><button class="video-button" type="submit">Lưu video</button> <a href="<c:url value='/admin/video/list'/>">Hủy</a></div>
</form></main></body></html>

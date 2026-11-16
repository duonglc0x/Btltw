<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><title>Quản lý video</title></head><body>
<main class="video-admin">
    <div class="video-admin-heading"><div><h1>Quản lý video</h1><p>Video giới thiệu và hướng dẫn sử dụng sản phẩm</p></div><a class="video-button" href="<c:url value='/admin/video/add'/>">+ Thêm video</a></div>
    <c:choose><c:when test="${empty videos}"><p>Chưa có video nào. Hãy thêm video đầu tiên.</p></c:when><c:otherwise>
    <div class="video-table-wrap"><table class="video-table"><thead><tr><th>ID</th><th>Tiêu đề</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody>
    <c:forEach items="${videos}" var="item"><tr><td>${item.id}</td><td><c:out value="${item.title}"/></td><td>${item.active ? 'Hiển thị' : 'Đã ẩn'}</td><td>
        <a href="<c:url value='/admin/video/edit'><c:param name='id' value='${item.id}'/></c:url>">Sửa</a>
        <form method="post" action="<c:url value='/admin/video/delete'/>" class="video-inline" onsubmit="return confirm('Bạn có chắc muốn xóa video này?');"><input type="hidden" name="id" value="${item.id}"><input type="hidden" name="csrf" value="<c:out value='${sessionScope.videoCsrf}'/>"><button type="submit">Xóa</button></form>
    </td></tr></c:forEach></tbody></table></div></c:otherwise></c:choose>
</main></body></html>

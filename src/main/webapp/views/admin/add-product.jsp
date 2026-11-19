<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>Thêm sản phẩm mới</title>

    <style>

        :root {
            --ink: #1c1c1c;
            --muted: #6b6b6b;
            --border: #dcdcdc;
            --surface: #ffffff;
            --accent: #2f5d50;
            --accent-dark: #24463c;
        }

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI",
                         Roboto, Helvetica, Arial, sans-serif;
            background: #f6f6f4;
            color: var(--ink);
        }

        .page-wrap {
            max-width: 860px;
            margin: 0 auto;
            padding: 46px 24px 70px;
        }

        .topbar {
            border-bottom: 1px solid var(--border);
            padding-bottom: 18px;
            margin-bottom: 26px;
        }

        .topbar h1 {
            font-size: 26px;
            font-weight: 600;
            margin: 0;
        }

        .topbar p {
            color: var(--muted);
            font-size: 15px;
            margin: 4px 0 0;
        }

        .form-card {
            background: var(--surface);
            border: 1px solid var(--border);
            padding: 32px 36px;
        }

        .form-group {
            margin-bottom: 18px;
        }

        .form-group label {
            display: block;
            font-weight: 500;
            color: var(--ink);
            font-size: 15px;
            margin-bottom: 7px;
        }

        .form-control {
            display: block;
            width: 100%;
            box-sizing: border-box;

            height: 36px;

            border: 1px solid var(--border);
            border-radius: 4px;

            padding: 7px 14px;
            font-size: 16px;

            background: #fff;
            color: var(--ink);

            box-shadow: none;
        }

        .form-control:focus {
            outline: none;
            border-color: var(--accent);
            box-shadow: none;
        }

        select.form-control {
            cursor: pointer;
        }

        textarea.form-control {
            height: auto;
            min-height: 100px;
            resize: vertical;
            padding: 10px 14px;
            line-height: 1.5;
        }

        .form-group input[type="file"] {
            display: block;
            width: 100%;
            height: 36px;
            box-sizing: border-box;

            border: 1px solid var(--border);
            border-radius: 4px;

            padding: 5px 14px;
            font-size: 15px;

            background: #fff;
            color: var(--ink);
        }

        .form-group input[type="file"]:focus {
            outline: none;
            border-color: var(--accent);
        }

        hr.divider {
            border: none;
            border-top: 1px solid var(--border);
            margin: 22px 0;
        }

        .btn {
            display: inline-block;

            border-radius: 4px;
            font-weight: 500;

            padding: 11px 24px;

            border: 1px solid transparent;
            font-size: 15px;

            text-decoration: none;
            cursor: pointer;
            line-height: 1.2;
        }

        .btn-success {
            background: var(--accent);
            color: #fff;
        }

        .btn-success:hover {
            background: var(--accent-dark);
            color: #fff;
        }

        .btn-primary {
            background: #fff;
            color: var(--ink);
            border-color: var(--border);
        }

        .btn-primary:hover {
            background: #f2f2f2;
            color: var(--ink);
        }

        .btn-default {
            background: #fff;
            color: var(--muted);
            border-color: var(--border);
        }

        .btn-default:hover {
            background: #f2f2f2;
            color: var(--ink);
            text-decoration: none;
        }

        .btn-group-actions {
            margin-top: 22px;
        }

    </style>

</head>


<body>

    <div class="page-wrap">

        <div class="topbar">

            <h1>Thêm sản phẩm mới</h1>

            <p>
                Tạo một sản phẩm mới cho hệ thống
            </p>

        </div>

        <div class="form-card">
            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
            </c:if>

            <form
                role="form"
                action="<c:url value='/admin/product/add'/>"
                method="post"
                enctype="multipart/form-data">

                <div class="form-group">

                    <label>
                        Tên sản phẩm
                    </label>

                    <input
                        type="text"
                        class="form-control"
                        name="name"
                        value="<c:out value='${param.name}'/>"
                        maxlength="255"
                        required />

                </div>

                <div class="form-group">

                    <label>
                        Danh mục
                    </label>

                    <select
                        class="form-control"
                        name="cateId"
                        required>
                        <c:if test="${not empty error}">
                            <option value="">Chọn danh mục</option>
                        </c:if>

                        <c:forEach
                            items="${cateList}"
                            var="c">

                            <option value="${c.id}" ${c.id == submittedCategoryId ? 'selected' : ''}>
                                <c:out value="${c.name}"/>
                            </option>

                        </c:forEach>

                    </select>

                </div>

                <div class="form-group">

                    <label>
                        Giá (VNĐ)
                    </label>

                    <input
                        type="number"
                        step="0.01"
                        min="0"
                        class="form-control"
                        name="price"
                        value="<c:out value='${param.price}'/>"
                        required />

                </div>

                <div class="form-group">

                    <label>
                        Số lượng
                    </label>

                    <input
                        type="number"
                        min="0"
                        class="form-control"
                        name="quantity"
                        max="2147483647"
                        value="<c:out value='${not empty error ? param.quantity : 0}'/>"
                        required />

                </div>

                <div class="form-group">

                    <label>
                        Mô tả
                    </label>

                    <textarea
                        class="form-control"
                        name="description"
                        maxlength="2000"
                        rows="4"><c:out value="${param.description}"/></textarea>

                </div>

                <div class="form-group">

                    <label>
                        Hình ảnh
                    </label>

                    <input
                        type="file"
                        name="image"
                        accept="image/jpeg,image/png,image/gif"
                        class="form-control" />

                </div>
                
                <div class="btn-group-actions">

                    <button
                        type="submit"
                        class="btn btn-success">
                        Thêm
                    </button>

                    <button
                        type="reset"
                        class="btn btn-primary">
                        Hủy
                    </button>

                    <a
                        href="<c:url value='/admin/product/list'/>"
                        class="btn btn-default">
                        Quay lại danh sách
                    </a>

                </div>

            </form>

        </div>

    </div>

</body>

</html>

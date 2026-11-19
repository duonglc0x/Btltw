package web.com.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import web.com.models.Category;
import web.com.service.CategoryService;
import web.com.service.ImageUpload;
import web.com.service.impl.CategoryServiceImpl;
import web.com.utils.FormValidation;

@WebServlet(urlPatterns = { "/admin/category/add", "/admin/category/edit" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = ImageUpload.MAX_SIZE,
        maxRequestSize = ImageUpload.MAX_REQUEST_SIZE)
public class CategoryFormController extends HttpServlet {
    private final CategoryService categoryService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean editing = "/admin/category/edit".equals(req.getServletPath());
        Category category = editing ? findCategory(req, resp) : null;
        if (!editing || category != null) {
            showForm(req, resp, category);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Part icon;
        try {
            icon = ImageUpload.readPart(req, "icon");
        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, ImageUpload.SIZE_ERROR);
            return;
        } catch (ServletException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dữ liệu tải lên không hợp lệ.");
            return;
        }

        boolean editing = "/admin/category/edit".equals(req.getServletPath());
        Category previous = editing ? findCategory(req, resp) : null;
        if (editing && previous == null) {
            return;
        }

        String name = FormValidation.text(req.getParameter("name"));
        if (name.isEmpty() || name.length() > 255) {
            req.setAttribute("error", "Tên danh mục là bắt buộc và không được quá 255 ký tự.");
            showForm(req, resp, previous);
            return;
        }

        String extension;
        try {
            extension = ImageUpload.validatedExtension(icon);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            showForm(req, resp, previous);
            return;
        }

        String iconPath = ImageUpload.save(icon, extension, "category");
        Category category = new Category(name, iconPath);
        try {
            if (editing) {
                category.setId(previous.getId());
                categoryService.edit(category);
            } else {
                categoryService.insert(category);
            }
        } catch (RuntimeException e) {
            ImageUpload.discard(iconPath, e);
            throw e;
        }
        resp.sendRedirect(req.getContextPath() + "/admin/category/list");
    }

    private Category findCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = FormValidation.positiveInteger(req.getParameter("id"), 0);
        if (id == 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        Category category = categoryService.get(id);
        if (category == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        return category;
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Category category)
            throws ServletException, IOException {
        req.setAttribute("category", category);
        String view = category == null ? "/views/admin/add-category.jsp" : "/views/admin/edit-category.jsp";
        req.getRequestDispatcher(view).forward(req, resp);
    }
}

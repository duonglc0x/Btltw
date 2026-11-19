package web.com.controller;

import java.io.IOException;
import java.math.BigDecimal;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import web.com.models.Category;
import web.com.models.Product;
import web.com.service.CategoryService;
import web.com.service.ImageUpload;
import web.com.service.ProductService;
import web.com.service.impl.CategoryServiceImpl;
import web.com.service.impl.ProductServiceImpl;
import web.com.utils.FormValidation;

@WebServlet(urlPatterns = { "/admin/product/add", "/admin/product/edit" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = ImageUpload.MAX_SIZE,
        maxRequestSize = ImageUpload.MAX_REQUEST_SIZE)
public class ProductFormController extends HttpServlet {
    private static final BigDecimal MAX_PRICE = new BigDecimal("999999999999999999999999999999999999.99");

    private final ProductService productService = new ProductServiceImpl();
    private final CategoryService categoryService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean editing = "/admin/product/edit".equals(req.getServletPath());
        Product product = editing ? findProduct(req, resp) : null;
        if (!editing || product != null) {
            showForm(req, resp, product);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Part image;
        try {
            image = ImageUpload.readPart(req, "image");
        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, ImageUpload.SIZE_ERROR);
            return;
        } catch (ServletException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dữ liệu tải lên không hợp lệ.");
            return;
        }

        boolean editing = "/admin/product/edit".equals(req.getServletPath());
        Product previous = editing ? findProduct(req, resp) : null;
        if (editing && previous == null) {
            return;
        }

        String name = FormValidation.text(req.getParameter("name"));
        String description = FormValidation.text(req.getParameter("description"));
        BigDecimal price = FormValidation.price(req.getParameter("price"), MAX_PRICE);
        Integer quantity = FormValidation.nonNegativeInteger(req.getParameter("quantity"));
        int categoryId = FormValidation.positiveInteger(req.getParameter("cateId"), 0);
        Category category = categoryId == 0 ? null : categoryService.get(categoryId);

        String error = validate(name, description, price, quantity, category);
        String extension = null;
        if (error == null) {
            try {
                extension = ImageUpload.validatedExtension(image);
            } catch (IllegalArgumentException e) {
                error = e.getMessage();
            }
        }
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("submittedCategoryId", categoryId);
            showForm(req, resp, previous);
            return;
        }

        String imagePath = ImageUpload.save(image, extension, "product");
        Product product = new Product(name, price, quantity, description, imagePath, category);
        try {
            if (editing) {
                product.setId(previous.getId());
                productService.edit(product);
            } else {
                productService.insert(product);
            }
        } catch (RuntimeException e) {
            ImageUpload.discard(imagePath, e);
            throw e;
        }
        resp.sendRedirect(req.getContextPath() + "/admin/product/list");
    }

    private String validate(String name, String description, BigDecimal price, Integer quantity, Category category) {
        if (name.isEmpty() || name.length() > 255) {
            return "Tên sản phẩm là bắt buộc và không được quá 255 ký tự.";
        }
        if (category == null) {
            return "Vui lòng chọn danh mục sản phẩm hợp lệ.";
        }
        if (price == null) {
            return "Giá phải là số không âm, có tối đa hai chữ số thập phân và nằm trong phạm vi cho phép.";
        }
        if (quantity == null) {
            return "Số lượng phải là số nguyên từ 0 đến 2147483647.";
        }
        return description.length() > 2000 ? "Mô tả không được quá 2000 ký tự." : null;
    }

    private Product findProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = FormValidation.positiveInteger(req.getParameter("id"), 0);
        if (id == 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        Product product = productService.get(id);
        if (product == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        return product;
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Product product)
            throws ServletException, IOException {
        req.setAttribute("product", product);
        req.setAttribute("cateList", categoryService.getAll());
        String view = product == null ? "/views/admin/add-product.jsp" : "/views/admin/edit-product.jsp";
        req.getRequestDispatcher(view).forward(req, resp);
    }
}

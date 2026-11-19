package web.com.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import web.com.service.ProductService;
import web.com.service.impl.ProductServiceImpl;
import web.com.utils.FormValidation;

@WebServlet(urlPatterns = { "/admin/product/delete" })
public class ProductDeleteController extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int id = FormValidation.positiveInteger(req.getParameter("id"), 0);
        if (id == 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        productService.delete(id);

        resp.sendRedirect(req.getContextPath() + "/admin/product/list");
    }
}

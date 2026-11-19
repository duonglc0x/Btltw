package web.com.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import web.com.models.Account;
import web.com.service.CartService;
import web.com.service.impl.CartServiceImpl;
import web.com.utils.FormValidation;

@WebServlet(urlPatterns = { "/cart/add" })
public class CartAddController extends HttpServlet {

    private final CartService cartService = new CartServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        boolean isAjax = "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));

        HttpSession session = req.getSession(false);
        Account account = (Account) session.getAttribute("account");

        int productId = FormValidation.positiveInteger(req.getParameter("productId"), 0);
        String quantityParam = req.getParameter("quantity");
        int quantity = quantityParam == null ? 1 : FormValidation.positiveInteger(quantityParam, 0);
        if (productId == 0 || quantity == 0) {
            String error = productId == 0 ? "Sản phẩm không hợp lệ." : "Số lượng phải là số nguyên lớn hơn 0.";
            writeError(resp, isAjax, HttpServletResponse.SC_BAD_REQUEST, error);
            return;
        }

        if (!cartService.addToCart(account, productId, quantity)) {
            writeError(resp, isAjax, HttpServletResponse.SC_CONFLICT, "Sản phẩm không còn hàng hoặc không tồn tại.");
            return;
        }

        int cartCount = cartService.getTotalItemCount(account);
        session.setAttribute("cartCount", cartCount);

        if (isAjax) {
            writeJson(resp, HttpServletResponse.SC_OK, true,
                    "Đã thêm sản phẩm vào giỏ hàng!", cartCount);
        } else {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    private void writeError(HttpServletResponse resp, boolean ajax, int status, String error) throws IOException {
        if (ajax) {
            writeJson(resp, status, false, error, null);
        } else {
            resp.sendError(status, error);
        }
    }

    private void writeJson(HttpServletResponse resp, int status, boolean success,
            String message, Integer cartCount) throws IOException {

        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");

        String json = "{\"success\":" + success
                + ",\"message\":\"" + escapeJson(message) + "\""
                + (cartCount != null ? ",\"cartCount\":" + cartCount : "")
                + "}";

        resp.getWriter().write(json);
    }

    private String escapeJson(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

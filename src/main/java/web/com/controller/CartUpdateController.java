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

@WebServlet(urlPatterns = { "/cart/update" })
public class CartUpdateController extends HttpServlet {

    private final CartService cartService = new CartServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        Account account = (Account) session.getAttribute("account");

        int itemId = FormValidation.positiveInteger(req.getParameter("itemId"), 0);
        int quantity = FormValidation.positiveInteger(req.getParameter("quantity"), 0);
        if (itemId == 0 || quantity == 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã mặt hàng hoặc số lượng không hợp lệ.");
            return;
        }
        if (!cartService.updateQuantity(account, itemId, quantity)) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Mặt hàng không tồn tại hoặc đã hết hàng.");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/cart");
    }
}

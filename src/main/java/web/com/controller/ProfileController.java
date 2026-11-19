package web.com.controller;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import web.com.models.Account;
import web.com.service.AccountService;
import web.com.service.ImageUpload;
import web.com.service.impl.AccountServiceImpl;
import web.com.utils.FormValidation;

@WebServlet(urlPatterns = { "/profile" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = ImageUpload.MAX_SIZE,
        maxRequestSize = ImageUpload.MAX_REQUEST_SIZE)
public class ProfileController extends HttpServlet {

    private final AccountService accountService = new AccountServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Account sessionAccount = (session == null) ? null : (Account) session.getAttribute("account");

        if (sessionAccount == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Account account = accountService.getById(sessionAccount.getId());
        req.setAttribute("account", account);

        RequestDispatcher dispatcher = req.getRequestDispatcher("/views/account/profile.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        Account sessionAccount = (session == null) ? null : (Account) session.getAttribute("account");

        if (sessionAccount == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Part avatarPart;
        try {
            avatarPart = ImageUpload.readPart(req, "avatar");
        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, ImageUpload.SIZE_ERROR);
            return;
        } catch (ServletException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dữ liệu tải lên không hợp lệ.");
            return;
        }
        String fullName = req.getParameter("fullName");
        String phone = req.getParameter("phone");
        String error = FormValidation.profileError(fullName, phone);
        String extension = null;
        if (error == null) {
            try {
                extension = ImageUpload.validatedExtension(avatarPart);
            } catch (IllegalArgumentException e) {
                error = e.getMessage();
            }
        }
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("account", sessionAccount);
            req.getRequestDispatcher("/views/account/profile.jsp").forward(req, resp);
            return;
        }

        String avatarPath = ImageUpload.save(avatarPart, extension, "avatar");
        try {
            error = accountService.updateProfile(sessionAccount.getId(), fullName, phone, avatarPath);
        } catch (RuntimeException e) {
            ImageUpload.discard(avatarPath, e);
            throw e;
        }

        if (error != null) {
            ImageUpload.delete(avatarPath);
            req.setAttribute("error", error);
            req.setAttribute("account", sessionAccount);
            req.getRequestDispatcher("/views/account/profile.jsp").forward(req, resp);
            return;
        }

        Account updated = accountService.getById(sessionAccount.getId());
        session.setAttribute("account", updated);

        req.setAttribute("message", "Cập nhật hồ sơ thành công.");
        req.setAttribute("account", updated);

        req.getRequestDispatcher("/views/account/profile.jsp").forward(req, resp);
    }
}

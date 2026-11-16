package web.com.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import web.com.models.Category;
import web.com.models.Product;
import web.com.service.CategoryService;
import web.com.service.ProductService;
import web.com.service.impl.CategoryServiceImpl;
import web.com.service.impl.ProductServiceImpl;
import web.com.service.VideoService;
import web.com.utils.Constant;

@WebServlet(urlPatterns = { "/home" })
public class HomeController extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl();
    private final CategoryService categoryService = new CategoryServiceImpl();
    private final VideoService videoService = new VideoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<Product> latestProducts = productService.getLatest(Constant.HOME_LATEST_PRODUCT_COUNT);
        List<Category> categories = categoryService.getAll();

        req.setAttribute("latestProducts", latestProducts);
        req.setAttribute("categories", categories);
        req.setAttribute("latestVideos", videoService.getPage(1, Constant.VIDEO_PAGE_SIZE));

        RequestDispatcher dispatcher = req.getRequestDispatcher("/views/home.jsp");
        dispatcher.forward(req, resp);
    }
}

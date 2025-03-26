package Controller.Admin;

import DAO.productDAO;
import Entity.Category;
import Entity.Color;
import Entity.Product;
import Entity.Size;
import Entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductManager extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "";

        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user != null && user.getIsAdmin().equalsIgnoreCase("true")) {
                productDAO dao = new productDAO();

                switch (action) {
                    case "":
                        List<Product> product = dao.getProduct();
                        List<Size> size = dao.getSize();
                        List<Color> color = dao.getColor();
                        List<Category> category = dao.getCategory();

                        request.setAttribute("CategoryData", category);
                        request.setAttribute("ProductData", product);
                        request.setAttribute("SizeData", size);
                        request.setAttribute("ColorData", color);

                        request.getRequestDispatcher("/admin/product.jsp").forward(request, response);
                        break;

                    case "insert":
                        List<Category> categories = dao.getCategory();
                        request.setAttribute("CategoryData", categories);
                        request.getRequestDispatcher("/admin/productinsert.jsp").forward(request, response);
                        break;

                    case "insertcategory":
                        String name = request.getParameter("name");
                        Category exist = dao.getCategoryByName(name);
                        if (exist != null) {
                            request.setAttribute("error", name + " already exists");
                            request.getRequestDispatcher("/admin/productinsert.jsp").forward(request, response);
                        } else {
                            dao.insertCategory(name);
                            response.sendRedirect("productmanager?action=insert");
                        }
                        break;

                    case "insertproduct":
                        handleInsertOrUpdateProduct(request, dao, false);
                        response.sendRedirect("productmanager?action=insert");
                        break;

                    case "updateproduct":
                        handleInsertOrUpdateProduct(request, dao, true);
                        response.sendRedirect("productmanager");
                        break;

                    case "deleteproduct":
                        String productId = request.getParameter("product_id");
                        dao.ProductDelete(productId);
                        response.sendRedirect("productmanager");
                        break;

                    default:
                        response.sendRedirect("productmanager");
                        break;
                }

            } else {
                response.sendRedirect("user?action=login");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("404.jsp");
        }
    }

    private void handleInsertOrUpdateProduct(HttpServletRequest request, productDAO dao, boolean isUpdate) {
        try {
            String product_id = request.getParameter("product_id");
            String category_id = request.getParameter("category_id");
            String product_name = request.getParameter("product_name");
            String product_price = request.getParameter("price") != null
                    ? request.getParameter("price") : request.getParameter("product_price");
            String product_size = request.getParameter("size") != null
                    ? request.getParameter("size") : request.getParameter("product_size");
            String product_color = request.getParameter("color") != null
                    ? request.getParameter("color") : request.getParameter("product_color");
            String product_quantity = request.getParameter("quantity") != null
                    ? request.getParameter("quantity") : request.getParameter("product_quantity");
            String product_img = "images/" + request.getParameter("product_img");
            String product_describe = request.getParameter("describe") != null
                    ? request.getParameter("describe") : request.getParameter("product_describe");

            int quantity = Integer.parseInt(product_quantity);
            float price = Float.parseFloat(product_price);
            int cid = Integer.parseInt(category_id);
            Category cate = new Category(cid);

            // Parse size
            String[] sizeArray = product_size.split("\\s*,\\s*");
            List<Size> sizeList = new ArrayList<>();
            for (String s : sizeArray) {
                sizeList.add(new Size(product_id, s));
            }

            // Parse color
            String[] colorArray = product_color.split("\\s*,\\s*");
            List<Color> colorList = new ArrayList<>();
            for (String c : colorArray) {
                colorList.add(new Color(product_id, c));
            }

            Product product = new Product();
            product.setProduct_id(product_id);
            product.setProduct_name(product_name);
            product.setProduct_price(price);
            product.setProduct_describe(product_describe);
            product.setQuantity(quantity);
            product.setImg(product_img);
            product.setCate(cate);
            product.setSize(sizeList);
            product.setColor(colorList);

            dao.insertProduct(product); // dùng chung hàm insert/update trong DAO
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Product Manager Servlet";
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Admin;

import DAO.userDAO;
import Entity.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


public class CustomerManager extends HttpServlet {

  
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html;charset=UTF-8");

    try {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");

        // Kiểm tra quyền admin
        if (user != null && "true".equalsIgnoreCase(user.getIsAdmin())) {
            userDAO dao = new userDAO();

            if (action == null || action.isEmpty()) {
                // Load danh sách người dùng
                List<User> userList = dao.getUser();
                request.setAttribute("user", userList);
                request.getRequestDispatcher("admin/customer.jsp").forward(request, response);
            } else if (action.equals("update")) {
                String user_id = request.getParameter("user_id");
                String isAdmin = request.getParameter("permission");
                int id = Integer.parseInt(user_id);
                dao.setAdmin(id, isAdmin);
                response.sendRedirect("customermanager");
            } else {
                // Nếu action không hợp lệ
                response.sendRedirect("customermanager");
            }
        } else {
            // Nếu chưa đăng nhập hoặc không phải admin
            response.sendRedirect("user?action=login");
        }

    } catch (Exception e) {
        e.printStackTrace(); // Log lỗi để debug
        response.sendRedirect("404.jsp");
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
        return "Short description";
    }// </editor-fold>

}

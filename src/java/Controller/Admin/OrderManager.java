package Controller.Admin;

import DAO.billDAO;
import Entity.Bill;
import Entity.BillDetail;
import Entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class OrderManager extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            String action = request.getParameter("action");
            billDAO dao = new billDAO();

            if (user != null && user.getIsAdmin().equalsIgnoreCase("true")) {
                if (action == null || action.isEmpty()) {
                    // Mặc định: hiển thị danh sách đơn hàng
                    List<Bill> bill = dao.getBillInfo();
                    request.setAttribute("bill", bill);
                    request.getRequestDispatcher("admin/order.jsp").forward(request, response);
                } else if (action.equals("showdetail")) {
                    String bill_id = request.getParameter("bill_id");
                    int id = Integer.parseInt(bill_id);
                    List<BillDetail> detail = dao.getDetail(id);
                    request.setAttribute("detail", detail);
                    request.getRequestDispatcher("admin/orderdetail.jsp").forward(request, response); 
                } else {
                    response.sendRedirect("ordermanager");
                }
            } else {
                response.sendRedirect("user?action=login");
            }

        } catch (Exception e) {
            e.printStackTrace();
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
        return "Order Manager Servlet";
    }
}

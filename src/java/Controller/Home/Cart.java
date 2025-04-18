/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Home;

import DAO.productDAO;
import Entity.Item;
import Entity.Product;
import java.io.IOException;
import java.util.List;
// Đổi từ javax.* sang jakarta.*
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


public class Cart extends HttpServlet {

   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String action = request.getParameter("action");
        
        if (action == null) {
            
            Entity.Cart cart = null;
            Object o = session.getAttribute("cart");
            if (o != null) {
                cart = (Entity.Cart) o;
            } else {
                cart = new Entity.Cart();
            }
            String Squantity = request.getParameter("quantity");
            String product_id = request.getParameter("product_id");
            String size = request.getParameter("size");
            String color = request.getParameter("color");
            
            try {
                int quanity = Integer.parseInt(Squantity);
                productDAO pdao = new productDAO();
                Product product = pdao.getProductByID(product_id);
                Item item = new Item(product, quanity, size, color);
                cart.addItem(item);

            } catch (Exception e) {
            }
            List<Item> list = cart.getItems();
            session.setAttribute("cart", cart);
            session.setAttribute("total", cart.getTotalMoney());
            session.setAttribute("size", list.size());
            request.getRequestDispatcher("product?action=productdetail&product_id=" + product_id).forward(request, response);
        }
        if (action.equalsIgnoreCase("showcart")) {
            request.getRequestDispatcher("cart.jsp").forward(request, response);
        }
        if (action.equals("deletecart")) {
            Entity.Cart cart = null;
            Object o = session.getAttribute("cart");
            if (o != null) {
                cart = (Entity.Cart) o;
            } else {
                cart = new Entity.Cart();
            }
            String product_id = request.getParameter("product_id");
            cart.removeItem(product_id);
            List<Item> list = cart.getItems();
            session.setAttribute("cart", cart);
            session.setAttribute("total", cart.getTotalMoney());
            session.setAttribute("size", list.size());
            response.sendRedirect("cart.jsp");
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

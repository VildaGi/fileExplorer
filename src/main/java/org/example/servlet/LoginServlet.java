package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.model.User;
import org.example.service.UserService;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = UserService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/");
        } else {
            response.sendRedirect(request.getContextPath() + "/login.html");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String login = request.getParameter("login");
        String password = request.getParameter("password");

        if (login == null || password == null || login.trim().isEmpty() || password.trim().isEmpty()) {
            response.setContentType("text/html");
            response.getWriter().println("<html><body><h3>Error: Login and password are required</h3>");
            response.getWriter().println("<a href='login.html'>Back to Login</a></body></html>");
            return;
        }

        User user = userService.login(login, password);

        if (user != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            response.sendRedirect(request.getContextPath() + "/");
        } else {
            response.setContentType("text/html");
            response.getWriter().println("<html><body><h3>Error: Invalid login or password</h3>");
            response.getWriter().println("<a href='login.html'>Back to Login</a></body></html>");
        }
    }
}
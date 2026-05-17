package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.UserService;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = UserService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/register.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        if (login == null || login.trim().isEmpty()) {
            response.setContentType("text/html");
            response.getWriter().println("<html><body><h3>Error: Login is required</h3>");
            response.getWriter().println("<a href='register.html'>Back to Registration</a></body></html>");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            response.setContentType("text/html");
            response.getWriter().println("<html><body><h3>Error: Password is required</h3>");
            response.getWriter().println("<a href='register.html'>Back to Registration</a></body></html>");
            return;
        }

        if (email == null || email.trim().isEmpty()) {
            response.setContentType("text/html");
            response.getWriter().println("<html><body><h3>Error: Email is required</h3>");
            response.getWriter().println("<a href='register.html'>Back to Registration</a></body></html>");
            return;
        }

        boolean success = userService.register(login, password, email);

        if (success) {
            response.setContentType("text/html");
            response.getWriter().println("<html><body><h3>Registration successful!</h3>");
            response.getWriter().println("<a href='login.html'>Go to Login</a></body></html>");
        } else {
            response.setContentType("text/html");
            response.getWriter().println("<html><body><h3>Error: Username already exists</h3>");
            response.getWriter().println("<a href='register.html'>Back to Registration</a></body></html>");
        }
    }
}
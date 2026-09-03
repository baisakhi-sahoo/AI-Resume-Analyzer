package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String email =
                request.getParameter("email");

        String password =
                request.getParameter("password");


        if (email == null ||
                password == null ||
                email.trim().isEmpty() ||
                password.trim().isEmpty()) {

            response.getWriter().println(
                    "<h2>Please enter email and password.</h2>"
            );

            return;
        }


        email = email.trim().toLowerCase();


        // Get users from RegisterServlet

        Map<String, String> users =
                RegisterServlet.getUsers();


        if (!users.containsKey(email)) {

            response.getWriter().println(
                    "<h2>Account not found.</h2>"
            );

            response.getWriter().println(
                    "<a href='register.html'>Create an account</a>"
            );

            return;
        }


        if (!users.get(email).equals(password)) {

            response.getWriter().println(
                    "<h2>Incorrect password.</h2>"
            );

            response.getWriter().println(
                    "<a href='login.html'>Try again</a>"
            );

            return;
        }


        // Login successful

        var session =
                request.getSession();

        session.setAttribute(
                "userEmail",
                email
        );

        session.setAttribute(
                "userName",
                RegisterServlet.getNames().get(email)
        );


        response.sendRedirect(
                request.getContextPath()
                        + "/analyze.html"
        );
    }
}
package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validate fields
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

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            String sql =
                    "SELECT name, password FROM users WHERE email = ?";

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, email);

                ResultSet resultSet =
                        statement.executeQuery();

                // User found
                if (resultSet.next()) {

                    String storedPassword =
                            resultSet.getString("password");

                    String name =
                            resultSet.getString("name");

                    // Check password
                    if (org.mindrot.jbcrypt.BCrypt.checkpw(
                            password,
                            storedPassword
                    )) {

                        HttpSession session =
                                request.getSession();

                        session.setAttribute(
                                "userEmail",
                                email
                        );

                        session.setAttribute(
                                "userName",
                                name
                        );

                        // Login successful
                        response.sendRedirect(
                                request.getContextPath()
                                        + "/upload.html"
                        );

                    } else {

                        response.getWriter().println(
                                "<h2>Incorrect password.</h2>"
                        );

                        response.getWriter().println(
                                "<a href='login.html'>Try Again</a>"
                        );
                    }

                } else {

                    response.getWriter().println(
                            "<h2>Account not found.</h2>"
                    );

                    response.getWriter().println(
                            "<a href='register.html'>Create an account</a>"
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                    "<h2>Login failed.</h2>"
            );

            response.getWriter().println(
                    "<p>Please try again.</p>"
            );
        }
    }
}
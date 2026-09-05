package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validate fields
        if (name == null ||
                email == null ||
                password == null ||
                name.trim().isEmpty() ||
                email.trim().isEmpty() ||
                password.trim().isEmpty()) {

            response.getWriter().println(
                    "<h2>Please fill all fields.</h2>"
            );

            return;
        }

        name = name.trim();
        email = email.trim().toLowerCase();

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            // Check whether email already exists
            String checkSql =
                    "SELECT id FROM users WHERE email = ?";

            try (PreparedStatement checkStatement =
                         connection.prepareStatement(checkSql)) {

                checkStatement.setString(1, email);

                ResultSet resultSet =
                        checkStatement.executeQuery();

                if (resultSet.next()) {

                    response.getWriter().println(
                            "<h2>Email already registered.</h2>"
                    );

                    response.getWriter().println(
                            "<a href='login.html'>Go to Login</a>"
                    );

                    return;
                }
            }

            // Insert new user
            String insertSql =
                    "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

            try (PreparedStatement insertStatement =
                         connection.prepareStatement(insertSql)) {

                insertStatement.setString(1, name);
                insertStatement.setString(2, email);
                String hashedPassword =
                        org.mindrot.jbcrypt.BCrypt.hashpw(
                                password,
                                org.mindrot.jbcrypt.BCrypt.gensalt()
                        );

                insertStatement.setString(3, hashedPassword);

                insertStatement.executeUpdate();
            }

            // Create session
            var session = request.getSession();

            session.setAttribute(
                    "userEmail",
                    email
            );

            session.setAttribute(
                    "userName",
                    name
            );

            // Go to resume analyzer
            response.sendRedirect(
                    request.getContextPath()
                            + "/upload.html"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                    "<h2>Registration failed.</h2>"
            );

            response.getWriter().println(
                    "<p>Please try again.</p>"
            );
        }
    }
}
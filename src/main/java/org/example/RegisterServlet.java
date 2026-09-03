package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final Map<String, String> users =
            new HashMap<>();

    private static final Map<String, String> names =
            new HashMap<>();


    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String name =
                request.getParameter("name");

        String email =
                request.getParameter("email");

        String password =
                request.getParameter("password");


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


        email = email.trim().toLowerCase();


        // Check existing user

        if (users.containsKey(email)) {

            response.getWriter().println(
                    "<h2>Email already registered.</h2>"
            );

            response.getWriter().println(
                    "<a href='login.html'>Go to Login</a>"
            );

            return;
        }


        // Store user

        users.put(email, password);
        names.put(email, name);


        // Automatically create session

        var session =
                request.getSession();

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
                        + "/analyze.html"
        );
    }
    public static Map<String, String> getUsers() {
        return users;
    }

    public static Map<String, String> getNames() {
        return names;
    }
}

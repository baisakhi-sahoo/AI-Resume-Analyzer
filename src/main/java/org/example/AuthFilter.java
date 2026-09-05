package org.example;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        String path = httpRequest.getRequestURI();

        String contextPath =
                httpRequest.getContextPath();

        String relativePath =
                path.substring(contextPath.length());

        // Public pages and resources
        if (relativePath.equals("/") ||
                relativePath.equals("/index.html") ||
                relativePath.equals("/login.html") ||
                relativePath.equals("/register.html") ||
                relativePath.startsWith("/css/") ||
                relativePath.startsWith("/js/") ||
                relativePath.equals("/login") ||
                relativePath.equals("/register") ||
                relativePath.equals("/logout")) {

            chain.doFilter(request, response);
            return;
        }

        // Check login session
        HttpSession session =
                httpRequest.getSession(false);

        if (session == null ||
                session.getAttribute("userEmail") == null) {

            httpResponse.sendRedirect(
                    contextPath + "/login.html"
            );

            return;
        }

        // User is logged in
        chain.doFilter(request, response);
    }
}

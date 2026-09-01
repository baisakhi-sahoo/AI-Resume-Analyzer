package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/career-roadmap-data")
public class careerRoadmapServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        var session = request.getSession();

        Object targetRole =
                session.getAttribute("targetRole");

        Object projectRecommendations =
                session.getAttribute("projectRecommendations");

        Object interviewPreparation =
                session.getAttribute("interviewPreparation");

        Object jobApplicationChecklist =
                session.getAttribute("jobApplicationChecklist");

        String json = "{"
                + "\"targetRole\":\"" + escapeJson(targetRole) + "\","
                + "\"projectRecommendations\":\""
                + escapeJson(projectRecommendations) + "\","
                + "\"interviewPreparation\":\""
                + escapeJson(interviewPreparation) + "\","
                + "\"jobApplicationChecklist\":\""
                + escapeJson(jobApplicationChecklist) + "\""
                + "}";

        response.getWriter().print(json);
    }

    private String escapeJson(Object value) {

        if (value == null) {
            return "";
        }

        return value.toString()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
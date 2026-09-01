package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/analysis-page2-data")
public class AnalysisPage2Servlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Object recommendedJobs =
                request.getSession().getAttribute("recommendedJobs");

        Object skillsToLearn =
                request.getSession().getAttribute("skillsToLearn");

        Object aiSuggestions =
                request.getSession().getAttribute("aiSuggestions");

        Object improvementTips =
                request.getSession().getAttribute("improvementTips");

        String json = "{"
                + "\"recommendedJobs\":\"" + escapeJson(recommendedJobs) + "\","
                + "\"skillsToLearn\":\"" + escapeJson(skillsToLearn) + "\","
                + "\"aiSuggestions\":\"" + escapeJson(aiSuggestions) + "\","
                + "\"improvementTips\":\"" + escapeJson(improvementTips) + "\""
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
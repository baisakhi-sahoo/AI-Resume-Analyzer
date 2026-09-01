package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/analysis-data")
public class AnalysisDataServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Object atsScore = request.getSession().getAttribute("atsScore");
        Object matchingSkills = request.getSession().getAttribute("matchingSkills");
        Object missingSkills = request.getSession().getAttribute("missingSkills");
        Object strengths = request.getSession().getAttribute("strengths");
        Object weaknesses = request.getSession().getAttribute("weaknesses");
        Object resumeSummary = request.getSession().getAttribute("resumeSummary");
        Object improvementSuggestions =
                request.getSession().getAttribute("improvementSuggestions");
        String json = "{"
                + "\"atsScore\":\"" + escapeJson(atsScore) + "\","
                + "\"matchingSkills\":\"" + escapeJson(matchingSkills) + "\","
                + "\"missingSkills\":\"" + escapeJson(missingSkills) + "\","
                + "\"strengths\":\"" + escapeJson(strengths) + "\","
                + "\"weaknesses\":\"" + escapeJson(weaknesses) + "\","
                + "\"resumeSummary\":\"" + escapeJson(resumeSummary) + "\","
                + "\"improvementSuggestions\":\"" + escapeJson(improvementSuggestions) + "\""
                    +"}";
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
package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/analyze")
@jakarta.servlet.annotation.MultipartConfig
public class ResumeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        out.println("<h1>Resume Analyzer</h1>");
        out.println("<p>Please upload your resume to start the analysis.</p>");
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        Part resumePart = request.getPart("resume");

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        if (resumePart != null && resumePart.getSize() > 0) {

            String fileName = resumePart.getSubmittedFileName();
            if (fileName.toLowerCase().endsWith(".pdf")) {

                byte[] fileData = resumePart.getInputStream().readAllBytes();

                try (PDDocument document = Loader.loadPDF(fileData)) {

                    PDFTextStripper stripper = new PDFTextStripper();
                    String resumeText = stripper.getText(document);
                    String lowerText = resumeText.toLowerCase();
                    List<String> detectedSkills = new ArrayList<>();

                    if (lowerText.contains("java")) {
                        detectedSkills.add("Java");
                    }

                    if (lowerText.contains("sql")) {
                        detectedSkills.add("SQL");
                    }

                    if (lowerText.contains("html")) {
                        detectedSkills.add("HTML");
                    }

                    if (lowerText.contains("css")) {
                        detectedSkills.add("CSS");
                    }

                    if (lowerText.contains("javascript")) {
                        detectedSkills.add("JavaScript");
                    }

                    out.println("<h2>Basic Resume Analysis</h2>");
                    out.println("<p>Skills detected: " + detectedSkills.size() + "</p>");

                    out.println("<h3>Skills Found:</h3>");
                    out.println("<ul>");

                    for (String skill : detectedSkills) {
                        out.println("<li>" + skill + "</li>");
                    }

                    out.println("</ul>");
                    int atsScore = 0;

                    if (!resumeText.trim().isEmpty()) {
                        atsScore += 20;
                    }

                    atsScore += detectedSkills.size() * 10;

                    if (atsScore > 100) {
                        atsScore = 100;
                    }

                    out.println("<h2>ATS Score: " + atsScore + "/100</h2>");
                    out.println("<h2>Basic Resume Analysis</h2>");
                    out.println("<p>Skills detected: " + detectedSkills.size()+ "</p>");
                    System.out.println("===== EXTRACTED RESUME TEXT =====");
                    System.out.println(resumeText);
                    System.out.println("===== END RESUME TEXT =====");
                    out.println("<h1>Resume Text Extracted!</h1>");
                    out.println("<pre>" + resumeText + "</pre>");
                }
            }

            out.println("<h1>Resume Received!</h1>");
            out.println("<p>File name: " + fileName + "</p>");
            out.println("<p>File size: " + resumePart.getSize() + " bytes</p>");

        } else {

            out.println("<h1>No Resume Uploaded</h1>");
            out.println("<p>Please select a resume and try again.</p>");
        }
    }
}
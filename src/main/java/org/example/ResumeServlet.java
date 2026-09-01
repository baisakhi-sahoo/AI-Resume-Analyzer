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
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

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
                    Client client = Client.builder()
                            .apiKey(System.getenv("GEMINI_API_KEY"))
                            .build();
                    String prompt = """
Analyze the following resume for a professional job application.

Return the analysis using EXACTLY these sections:

ATS_ANALYSIS:
Give a short explanation of how ATS-friendly the resume is.

MATCHING_SKILLS:
List the important technical skills found in the resume.

MISSING_SKILLS:
List important skills that appear to be missing or could strengthen the candidate's profile.

STRENGTHS:
List 3 to 5 strong points about the resume.

WEAKNESSES:
List 3 to 5 weaknesses or areas that should be improved.

RESUME_SUMMARY:
Give a short professional summary of the candidate's current profile.

IMPROVEMENT_SUGGESTIONS:
Give 3 to 5 practical suggestions to improve the resume.

IMPORTANT:
- Do not invent experience that is not present in the resume.
- Base the analysis only on the information provided.
- Keep the language professional and easy to understand.
- Do not give an ATS score number. We will calculate that separately in Java.

Resume:
""" + resumeText;
                    GenerateContentResponse airesponse =
                            client.models.generateContent(
                                    "gemini-3.6-flash",
                                    prompt,
                                    null
                            );

                    String aiAnalysis = airesponse.text();
                    String atsAnalysis = "";
                    String matchingSkills = "";
                    String missingSkills = "";
                    String strengths = "";
                    String weaknesses = "";
                    String resumeSummary = "";
                    String improvementSuggestions = "";

                    String[] sections = aiAnalysis.split("\n\n");

                    for (String section : sections) {

                        if (section.startsWith("ATS_ANALYSIS:")) {
                            atsAnalysis = section.replace("ATS_ANALYSIS:", "").trim();

                        } else if (section.startsWith("MATCHING_SKILLS:")) {
                            matchingSkills = section.replace("MATCHING_SKILLS:", "").trim();

                        } else if (section.startsWith("MISSING_SKILLS:")) {
                            missingSkills = section.replace("MISSING_SKILLS:", "").trim();

                        } else if (section.startsWith("STRENGTHS:")) {
                            strengths = section.replace("STRENGTHS:", "").trim();

                        } else if (section.startsWith("WEAKNESSES:")) {
                            weaknesses = section.replace("WEAKNESSES:", "").trim();

                        } else if (section.startsWith("RESUME_SUMMARY:")) {
                            resumeSummary = section.replace("RESUME_SUMMARY:", "").trim();

                        } else if (section.startsWith("IMPROVEMENT_SUGGESTIONS:")) {
                            improvementSuggestions =
                                    section.replace("IMPROVEMENT_SUGGESTIONS:", "").trim();
                        }
                    }
                    System.out.println("===== AI RESUME ANALYSIS =====");
                    System.out.println(aiAnalysis);
                    System.out.println("===== END AI ANALYSIS =====");
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
                    request.getSession().setAttribute("atsScore", atsScore);
                    request.getSession().setAttribute("atsAnalysis", atsAnalysis);
                    request.getSession().setAttribute("matchingSkills", matchingSkills);
                    request.getSession().setAttribute("missingSkills", missingSkills);
                    request.getSession().setAttribute("strengths", strengths);
                    request.getSession().setAttribute("weaknesses", weaknesses);
                    request.getSession().setAttribute("resumeSummary", resumeSummary);
                    request.getSession().setAttribute(
                            "improvementSuggestions",
                            improvementSuggestions
                    );
                    response.sendRedirect(request.getContextPath()+"/analysis.html");
                    return;

                }
            }



        } else {

            out.println("<h1>No Resume Uploaded</h1>");
            out.println("<p>Please select a resume and try again.</p>");
        }
    }
}
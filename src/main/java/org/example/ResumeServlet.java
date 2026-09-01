package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/analyze")
@MultipartConfig
public class ResumeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

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
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();


        // ==========================================
        // CHECK RESUME
        // ==========================================

        if (resumePart == null || resumePart.getSize() == 0) {

            out.println("<h1>No Resume Uploaded</h1>");
            out.println("<p>Please select a resume and try again.</p>");

            return;
        }


        // ==========================================
        // CHECK PDF
        // ==========================================

        String fileName =
                resumePart.getSubmittedFileName();

        if (fileName == null ||
                !fileName.toLowerCase().endsWith(".pdf")) {

            out.println("<h1>Invalid File</h1>");
            out.println("<p>Please upload a PDF resume.</p>");

            return;
        }


        // ==========================================
        // READ PDF
        // ==========================================

        byte[] fileData =
                resumePart.getInputStream().readAllBytes();


        try (PDDocument document =
                     Loader.loadPDF(fileData)) {


            // ==========================================
            // EXTRACT TEXT
            // ==========================================

            PDFTextStripper stripper =
                    new PDFTextStripper();

            String resumeText =
                    stripper.getText(document);


            // ==========================================
            // GEMINI CLIENT
            // ==========================================

            Client client =
                    Client.builder()
                            .apiKey(
                                    System.getenv(
                                            "GEMINI_API_KEY"
                                    )
                            )
                            .build();


            // ==========================================
            // GEMINI PROMPT
            // ==========================================

            String prompt = """

                    Analyze the following resume for a professional job application.

                    Return the analysis using EXACTLY these section headings:

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

                    RECOMMENDED_JOB_ROLES:
                    Suggest 3 to 5 job roles that match the candidate's current skills.

                    SKILLS_TO_LEARN:
                    Suggest 5 to 8 important skills the candidate should learn for better career opportunities.

                    AI_SUGGESTIONS:
                    Give 3 to 5 personalized career suggestions based only on the resume.

                    RESUME_IMPROVEMENT_TIPS:
                    Give 3 to 5 practical tips to improve the resume.


                    TARGET_ROLE:
                    Choose ONE most suitable target job role for this candidate based on the resume.

                    PROJECT_RECOMMENDATIONS:
                    Suggest 3 practical projects that would help the candidate become stronger for the TARGET_ROLE.

                    For each project include:
                    Project Name:
                    Description:
                    Technologies:
                    Difficulty:

                    INTERVIEW_PREPARATION:
                    Give 5 important interview preparation areas for the TARGET_ROLE.

                    Include:
                    - Technical topics
                    - Important concepts
                    - Practical preparation
                    - Questions the candidate should practice

                    JOB_APPLICATION_CHECKLIST:
                    Give 5 to 7 practical steps the candidate should complete before applying for jobs.

                    IMPORTANT:
                    - Do not invent experience that is not present in the resume.
                    - Base the analysis only on the information provided.
                    - Keep the language professional and easy to understand.
                    - Do not give an ATS score number.
                    - We will calculate the ATS score separately in Java.
                    - Do not invent experience, projects, or skills that are not present in the resume.
                    - Recommended skills in SKILLS_TO_LEARN are learning recommendations only.
                    - Project recommendations are suggestions for future projects.
                    - Put each item on a separate line.
                    - Keep each section clearly separated.
                    - Do not combine multiple sections into one paragraph.

                    Resume:

                    """ + resumeText;


            // ==========================================
            // CALL GEMINI
            // ==========================================

            GenerateContentResponse aiResponse =
                    client.models.generateContent(
                            "gemini-3.6-flash",
                            prompt,
                            null
                    );


            String aiAnalysis =
                    aiResponse.text();


            // ==========================================
            // PRINT AI RESPONSE
            // ==========================================

            System.out.println(
                    "================================"
            );

            System.out.println(
                    "AI RESUME ANALYSIS"
            );

            System.out.println(
                    "================================"
            );

            System.out.println(aiAnalysis);

            System.out.println(
                    "================================"
            );


            // ==========================================
            // VARIABLES - PAGE 1
            // ==========================================

            String atsAnalysis = "";
            String matchingSkills = "";
            String missingSkills = "";
            String strengths = "";
            String weaknesses = "";
            String resumeSummary = "";
            String improvementSuggestions = "";


            // ==========================================
            // VARIABLES - PAGE 2
            // ==========================================

            String recommendedJobs = "";
            String skillsToLearn = "";
            String aiSuggestions = "";
            String improvementTips = "";


            // ==========================================
            // VARIABLES - PAGE 3
            // ==========================================

            String targetRole = "";
            String projectRecommendations = "";
            String interviewPreparation = "";
            String jobApplicationChecklist = "";


            // ==========================================
            // PARSE AI RESPONSE
            // ==========================================

            String[] sections =
                    aiAnalysis.split(
                            "(?=ATS_ANALYSIS:|"
                                    + "MATCHING_SKILLS:|"
                                    + "MISSING_SKILLS:|"
                                    + "STRENGTHS:|"
                                    + "WEAKNESSES:|"
                                    + "RESUME_SUMMARY:|"
                                    + "IMPROVEMENT_SUGGESTIONS:|"
                                    + "RECOMMENDED_JOB_ROLES:|"
                                    + "SKILLS_TO_LEARN:|"
                                    + "AI_SUGGESTIONS:|"
                                    + "RESUME_IMPROVEMENT_TIPS:|"
                                    + "TARGET_ROLE:|"
                                    + "PROJECT_RECOMMENDATIONS:|"
                                    + "INTERVIEW_PREPARATION:|"
                                    + "JOB_APPLICATION_CHECKLIST:)"
                    );


            for (String section : sections) {

                section = section.trim();


                // ======================================
                // PAGE 1
                // ======================================

                if (section.startsWith("ATS_ANALYSIS:")) {

                    atsAnalysis =
                            section
                                    .replaceFirst(
                                            "ATS_ANALYSIS:",
                                            ""
                                    )
                                    .trim();


                } else if (section.startsWith("MATCHING_SKILLS:")) {

                    matchingSkills =
                            section
                                    .replaceFirst(
                                            "MATCHING_SKILLS:",
                                            ""
                                    )
                                    .trim();


                } else if (section.startsWith("MISSING_SKILLS:")) {

                    missingSkills =
                            section
                                    .replaceFirst(
                                            "MISSING_SKILLS:",
                                            ""
                                    )
                                    .trim();


                } else if (section.startsWith("STRENGTHS:")) {

                    strengths =
                            section
                                    .replaceFirst(
                                            "STRENGTHS:",
                                            ""
                                    )
                                    .trim();


                } else if (section.startsWith("WEAKNESSES:")) {

                    weaknesses =
                            section
                                    .replaceFirst(
                                            "WEAKNESSES:",
                                            ""
                                    )
                                    .trim();


                } else if (section.startsWith("RESUME_SUMMARY:")) {

                    resumeSummary =
                            section
                                    .replaceFirst(
                                            "RESUME_SUMMARY:",
                                            ""
                                    )
                                    .trim();


                } else if (section.startsWith(
                        "IMPROVEMENT_SUGGESTIONS:")) {

                    improvementSuggestions =
                            section
                                    .replaceFirst(
                                            "IMPROVEMENT_SUGGESTIONS:",
                                            ""
                                    )
                                    .trim();


                    // ======================================
                    // PAGE 2
                    // ======================================

                } else if (section.startsWith(
                        "RECOMMENDED_JOB_ROLES:")) {

                    recommendedJobs =
                            section
                                    .replaceFirst(
                                            "RECOMMENDED_JOB_ROLES:",
                                            ""
                                    )
                                    .trim();


                } else if (section.startsWith(
                        "SKILLS_TO_LEARN:")) {

                    skillsToLearn =
                            section
                                    .replaceFirst(
                                            "SKILLS_TO_LEARN:",
                                            ""
                                    )
                                    .trim();


                } else if (section.startsWith(
                        "AI_SUGGESTIONS:")) {

                    aiSuggestions =
                            section
                                    .replaceFirst(
                                            "AI_SUGGESTIONS:",
                                            ""
                                    )
                                    .trim();


                } else if (section.startsWith(
                        "RESUME_IMPROVEMENT_TIPS:")) {

                    improvementTips =
                            section
                                    .replaceFirst(
                                            "RESUME_IMPROVEMENT_TIPS:",
                                            ""
                                    )
                                    .trim();


                    // ======================================
                    // PAGE 3
                    // ======================================

                } else if (section.startsWith(
                        "TARGET_ROLE:")) {

                    targetRole =
                            section
                                    .replaceFirst(
                                            "TARGET_ROLE:",
                                            ""
                                    )
                                    .trim();


                } else if (section.startsWith(
                        "PROJECT_RECOMMENDATIONS:")) {

                    projectRecommendations =
                            section
                                    .replaceFirst(
                                            "PROJECT_RECOMMENDATIONS:",
                                            ""
                                    )
                                    .trim();


                } else if (section.startsWith(
                        "INTERVIEW_PREPARATION:")) {

                    interviewPreparation =
                            section
                                    .replaceFirst(
                                            "INTERVIEW_PREPARATION:",
                                            ""
                                    )
                                    .trim();


                } else if (section.startsWith(
                        "JOB_APPLICATION_CHECKLIST:")) {

                    jobApplicationChecklist =
                            section
                                    .replaceFirst(
                                            "JOB_APPLICATION_CHECKLIST:",
                                            ""
                                    )
                                    .trim();
                }
            }


            // ==========================================
            // BASIC SKILL DETECTION
            // ==========================================

            String lowerText =
                    resumeText.toLowerCase();

            List<String> detectedSkills =
                    new ArrayList<>();


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

            if (lowerText.contains("python")) {
                detectedSkills.add("Python");
            }

            if (lowerText.contains("spring")) {
                detectedSkills.add("Spring");
            }

            if (lowerText.contains("react")) {
                detectedSkills.add("React");
            }


            // ==========================================
            // ATS SCORE
            // ==========================================

            int atsScore = 0;


            if (!resumeText.trim().isEmpty()) {
                atsScore += 20;
            }


            atsScore +=
                    detectedSkills.size() * 10;


            if (resumeText.length() > 500) {
                atsScore += 10;
            }


            if (resumeText.length() > 1000) {
                atsScore += 10;
            }


            if (atsScore > 100) {
                atsScore = 100;
            }


            // ==========================================
            // SAVE PAGE 1 DATA
            // ==========================================

            request.getSession().setAttribute(
                    "atsScore",
                    atsScore
            );

            request.getSession().setAttribute(
                    "atsAnalysis",
                    atsAnalysis
            );

            request.getSession().setAttribute(
                    "matchingSkills",
                    matchingSkills
            );

            request.getSession().setAttribute(
                    "missingSkills",
                    missingSkills
            );

            request.getSession().setAttribute(
                    "strengths",
                    strengths
            );

            request.getSession().setAttribute(
                    "weaknesses",
                    weaknesses
            );

            request.getSession().setAttribute(
                    "resumeSummary",
                    resumeSummary
            );

            request.getSession().setAttribute(
                    "improvementSuggestions",
                    improvementSuggestions
            );


            // ==========================================
            // SAVE PAGE 2 DATA
            // ==========================================

            request.getSession().setAttribute(
                    "recommendedJobs",
                    recommendedJobs
            );

            request.getSession().setAttribute(
                    "skillsToLearn",
                    skillsToLearn
            );

            request.getSession().setAttribute(
                    "aiSuggestions",
                    aiSuggestions
            );

            request.getSession().setAttribute(
                    "improvementTips",
                    improvementTips
            );


            // ==========================================
            // SAVE PAGE 3 DATA
            // ==========================================

            request.getSession().setAttribute(
                    "targetRole",
                    targetRole
            );

            request.getSession().setAttribute(
                    "projectRecommendations",
                    projectRecommendations
            );

            request.getSession().setAttribute(
                    "interviewPreparation",
                    interviewPreparation
            );

            request.getSession().setAttribute(
                    "jobApplicationChecklist",
                    jobApplicationChecklist
            );


            // ==========================================
            // PAGE 3 DEBUG
            // ==========================================

            System.out.println(
                    "===== PAGE 3 DATA ====="
            );

            System.out.println(
                    "Target Role: "
                            + targetRole
            );

            System.out.println(
                    "Project Recommendations: "
                            + projectRecommendations
            );

            System.out.println(
                    "Interview Preparation: "
                            + interviewPreparation
            );

            System.out.println(
                    "Job Application Checklist: "
                            + jobApplicationChecklist
            );

            System.out.println(
                    "======================="
            );


            // ==========================================
            // REDIRECT TO PAGE 1
            // ==========================================

            response.sendRedirect(
                    request.getContextPath()
                            + "/analysis.html"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            out.println(
                    "<h1>Analysis Error</h1>"
            );

            out.println(
                    "<p>Something went wrong while analyzing the resume.</p>"
            );

            out.println(
                    "<p>"
                            + e.getMessage()
                            + "</p>"
            );
        }
    }
}
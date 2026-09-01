function formatList(text) {

    if (!text || text.trim() === "") {
        return "<p>No information available.</p>";
    }

    return text
        .split(/\r?\n/)
        .map(line => line.trim())
        .filter(line => line !== "")
        .map(line => {

            // Remove bullets and numbering
            line = line.replace(/^[-•]\s/, "");
            line = line.replace(/^\d+[.)]\s*/, "");

            return "<li>" + line + "</li>";
        })
        .join("");
}


// ==========================================
// PROJECT RECOMMENDATIONS
// ==========================================

function formatProjects(text) {

    if (!text || text.trim() === "") {
        return "<p>No project recommendations available.</p>";
    }

    return text
        .split(/\r?\n/)
        .map(line => line.trim())
        .filter(line => line !== "")
        .map(line => {

            line = line.replace(/^[-•]\s/, "");

            return `
                <div class="project-item">
                    <div class="project-name">
                        💻 ${line}
                    </div>
                </div>
            `;
        })
        .join("");
}


// ==========================================
// INTERVIEW PREPARATION
// ==========================================

function formatInterview(text) {

    if (!text || text.trim() === "") {
        return "<p>No interview preparation available.</p>";
    }

    return text
        .split(/\r?\n/)
        .map(line => line.trim())
        .filter(line => line !== "")
        .map(line => {

            line = line.replace(/^[-•]\s/, "");
            line = line.replace(/^\d+[.)]\s*/, "");

            return `
                <div class="interview-item">
                    <span class="interview-icon">🎤</span>
                    <span>${line}</span>
                </div>
            `;
        })
        .join("");
}


// ==========================================
// JOB APPLICATION CHECKLIST
// ==========================================

function formatChecklist(text) {

    if (!text || text.trim() === "") {
        return "<p>No checklist available.</p>";
    }

    return text
        .split(/\r?\n/)
        .map(line => line.trim())
        .filter(line => line !== "")
        .map(line => {

            line = line.replace(/^[-•]\s/, "");
            line = line.replace(/^\d+[.)]\s*/, "");

            return `
                <label class="check-item">
                    <input type="checkbox">
                    <span>${line}</span>
                </label>
            `;
        })
        .join("");
}


// ==========================================
// LOAD CAREER ROADMAP DATA
// ==========================================

async function loadCareerRoadmap() {

    try {

        const response =
            await fetch("career-roadmap-data");

        if (!response.ok) {
            throw new Error(
                "Failed to load career roadmap data"
            );
        }

        const data =
            await response.json();


        // ======================================
        // TARGET ROLE
        // ======================================

        const targetRole =
            document.getElementById("targetRole");

        if (targetRole) {

            targetRole.textContent =
                data.targetRole ||
                "No target role available.";
        }


        // ======================================
        // PROJECTS
        // ======================================

        const projects =
            document.getElementById(
                "projectRecommendations"
            );

        if (projects) {

            projects.innerHTML =
                formatProjects(
                    data.projectRecommendations
                );
        }


        // ======================================
        // INTERVIEW
        // ======================================

        const interview =
            document.getElementById(
                "interviewPreparation"
            );

        if (interview) {

            interview.innerHTML =
                formatInterview(
                    data.interviewPreparation
                );
        }


        // ======================================
        // CHECKLIST
        // ======================================

        const checklist =
            document.getElementById(
                "jobApplicationChecklist"
            );

        if (checklist) {

            checklist.innerHTML =
                formatChecklist(
                    data.jobApplicationChecklist
                );
        }


        console.log(
            "Career roadmap data loaded successfully."
        );

    } catch (error) {

        console.error(
            "Error loading career roadmap:",
            error
        );
    }
}


// ==========================================
// START
// ==========================================

loadCareerRoadmap();
function cleanText(text) {
    if (!text) {
        return "";
    }

    return text
        .replace(/^[-•]\s/, "")
        .replace(/^\d+[\.\)]\s*/, "")
        .trim();
}


function formatJobs(text) {

    if (!text) {
        return "<p>No job roles available.</p>";
    }

    const items = text
        .split(/\n+/)
        .map(cleanText)
        .filter(item => item.length > 0);

    return items.map((job, index) => {

        return `
            <div class="career-item role-card">

                <div class="career-number">
                    ${index + 1}
                </div>

                <div class="career-content">
                    <strong>${job}</strong>
                </div>

            </div>
        `;

    }).join("");
}


function formatSkills(text) {

    if (!text) {
        return "<p>No skills available.</p>";
    }

    const items = text
        .split(/\n+/)
        .map(cleanText)
        .filter(item => item.length > 0);

    return `
        <div class="skills-container">

            ${items.map(skill => `
                <span class="learning-skill">
                    📚 ${skill}
                </span>
            `).join("")}

        </div>
    `;
}


function formatSuggestions(text) {

    if (!text) {
        return "<p>No information available.</p>";
    }

    const items = text
        .split(/\n+/)
        .map(cleanText)
        .filter(item => item.length > 0);

    return items.map((item, index) => {

        return `
            <div class="suggestion-item">

                <div class="suggestion-number">
                    ${index + 1}
                </div>

                <div class="suggestion-text">
                    ${item}
                </div>

            </div>
        `;

    }).join("");
}


function formatTips(text) {

    if (!text) {
        return "<p>No information available.</p>";
    }

    const items = text
        .split(/\n+/)
        .map(cleanText)
        .filter(item => item.length > 0);

    return items.map((item, index) => {

        return `
            <div class="tip-item">

                <div class="tip-number">
                    ${index + 1}
                </div>

                <div class="tip-text">
                    ${item}
                </div>

            </div>
        `;

    }).join("");
}


async function loadCareerData() {

    try {

        const response =
            await fetch("analysis-page2-data");

        if (!response.ok) {
            throw new Error("Unable to load career data");
        }

        const data =
            await response.json();

        console.log("Career Page 2 Data:", data);


        document.getElementById("jobRoles").innerHTML =
            formatJobs(data.recommendedJobs);


        document.getElementById("skillsToLearn").innerHTML =
            formatSkills(data.skillsToLearn);


        document.getElementById("aiSuggestions").innerHTML =
            formatSuggestions(data.aiSuggestions);


        document.getElementById("improvementTips").innerHTML =
            formatTips(data.improvementTips);


    } catch (error) {

        console.error(
            "Error loading career analysis:",
            error
        );

    }
}


loadCareerData();
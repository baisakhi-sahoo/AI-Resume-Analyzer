function formatText(text) {
    if (!text) return "<p>No information available.</p>";

    return text
        .split("\n")
        .filter(line => line.trim() !== "")
        .map(line => {
            line = line.replace(/^[-•]\s/, "");
            return "<li>" + line + "</li>";
        })
        .join("");
}

async function loadAnalysis() {

    try {
        const response = await fetch("analysis-data");
        const data = await response.json();

        document.getElementById("atsScore").textContent =
            data.atsScore;

        document.getElementById("atsMessage").textContent =
            "Your resume has been analyzed successfully.";

       document.getElementById("matchingSkills").innerHTML =
           formatSkills(data.matchingSkills, "matching");

       document.getElementById("missingSkills").innerHTML =
           formatSkills(data.missingSkills, "missing");

        document.getElementById("strengths").innerHTML =
            "<ul>" + formatText(data.strengths) + "</ul>";

        document.getElementById("weaknesses").innerHTML =
            "<ul>" + formatText(data.weaknesses) + "</ul>";

        document.getElementById("resumeSummary").innerHTML =
            "<p>" + data.resumeSummary + "</p>";

        document.getElementById("improvementSuggestions").innerHTML =
            "<ul>" + formatText(data.improvementSuggestions) + "</ul>";

    } catch (error) {
        console.error(error);
    }
}
function formatSkills(text, type) {
    if (!text) return "";

    return text
        .split(/[,•\n-]+/)
        .map(skill => skill.trim())
        .filter(skill => skill.length > 1)
        .map(skill =>
            `<span class="skill-tag ${type}">${skill}</span>`
        )
        .join("");
}
loadAnalysis();
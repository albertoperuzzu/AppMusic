document.addEventListener("DOMContentLoaded", function() {
    const generateButton = document.getElementById("callAI");
    const resultDiv = document.getElementById("result");
    const translationContainer = document.getElementById("translation-container");

    generateButton.addEventListener("click", function() {
        fetch("/client/AI/get_translation", {
            method: "POST"
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Errore nella chiamata all'API");
            }
            return response.text();
        })
        .then(data => {
            let translationText = "";
            try {
                const json = JSON.parse(data);
                translationText = json.candidates[0].content.parts[0].text;
            } catch (error) {
                console.error("Errore nel parsing JSON:", error);
                translationText = "Errore nel parsing della risposta.";
            }
            resultDiv.innerText = translationText;
            generateButton.style.display = "none";
            translationContainer.classList.add("translated");
        })
        .catch(error => {
            console.error("Errore:", error);
            resultDiv.innerText = "Si è verificato un errore durante la generazione.";
        });
    });
});
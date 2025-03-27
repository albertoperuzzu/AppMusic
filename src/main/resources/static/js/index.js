    // Recupera il token dall'elemento nascosto (Thymeleaf)
    document.addEventListener("DOMContentLoaded", function () {
        let accessToken = document.getElementById("spotifyToken").value;
        if (accessToken) {
            console.log("Tokenb di accesso recuperato!");
        } else {
            console.error("Nessun access token nella request");
        }
    });
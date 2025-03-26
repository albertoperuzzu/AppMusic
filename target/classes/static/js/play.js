document.addEventListener("DOMContentLoaded", function () {
    const playButton = document.getElementById("playBtn");
    const pauseButton = document.getElementById("pauseBtn");
    const accessToken = document.getElementById("spotifyToken").value;

    if (!accessToken) {
        alert("Access token non trovato! Assicurati di essere autenticato.");
        return;
    }

    async function controlSpotify(action) {
        const apiUrl = `https://api.spotify.com/v1/me/player/${action}`;

        try {
            const response = await fetch(apiUrl, {
                method: "PUT",
                headers: {
                    "Authorization": "Bearer " + accessToken,
                    "Content-Type": "application/json"
                }
            });

            if (response.ok) {
                console.log(`${action.toUpperCase()} eseguito con successo`);
            } else {
                const errorText = await response.text();
                console.error(`Errore API: ${errorText}`);
            }
        } catch (error) {
            console.error("Errore nella richiesta API:", error);
        }
    }

    playButton.addEventListener("click", () => controlSpotify("play"));
    pauseButton.addEventListener("click", () => controlSpotify("pause"));
});
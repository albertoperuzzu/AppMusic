async function checkSpotifyDevices(accessToken) {
        try {
            let response = await fetch("https://api.spotify.com/v1/me/player/devices", {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + accessToken
                }
            });

            let data = await response.json();

            if (data.devices && data.devices.length > 0) {
                console.log("Dispositivi trovati");
            } else {
                console.log("Nessun dispositivo, apro app");
                setTimeout(() => {
                    window.location.href = "spotify://";
                }, 1000); // Aspetta 1 secondo per evitare conflitti
            }

        } catch (error) {
            console.error("Errore nella richiesta API:", error);
        }
    }

    // Recupera il token dall'elemento nascosto (Thymeleaf)
    document.addEventListener("DOMContentLoaded", function () {
        let accessToken = document.getElementById("spotifyToken").value;
        if (accessToken) {
            checkSpotifyDevices(accessToken);
        } else {
            console.error("Nessun access token");
        }
    });
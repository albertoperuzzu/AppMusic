document.addEventListener("DOMContentLoaded", function () {
    const playButton = document.getElementById("playBtn");
    const pauseButton = document.getElementById("pauseBtn");
    const restartButton = document.getElementById("restartBtn");
    const prevButton = document.getElementById("prevBtn");
    const nextButton = document.getElementById("nextBtn");

    async function controlSpotify(action) {
        const apiUrl = `/client/spotify/put/${action}`;
        try {
            const response = await fetch(apiUrl, {
                method: "PUT"
            });
            if (response.ok) {
                console.log(`${action.toUpperCase()} eseguito con successo`);
            } else {
                const errorText = await response.text();
                console.error(`Errore API: ${errorText}`);
            }
        } catch (error) {
            console.error("Errore nella richiesta API PLAY/PAUSE: ", error);
        }
    }

    async function skipTrack(action) {
        const apiUrl = `/client/spotify/post/${action}`;
        try {
            const response = await fetch(apiUrl, {
                method: "POST"
            });
            if (response.ok) {
                console.log(`${action.toUpperCase()} eseguito con successo`);
                setTimeout(() => {
                    window.location.reload();
                }, 200);
            } else {
                const errorText = await response.text();
                console.error(`Errore API: ${errorText}`);
            }
        } catch (error) {
            console.error("Errore nella richiesta API NEXT/PREV: ", error);
        }
    }

    async function checkSpotifyDevices() {
        try {
            let response = await fetch("/client/spotify/get/queue", {
                method: "GET"
            });
            if (!response.ok) {
                throw new Error(`Errore: ${response.status}`);
            }
        } catch (error) {
            console.error("Errore nella richiesta API DEVICES:", error);
        }
    }

    playButton.addEventListener("click", () => controlSpotify("play"));
    pauseButton.addEventListener("click", () => controlSpotify("pause"));
    restartButton.addEventListener("click", () => controlSpotify("restart"));
    prevButton.addEventListener("click", () => skipTrack("previous"));
    nextButton.addEventListener("click", () => skipTrack("next"));

    setTimeout(() => {
        checkSpotifyDevices();
    }, 500);
});
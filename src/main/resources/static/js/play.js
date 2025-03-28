document.addEventListener("DOMContentLoaded", function () {
    const playButton = document.getElementById("playBtn");
    const pauseButton = document.getElementById("pauseBtn");
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
                window.location.reload();
            } else {
                const errorText = await response.text();
                console.error(`Errore API: ${errorText}`);
            }
        } catch (error) {
            console.error("Errore nella richiesta API NEXT/PREV: ", error);
        }
    }

    playButton.addEventListener("click", () => controlSpotify("play"));
    pauseButton.addEventListener("click", () => controlSpotify("pause"));
    prevButton.addEventListener("click", () => skipTrack("previous"));
    nextButton.addEventListener("click", () => skipTrack("next"));
});
document.addEventListener("DOMContentLoaded", function () {

    const playButton = document.getElementById("playBtn");
    const pauseButton = document.getElementById("pauseBtn");
    const restartButton = document.getElementById("restartBtn");
    const prevButton = document.getElementById("prevBtn");
    const nextButton = document.getElementById("nextBtn");

    let reloadTimer = null;
    let totalDuration = null;
    let startTime = null;
    let remainingTime = null;

    function startReloadTimer(duration) {
        totalDuration = duration;
        startTime = Date.now();
        reloadTimer = setTimeout(() => {
            window.location.reload();
        }, duration);
        console.log(`Timer avviato per ${duration} ms`);
    }

    function pauseReloadTimer() {
        if (reloadTimer !== null) {
            clearTimeout(reloadTimer);
            reloadTimer = null;
            const elapsed = Date.now() - startTime;
            remainingTime = totalDuration - elapsed;
            console.log(`Timer messo in pausa; tempo rimanente: ${remainingTime} ms`);
        }
    }

    function resumeReloadTimer() {
        if (reloadTimer === null && remainingTime > 0) {
            startTime = Date.now();
            reloadTimer = setTimeout(() => {
                window.location.reload();
            }, remainingTime);
            console.log(`Timer ripreso; tempo rimanente: ${remainingTime} ms`);
        }
    }

    function restartReloadTimer() {
        if (reloadTimer !== null) {
            clearTimeout(reloadTimer);
        }
        if (totalDuration !== null) {
            remainingTime = totalDuration;
            startReloadTimer(totalDuration);
            console.log(`Timer resettato alla durata totale: ${totalDuration} ms`);
        }
    }

    const playContainer = document.getElementById("play-container");
    if (playContainer && window.location.pathname === "/play") {
        const durationMs = parseInt(playContainer.dataset.trackDuration, 10);
        if (!isNaN(durationMs) && durationMs > 0) {
            startReloadTimer(durationMs);
        }
    }

    async function controlSpotify(action) {
        const apiUrl = `/client/spotify/put/${action}`;
        try {
            const response = await fetch(apiUrl, { method: "PUT" });
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
            const response = await fetch(apiUrl, { method: "POST" });
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
            let response = await fetch("/client/spotify/get/queue", { method: "GET" });
            if (!response.ok) {
                throw new Error(`Errore: ${response.status}`);
            }
        } catch (error) {
            console.error("Errore nella richiesta API DEVICES:", error);
        }
    }

    playButton.addEventListener("click", function () {
        resumeReloadTimer();
        controlSpotify("play");
    });
    pauseButton.addEventListener("click", function () {
        pauseReloadTimer();
        controlSpotify("pause");
    });
    restartButton.addEventListener("click", function () {
        restartReloadTimer();
        controlSpotify("restart");
    });
    prevButton.addEventListener("click", () => skipTrack("previous"));
    nextButton.addEventListener("click", () => skipTrack("next"));

    setTimeout(() => {
        checkSpotifyDevices();
    }, 500);

});
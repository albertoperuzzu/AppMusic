document.addEventListener("DOMContentLoaded", function () {
    const resultDiv = document.getElementById("result");
    const callAIButton = document.getElementById("callAI");
    const buttonContainer = callAIButton.parentNode;
    const observer = new MutationObserver((mutationsList, observerInstance) => {
        if (resultDiv.innerHTML.trim() !== "") {
            observerInstance.disconnect();
            const listenButton = document.createElement("button");
            listenButton.className = "spotify-btn";
            listenButton.id = "ascoltaBtn";
            listenButton.innerHTML = `<i class="fas fa-play"></i> <span class="btn-text">Ascolta</span>`;
            buttonContainer.innerHTML = "";
            buttonContainer.appendChild(listenButton);

            listenButton.addEventListener("click", function () {
                fetch("/client/google/get_speech", { method: "POST" })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error("Errore nella chiamata all'API TTS");
                        }
                        return response.blob();
                    })
                    .then(blob => {
                        const audioUrl = URL.createObjectURL(blob);
                        let audioPlayer = document.getElementById("audioPlayer");
                        if (!audioPlayer) {
                            audioPlayer = document.createElement("audio");
                            audioPlayer.id = "audioPlayer";
                            audioPlayer.controls = true;
                            const translationContainer = document.getElementById("translation-container");
                            translationContainer.appendChild(audioPlayer);
                        }
                        audioPlayer.src = audioUrl;
                        audioPlayer.play();
                    })
                    .catch(error => {
                        console.error("Errore nella riproduzione dell'audio:", error);
                    });
            });
        }
    });

    observer.observe(resultDiv, { childList: true, subtree: true });
});
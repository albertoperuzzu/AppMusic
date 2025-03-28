async function checkSpotifyDevices() {
    try {
        let response = await fetch("/client/spotify/get/devices", {
            method: "GET"
        });

        if (!response.ok) {
            throw new Error(`Errore: ${response.status}`);
        }

        let data = await response.json();

        if (data.devices && data.devices.length > 0) {
            console.log("Dispositivi trovati");
        } else {
            console.log("Nessun dispositivo, apro app");
            setTimeout(() => {
                window.location.href = "spotify://";
            }, 200);
        }
    } catch (error) {
        console.error("Errore nella richiesta API DEVICES:", error);
    }
}

document.addEventListener("DOMContentLoaded", function () {
    checkSpotifyDevices();
});
// Funzione per aprire la modale
function openModal() {
    document.getElementById("loginModal").style.display = "block";
}

// Funzione per chiudere la modale
function closeModal() {
    document.getElementById("loginModal").style.display = "none";
}

// Funzione che viene eseguita quando si preme il bottone "Accedi"
function loginSpotify() {
    console.log("Bottone Accedi premuto!");

     /*
    fetch('/spotify/login', {
        method: 'GET'
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error('Errore durante il login');
    })
    .then(data => {
        console.log("Risposta dal server:", data);
        alert("Login avviato! Controlla la console per i dettagli.");
    })
    .catch(error => {
        console.error("Errore:", error);
        alert("Si è verificato un errore durante il login.");
    });
    */
}
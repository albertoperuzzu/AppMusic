document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    const resultsSelect = document.getElementById('resultsSelect');
    const searchContainer = document.getElementById('search-container');
    let timeout = null;


    searchInput.addEventListener('input', function() {
        const query = searchInput.value.trim();
        if (query.length < 3) {
            resultsSelect.innerHTML = "";
            resultsSelect.style.display = "none";
            return;
        }
        if (timeout) clearTimeout(timeout);
        timeout = setTimeout(() => {
            fetch('/client/spotify/search/ask?query=' + encodeURIComponent(query))
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Errore nella chiamata");
                    }
                    return response.json();
                })
                .then(data => {
                    resultsSelect.innerHTML = "";
                    data.tracks.items.forEach(item => {
                        const trackName = item.name;
                        const artistName = item.artists && item.artists.length > 0 ? item.artists[0].name : 'Sconosciuto';
                        const trackUri = item.uri;
                        const option = document.createElement('option');
                        option.value = trackUri;
                        option.textContent = `${trackName} - ${artistName}`;
                        resultsSelect.appendChild(option);
                    });
                    if (resultsSelect.options.length > 0) {
                        resultsSelect.style.display = "block";
                    } else {
                        resultsSelect.style.display = "none";
                    }
                })
                .catch(error => {
                    console.error("Errore durante la ricerca:", error);
                });
        }, 300);
    });


    document.addEventListener('click', function(event) {
        if (!searchContainer.contains(event.target)) {
            resultsSelect.innerHTML = "";
            resultsSelect.style.display = "none";
        }
    });


    resultsSelect.addEventListener('dblclick', function() {
        const selectedSong = resultsSelect.value;
        console.log('Selected song:', selectedSong);
        console.log('Encoded song:', encodeURIComponent(selectedSong));
        fetch('/client/spotify/search/play?query=' + encodeURIComponent(selectedSong))
            .then(response => {
                if (!response.ok) {
                    throw new Error("Errore durante la chiamata a play");
                }
                return response.json();
            })
            .then(data => {
                console.log("Chiamata play eseguita con successo:", data);
                setTimeout(() => {
                    const currentPath = window.location.pathname;
                    if (currentPath === "/podcast") {
                        window.location.href = "/play";
                    } else {
                        window.location.reload();
                    }
                }, 700);
            })
            .catch(error => {
                console.error("Errore durante la chiamata play:", error);
            });
    });

});
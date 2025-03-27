package com.music.project.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class GeniusScraper {

    public static String scrapeLyrics(String url) {
        try {
            // Scarica il documento e simula un browser reale
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/100.0.4896.75 Safari/537.36")
                    .get();

            // Seleziona il contenitore delle liriche (HTML)
            Elements lyricsElements = doc.select("div[data-lyrics-container='true']");
            lyricsElements.select("a").unwrap();
            lyricsElements.select("span").unwrap();
            lyricsElements.select("i").unwrap();
            StringBuilder rawHtml = new StringBuilder();
            for (Element e : lyricsElements) {
                rawHtml.append(e.html());
            }
            String html = rawHtml.toString().trim();

            // Sostituisci tutte le varianti di <br> con un delimitatore unico (qui "|||")
            String delimiter = "|||";
            String normalized = html.replaceAll("(?i)<br\\s*/?>", delimiter);

            // Ora dividiamo il testo per righe usando il delimitatore
            String[] lines = normalized.split("\\Q" + delimiter + "\\E");
            StringBuilder processed = new StringBuilder();

            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    // Se la riga è vuota, aggiungi un <br>
                    processed.append("<br>");
                } else if (trimmed.startsWith("[")) {
                    // Se la riga inizia con "[" (ad esempio [Chorus] o [Strofa 1]),
                    // sostituisci la riga con due <br> per dare più spazio
                    processed.append("<br><br>");
                } else {
                    // Altrimenti, aggiungi la riga seguita da un <br>
                    processed.append(trimmed).append("<br>");
                }
            }
            return processed.toString().replace("<br><br><br><br>", "<br><br>").replace("<br><br><br>", "<br><br>");
        } catch (IOException e) {
            System.err.println("Errore nel recupero del testo: " + e.getMessage());
            return null;
        }
    }

}
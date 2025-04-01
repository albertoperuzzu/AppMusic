package com.music.project.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class GeniusScraper {

    public static String scrapeLyrics(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/100.0.4896.75 Safari/537.36")
                    .get();
            doc.select("div[class^=LyricsHeader__Container-sc-3eaf69e8]").remove();
            Elements lyricsElements = doc.select("div[data-lyrics-container='true']");
            lyricsElements.select("a").unwrap();
            lyricsElements.select("span").unwrap();
            lyricsElements.select("i").unwrap();
            StringBuilder rawHtml = new StringBuilder();
            for (Element e : lyricsElements) {
                rawHtml.append(e.html());
            }
            String html = rawHtml.toString().trim();
            String delimiter = "|||";
            String normalized = html.replaceAll("(?i)<br\\s*/?>", delimiter);
            String[] lines = normalized.split("\\Q" + delimiter + "\\E");
            StringBuilder processed = new StringBuilder();
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    processed.append("<br>");
                } else if (trimmed.startsWith("[")) {
                    processed.append("<br><br>");
                } else {
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
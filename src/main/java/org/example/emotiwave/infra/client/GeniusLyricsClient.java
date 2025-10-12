package org.example.emotiwave.infra.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.emotiwave.domain.exceptions.GeniusLyricsNaoEncontrada;
import org.example.emotiwave.domain.exceptions.LetraMusicaNaoEncontradaGenius;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Component
public class GeniusLyricsClient {

    private final String secret;
    private final WebClient webClient;

    public GeniusLyricsClient(WebClient webClient) {
        this.webClient = webClient;
        this.secret = System.getenv("GENIUS_KEY");
    }

    public String fetchLyricsUrl(String artist, String title) throws IOException, InterruptedException {
        String query = title + " " + artist;
        String url = "https://api.genius.com/search?q=" + URLEncoder.encode(query, "UTF-8");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + secret)
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray hits = json.getAsJsonObject("response").getAsJsonArray("hits");

        if (hits.size() == 0) {
            throw new LetraMusicaNaoEncontradaGenius("Letra musica nao encontrada");

        }


        return hits.get(0).getAsJsonObject()
                .getAsJsonObject("result")
                .get("url").getAsString();
    }

    public String fetchLyrics(String artist, String title) throws IOException, InterruptedException {
        String url = fetchLyricsUrl(artist, title);
        if (url == null) return null;

        Document doc = Jsoup.connect(url).userAgent(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                        + "(KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36"
        ).get();

        Elements lyricsDivs = doc.select("div[data-lyrics-container=true]");

        StringBuilder lyrics = new StringBuilder();
        for (Element el : lyricsDivs) {
            lyrics.append(el.text()).append("\n");
        }

        String lyricsText = lyrics.toString();

        int start = lyricsText.indexOf("[Verse 1]");
        if (start != -1) {
            lyricsText = lyricsText.substring(start);
        }

        return lyricsText;
    }
}





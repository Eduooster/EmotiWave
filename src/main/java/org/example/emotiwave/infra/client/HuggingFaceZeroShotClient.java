package org.example.emotiwave.infra.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.emotiwave.domain.entities.AnaliseMusica;
import org.example.emotiwave.domain.entities.Musica;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.emotiwave.infra.repository.AnaliseMusicaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class HuggingFaceZeroShotClient {

    private final AnaliseMusicaRepository analiseMusicaRepository;
    private final ObjectMapper objectMapper;
    String huggingKey = System.getenv("HUGGING_KEY");
    String url = "https://api-inference.huggingface.co/models/facebook/bart-large-mnli";


    public HuggingFaceZeroShotClient(AnaliseMusicaRepository analiseMusicaRepository, ObjectMapper objectMapper) {
        this.analiseMusicaRepository = analiseMusicaRepository;
        this.objectMapper = objectMapper;
    }

    public AnaliseMusica analisarScoreMusica(Musica musica) throws IOException, InterruptedException {

        String bodyString = montarJson(musica);

        HttpResponse<String> response =  montarRequestEPegarResponse(url,huggingKey,bodyString);

        ArrayList<Serializable> resultado = parsearResponse(response);

        AnaliseMusica analise = new AnaliseMusica();
        analise.setLabel((String) resultado.get(0));
        analise.setScore((BigDecimal) resultado.get(1));
        analise.setAnalisado_em(LocalDate.now());
        analise.setMusica(musica);

        analiseMusicaRepository.save(analise);

        return analise;
    }

    private ArrayList<Serializable> parsearResponse(HttpResponse<String> response) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(response.body());
        String topLabel = null;
        BigDecimal topScore = null;

        if (root.has("labels") && root.has("scores")) {
            JsonNode labels = root.get("labels");
            JsonNode scores = root.get("scores");

            if (labels.isArray() && labels.size() > 0) {
                topLabel = labels.get(0).asText();
                double topScoreDouble = scores.get(0).asDouble();
                topScore = BigDecimal.valueOf(topScoreDouble);
                return new ArrayList<>(Arrays.asList(topLabel, topScore));
            }
        } else {
            System.err.println("Resposta inesperada da Hugging Face: " + response.body());
        }
        return null;
    }

    private String montarJson(Musica musica) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode bodyJson = mapper.createObjectNode();
        bodyJson.put("inputs", musica.getLetra());

        ObjectNode parameters = mapper.createObjectNode();
        ArrayNode candidateLabels = mapper.createArrayNode();

        candidateLabels.add("happy");
        candidateLabels.add("sad");
        candidateLabels.add("angry");
        candidateLabels.add("calm");
        candidateLabels.add("romantic");
        candidateLabels.add("love");
        candidateLabels.add("heartbreak");
        candidateLabels.add("lonely");
        candidateLabels.add("nostalgic");
        candidateLabels.add("energetic");
        candidateLabels.add("hopefull");

        parameters.set("candidate_labels", candidateLabels);
        bodyJson.set("parameters", parameters);

        return mapper.writeValueAsString(bodyJson);
    }

    private HttpResponse<String> montarRequestEPegarResponse(String url, String huggingKey, String bodyString) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + huggingKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyString, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;

    }

}

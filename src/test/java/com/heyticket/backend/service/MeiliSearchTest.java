package com.heyticket.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.module.mapper.PerformanceMapper;
import com.heyticket.backend.repository.performance.PerformanceRepository;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.exceptions.MeilisearchException;
import com.meilisearch.sdk.model.MatchingStrategy;
import com.meilisearch.sdk.model.SearchResult;
import com.meilisearch.sdk.model.Searchable;
import com.meilisearch.sdk.model.Settings;
import com.meilisearch.sdk.model.TypoTolerance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
@Disabled // MeiliSearch 학습 테스트
public class MeiliSearchTest {

    @Autowired
    private PerformanceRepository performanceRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Client client = new Client(new Config("http://localhost:7700", "masterKey"));

    @Test
    @DisplayName("MeiliSearch test")
    void meiliSearchTest() throws JSONException, MeilisearchException {
        ArrayList<JSONObject> items = new ArrayList<>();
        items.add(new JSONObject().put("id", "1").put("title", "Carol").put("genres", new JSONArray("[\"Romance\",\"Drama\"]")));
        items.add(new JSONObject().put("id", "2").put("title", "Wonder Woman").put("genres", new JSONArray("[\"Action\",\"Adventure\"]")));
        items.add(new JSONObject().put("id", "3").put("title", "Life of Pi").put("genres", new JSONArray("[\"Adventure\",\"Drama\"]")));
        items.add(new JSONObject().put("id", "4").put("title", "Mad Max: Fury Road").put("genres", new JSONArray("[\"Adventure\",\"Science Fiction\"]")));
        items.add(new JSONObject().put("id", "5").put("title", "Moana").put("genres", new JSONArray("[\"Fantasy\",\"Action\"]")));
        items.add(new JSONObject().put("id", "6").put("title", "Philadelphia").put("genres", new JSONArray("[\"Drama\"]")));

        JSONArray array = new JSONArray(items);
        String documents = array.toString();

        Index index = client.index("performance");
        index.addDocuments(documents);
    }

    @Test
    @DisplayName("Basic search")
    void basicSearch() throws MeilisearchException {
        Index index = client.index("movies");
        SearchResult results = index.search("carlo");
        System.out.println(results);
    }

    @Test
    @DisplayName("Add performances")
    void addDocuments() throws MeilisearchException {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        List<Performance> performances = performanceRepository.findAll();

        for (Performance performance : performances) {
            PerformanceResponse performanceResponse = PerformanceMapper.INSTANCE.toPerformanceDto(performance);
            JsonNode jsonNode = objectMapper.valueToTree(performanceResponse);
            arrayNode.add(jsonNode);
        }

        client.createIndex("performance", "id");
        Index index = client.index("performance");

        TypoTolerance typoTolerance = new TypoTolerance();
        typoTolerance.setEnabled(false);

        Settings settings = new Settings();
        settings.setSearchableAttributes(new String[]{"title", "crew"});
        settings.setTypoTolerance(typoTolerance);
        index.updateSettings(settings);
        index.addDocuments(arrayNode.toString());
    }

    @Test
    @DisplayName("Search performances")
    void searchDocuments() throws MeilisearchException {
        Index index = client.index("performance");

        TypoTolerance typoTolerance = new TypoTolerance();
        typoTolerance.setEnabled(false);

        Settings settings = new Settings();
        settings.setSearchableAttributes(new String[]{"title", "cast"});
        settings.setTypoTolerance(typoTolerance);
        index.updateSettings(settings);

        SearchRequest request = SearchRequest.builder()
            .q("시카")
            .matchingStrategy(MatchingStrategy.ALL)
            .build();

        Searchable results = index.search(request);
        ArrayList<HashMap<String, Object>> hits = results.getHits();
        System.out.println("results = " + hits);
    }

    @Test
    @DisplayName("delete index")
    void deleteIdx() throws MeilisearchException {
        client.deleteIndex("performance");
    }
}

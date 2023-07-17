package com.heyticket.backend.module.meilesearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.exceptions.MeilisearchException;
import com.meilisearch.sdk.model.MatchingStrategy;
import com.meilisearch.sdk.model.Searchable;
import com.meilisearch.sdk.model.Settings;
import com.meilisearch.sdk.model.TypoTolerance;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MeiliSearchService {

    private final Client meiliClient;

    private final ObjectMapper objectMapper;

    public MeiliSearchService(@Value("${meili.url:url}") String url, @Value("${meili.key:key}") String key, ObjectMapper objectMapper) {
        this.meiliClient = new Client(new Config(url, key));
        this.objectMapper = objectMapper;
    }

    public void addPerformance(List<PerformanceResponse> performanceResponses) {
        try {
            meiliClient.createIndex("performance", "id");
            Index index = meiliClient.index("performance");

            TypoTolerance typoTolerance = new TypoTolerance();
            typoTolerance.setEnabled(false);

            Settings settings = new Settings();
            settings.setSearchableAttributes(new String[]{"title", "cast"});
            settings.setTypoTolerance(typoTolerance);
            index.updateSettings(settings);

            ArrayNode arrayNode = objectMapper.createArrayNode();
            for (PerformanceResponse performanceResponse : performanceResponses) {
                JsonNode jsonNode = objectMapper.valueToTree(performanceResponse);
                arrayNode.add(jsonNode);
            }

            index.addDocuments(arrayNode.toString());
        } catch (MeilisearchException e) {
            throw new IllegalStateException("Failure to save doc in meili");
        }
    }

    public void searchPerformance(String query) {
        try {
            Index index = meiliClient.index("performance");
            TypoTolerance typoTolerance = new TypoTolerance();
            typoTolerance.setEnabled(false);

            Settings settings = new Settings();
            settings.setSearchableAttributes(new String[]{"title", "cast"});
            settings.setTypoTolerance(typoTolerance);
            index.updateSettings(settings);

            SearchRequest request = SearchRequest.builder()
                .q(query)
                .matchingStrategy(MatchingStrategy.ALL)
                .build();

            Searchable results = index.search(request);
        } catch (MeilisearchException e) {
            e.printStackTrace();
        }
    }
}

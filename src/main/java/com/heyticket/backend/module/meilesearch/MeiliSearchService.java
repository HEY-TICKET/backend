package com.heyticket.backend.module.meilesearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.heyticket.backend.module.meilesearch.dto.MeiliPerformanceDocument;
import com.heyticket.backend.module.meilesearch.dto.MeiliSearchRequest;
import com.heyticket.backend.module.meilesearch.dto.MeiliSearchResponse;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.exceptions.MeilisearchException;
import com.meilisearch.sdk.model.Settings;
import com.meilisearch.sdk.model.TypoTolerance;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MeiliSearchService {

    private final Client meiliClient;

    private final ObjectMapper objectMapper;

    private final MeiliSearchFeignClient feignClient;

    public MeiliSearchService(@Value("${meili.url:url}") String url, @Value("${meili.key:key}") String key, ObjectMapper objectMapper, MeiliSearchFeignClient feignClient) {
        this.meiliClient = new Client(new Config(url, key));
        this.objectMapper = objectMapper;
        this.feignClient = feignClient;
    }

    public void addPerformance(List<MeiliPerformanceDocument> meiliPerformanceDocuments) {
        try {
            meiliClient.createIndex("performance", "id");
            Index index = meiliClient.index("performance");

            TypoTolerance typoTolerance = new TypoTolerance();
            typoTolerance.setEnabled(false);

            Settings settings = new Settings();
            settings.setSearchableAttributes(new String[]{"title", "cast"});
            settings.setSortableAttributes(new String[]{"status"});
            settings.setRankingRules(new String[]{
                "sort",
                "exactness",
                "proximity",
                "attribute"
            });
            settings.setTypoTolerance(typoTolerance);
            index.updateSettings(settings);

            ArrayNode arrayNode = objectMapper.createArrayNode();
            for (MeiliPerformanceDocument meiliPerformanceDocument : meiliPerformanceDocuments) {
                JsonNode jsonNode = objectMapper.valueToTree(meiliPerformanceDocument);
                arrayNode.add(jsonNode);
            }

            index.addDocuments(arrayNode.toString());
        } catch (MeilisearchException e) {
            throw new IllegalStateException("Failure to save doc in meili");
        }
    }

    public MeiliSearchResponse searchPerformance(String query, int page, int pageSize) {
        MeiliSearchRequest request = MeiliSearchRequest.builder()
            .q(query)
            .page(page)
            .hitsPerPage(pageSize)
            .attributesToHighlight(List.of("title", "cast"))
            .sort(List.of("status:asc"))
            .build();

        return feignClient.searchPerformance(request);
    }
}

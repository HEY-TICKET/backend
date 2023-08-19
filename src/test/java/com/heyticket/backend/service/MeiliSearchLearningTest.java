package com.heyticket.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.module.mapper.PerformanceMapper;
import com.heyticket.backend.repository.performance.PerformanceRepository;
import com.heyticket.backend.service.dto.pagable.PageResponse;
import com.heyticket.backend.service.dto.request.PerformanceSearchRequest;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.exceptions.MeilisearchException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
@Disabled // MeiliSearch 학습 테스트
public class MeiliSearchLearningTest {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private PerformanceService performanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${meili.url}")
    private String url;

    @Value("${meili.key}")
    private String key;

    private Client client;

    @BeforeEach
    void init() {
        client = new Client(new Config(url, key));
    }

    @Test
    @DisplayName("MeiliSearch - 샘플 document add 테스트")
    void meiliSampleDocAdd() throws JSONException, MeilisearchException {
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
    @DisplayName("MeiliSearch - query로만 검색")
    void basicSearch() throws MeilisearchException {
        Index index = client.index("movies");
        SearchResult results = index.search("carlo");
        System.out.println(results);
    }

    @Test
    @DisplayName("MeiliSearch - typoTolerance, rankingRule setting 테스트")
    void typoSetting() throws MeilisearchException {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        List<Performance> performances = performanceRepository.findAll();

        for (Performance performance : performances) {
            PerformanceResponse performanceResponse = PerformanceMapper.INSTANCE.toPerformanceDto(performance);
            ObjectNode objectNode = objectMapper.valueToTree(performanceResponse);
            arrayNode.add(objectNode);
        }

        client.createIndex("performance", "id");
        Index index = client.index("performance");

        TypoTolerance typoTolerance = new TypoTolerance();
        typoTolerance.setDisableOnAttributes(new String[]{"cast"});
        typoTolerance.setMinWordSizeForTypos(
            new HashMap<>() {
                {
                    put("oneTypo", 8);
                }
            });
        typoTolerance.setEnabled(true);

        Settings settings = new Settings();
        settings.setTypoTolerance(typoTolerance);
        settings.setRankingRules(new String[]{
            "exactness",
            "proximity",
            "typo",
            "attribute",
            "sort"
        });
        settings.setSearchableAttributes(new String[]{"title", "cast"});
        settings.setSortableAttributes(new String[]{"status"});
        settings.setTypoTolerance(typoTolerance);
        index.updateSettings(settings);
        index.addDocuments(arrayNode.toString());
    }

    @Test
    @DisplayName("MeiliSearch - SearchRequest highlight 필드 테스트")
    void searchDocuments() throws MeilisearchException {
        Index index = client.index("performance");

        Settings settings = new Settings();
        settings.setSearchableAttributes(new String[]{"title", "cast"});

        index.updateSettings(settings);

        SearchRequest request = SearchRequest.builder()
            .q("시카")
            .highlightPreTag("<em>")
            .highlightPostTag("</em>")
            .attributesToHighlight(new String[]{"title", "cast"})
            .build();

        System.out.println("request.toString() = " + request.toString());

        Searchable results = index.search(request);
        ArrayList<HashMap<String, Object>> hits = results.getHits();
        System.out.println("hits = " + hits);
    }

    @Test
    @DisplayName("MeiliSearch - index 삭제")
    void deleteIdx() throws MeilisearchException {
        client.deleteIndex("performance");
    }

    @Test
    @DisplayName("MeiliSearch -get ranking rule")
    void getRankingRule() throws MeilisearchException {
        Index index = client.index("performance");
        String[] rankingRulesSettings = index.getRankingRulesSettings();
        for (String rankingRulesSetting : rankingRulesSettings) {
            System.out.println("rankingRulesSetting = " + rankingRulesSetting);
        }
        TypoTolerance typoToleranceSettings = index.getTypoToleranceSettings();
        boolean enabled = typoToleranceSettings.isEnabled();
        System.out.println("enabled = " + enabled);
    }

    @Test
    @DisplayName("MeiliSearch - set ranking rule")
    void setRankingRule() throws MeilisearchException {
        Index index = client.index("performance");

        TypoTolerance typoTolerance = new TypoTolerance();
        typoTolerance.setDisableOnAttributes(new String[]{"cast"});
        typoTolerance.setMinWordSizeForTypos(
            new HashMap<>() {
                {
                    put("oneTypo", 8);
                }
            });
        typoTolerance.setEnabled(false);

        Settings settings = new Settings();
        settings.setSearchableAttributes(new String[]{"title", "cast"});
        settings.setTypoTolerance(typoTolerance);
        settings.setRankingRules(new String[]{
            "exactness",
            "proximity",
            "typo",
            "attribute",
            "sort"
        });

        index.updateSettings(settings);
    }

    @Test
    @DisplayName("MeiliSearch - get searchable attribute")
    void getSearchableAttribute() throws MeilisearchException {
        Index index = client.index("performance");
        String[] searchableAttributes = index.getSettings().getSearchableAttributes();
        for (String searchableAttribute : searchableAttributes) {
            System.out.println("searchableAttribute = " + searchableAttribute);
        }
    }

    @Test
    void addPerformance() {
        performanceService.updatePerformanceMeiliData();
    }

    @Test
    void search() {
        PerformanceSearchRequest request = PerformanceSearchRequest.builder()
            .query("시카")
            .build();
        PageResponse<PerformanceResponse> performanceResponsePageResponse = performanceService.searchPerformances(request, PageRequest.of(1, 10));
        List<PerformanceResponse> contents = performanceResponsePageResponse.getContents();
        System.out.println("contents.size() = " + contents.size());
    }
}

package com.heyticket.backend.module.meilesearch;

import com.heyticket.backend.config.FeignConfig;
import com.heyticket.backend.module.meilesearch.dto.MeiliSearchSearchRequest;
import com.heyticket.backend.module.meilesearch.dto.MeiliSearchSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "meiliSearchFeignClient", url = "${meili.url:url}", configuration = FeignConfig.class)
public interface MeiliSearchFeignClient {

    @PostMapping(value = "/indexes/performance/search", produces = "application/xml;charset=UTF-8")
    MeiliSearchSearchResponse searchPerformance(@RequestBody MeiliSearchSearchRequest request);
}

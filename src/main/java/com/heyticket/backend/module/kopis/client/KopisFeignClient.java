package com.heyticket.backend.module.kopis.client;

import com.heyticket.backend.config.FeignConfiguration;
import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeRequest;
import com.heyticket.backend.module.kopis.client.dto.BoxOfficeResponse;
import com.heyticket.backend.module.kopis.client.dto.PerformanceDetailResponse;
import com.heyticket.backend.module.kopis.client.dto.PerformanceRequest;
import com.heyticket.backend.module.kopis.client.dto.PerformanceResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kopisFeignClient", url = "${kopis.performance.url}", configuration = FeignConfiguration.class)
public interface KopisFeignClient {

    @GetMapping(value = "/pblprfr", produces = "application/xml;charset=UTF-8")
    List<PerformanceResponse> getPerformances(@SpringQueryMap PerformanceRequest request, @RequestParam("service") String apiKey);

    @GetMapping(value = "/boxoffice", produces = "application/xml;charset=UTF-8")
    List<BoxOfficeResponse> getBoxOffice(@SpringQueryMap KopisBoxOfficeRequest request, @RequestParam("service") String apiKey);

    @GetMapping(value = "/pblprfr/{mt20id}", produces = "application/xml;charset=UTF-8")
    List<PerformanceDetailResponse> getPerformanceDetail(@PathVariable("mt20id") String performanceId, @RequestParam("service") String apiKey);

    //    @GetMapping(value = "/pblprfr?prfstate=02", produces = "application/xml;charset=UTF-8")
//    List<KopisPerformanceResponse> getOnGoingPerformances(@SpringQueryMap KopisPerformanceRequest request);

//    @GetMapping(value = "/prfplc/{mt10id}", produces = "application/xml;charset=UTF-8")
//    List<KopisPlaceResponse> getPlace(@PathVariable("mt10id") final String mt10id, @RequestParam("service") final String service);

}

package com.heyticket.backend.module.kopis.client;

import com.heyticket.backend.config.FeignConfig;
import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeRequest;
import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisPerformanceDetailResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisPerformanceRequest;
import com.heyticket.backend.module.kopis.client.dto.KopisPerformanceResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisPlaceDetailResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisPlaceRequest;
import com.heyticket.backend.module.kopis.client.dto.KopisPlaceResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kopisFeignClient", url = "${kopis.performance.url}", configuration = FeignConfig.class)
public interface KopisFeignClient {

    @GetMapping(value = "/pblprfr", produces = "application/xml;charset=UTF-8")
    List<KopisPerformanceResponse> getPerformances(@SpringQueryMap KopisPerformanceRequest request, @RequestParam("service") String apiKey);

    @GetMapping(value = "/boxoffice", produces = "application/xml;charset=UTF-8")
    List<KopisBoxOfficeResponse> getBoxOffice(@SpringQueryMap KopisBoxOfficeRequest request, @RequestParam("service") String apiKey);

    @GetMapping(value = "/pblprfr/{performanceId}", produces = "application/xml;charset=UTF-8")
    List<KopisPerformanceDetailResponse> getPerformanceDetail(@PathVariable("performanceId") String performanceId, @RequestParam("service") String apiKey);

    @GetMapping(value = "/prfplc", produces = "application/xml;charset=UTF-8")
    List<KopisPlaceResponse> getPlaces(@SpringQueryMap KopisPlaceRequest request, @RequestParam("service") String apiKey);

    @GetMapping(value = "/prfplc/{placeId}", produces = "application/xml;charset=UTF-8")
    List<KopisPlaceDetailResponse> getPlaceDetail(@PathVariable("placeId") String placeId, @RequestParam("service") String apiKey);

    //    @GetMapping(value = "/pblprfr?prfstate=02", produces = "application/xml;charset=UTF-8")
//    List<KopisPerformanceResponse> getOnGoingPerformances(@SpringQueryMap KopisPerformanceRequest request);

//    @GetMapping(value = "/prfplc/{mt10id}", produces = "application/xml;charset=UTF-8")
//    List<KopisPlaceResponse> getPlace(@PathVariable("mt10id") final String mt10id, @RequestParam("service") final String service);

}

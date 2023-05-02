package com.heyticket.backend.performances.client;

import com.heyticket.backend.config.FeignConfiguration;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "KopisFeignClient", url = "${kopis.performance.url}", configuration = FeignConfiguration.class)
public interface KopisFeignClient {

    @GetMapping(value = "/pblprfr", produces = "application/xml;charset=UTF-8")
    List<PerformanceResponse> getPerformances(@SpringQueryMap KopisPerformanceRequest request);

//    @GetMapping(value = "/pblprfr?prfstate=02", produces = "application/xml;charset=UTF-8")
//    List<KopisPerformanceResponse> getOnGoingPerformances(@SpringQueryMap KopisPerformanceRequest request);

//    @GetMapping(value = "/pblprfr/{mt20id}", produces = "application/xml;charset=UTF-8")
//    List<KopisDetailPerformanceResponse> getPerformance(@PathVariable("mt20id") final String mt20id, @RequestParam("service") final String service);

//    @GetMapping(value = "/prfplc/{mt10id}", produces = "application/xml;charset=UTF-8")
//    List<KopisPlaceResponse> getPlace(@PathVariable("mt10id") final String mt10id, @RequestParam("service") final String service);

}

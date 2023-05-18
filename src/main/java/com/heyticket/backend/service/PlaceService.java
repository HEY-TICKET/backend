package com.heyticket.backend.service;

import com.heyticket.backend.domain.Place;
import com.heyticket.backend.module.kopis.client.dto.KopisPlaceDetailResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisPlaceRequest;
import com.heyticket.backend.module.kopis.client.dto.KopisPlaceResponse;
import com.heyticket.backend.module.kopis.service.KopisService;
import com.heyticket.backend.repository.PlaceRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlaceService {

    private final PlaceRepository placeRepository;

    private final KopisService kopisService;

    public List<KopisPlaceResponse> updatePlace() {
        KopisPlaceRequest kopisPlaceRequest = KopisPlaceRequest.builder()
            .cpage(1)
            .rows(5000)
            .build();
        List<KopisPlaceResponse> placeResponseList = kopisService.getPlaces(kopisPlaceRequest);

        List<String> allIdList = placeRepository.findAllIds();
        HashSet<String> allIdSet = new HashSet<>(allIdList);
        ArrayList<Place> newPlaceList = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(20);

        for (KopisPlaceResponse placeResponse : placeResponseList) {
            String placeId = placeResponse.mt10id();
            if (!allIdSet.contains(placeId)) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    KopisPlaceDetailResponse placeDetailResponse = kopisService.getPlaceDetail(placeId);
                    Place place = placeDetailResponse.toEntity();
                    place.updateSidoGugun(placeResponse.sidonm(), placeResponse.gugunnm());
                    newPlaceList.add(place);
                }, executorService);
                futures.add(future);
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        placeRepository.saveAll(newPlaceList);

        log.info("Place information update. size : {}", newPlaceList.size());

        return placeResponseList;
    }

}

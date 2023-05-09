package com.heyticket.backend.service;

import com.heyticket.backend.domain.Place;
import com.heyticket.backend.module.kopis.client.dto.KopisPlaceDetailResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisPlaceRequest;
import com.heyticket.backend.module.kopis.client.dto.KopisPlaceResponse;
import com.heyticket.backend.repository.PlaceRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
            .rows(30)
            .build();
        List<KopisPlaceResponse> placeResponseList = kopisService.getPlaces(kopisPlaceRequest);

        List<String> allIdList = placeRepository.findAllIds();
        HashSet<String> allIdSet = new HashSet<>(allIdList);

        ArrayList<Place> newPlaceList = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            String placeId = placeResponseList.get(i).mt10id();
            if (!allIdSet.contains(placeId)) {
                KopisPlaceDetailResponse placeDetailResponse = kopisService.getPlaceDetail(placeId);
                Place place = placeDetailResponse.toEntity();
                newPlaceList.add(place);
            }
        }

        placeRepository.saveAll(newPlaceList);

        return placeResponseList;
    }

}

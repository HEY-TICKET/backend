package com.heyticket.backend.service;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.domain.Place;
import com.heyticket.backend.module.mapper.PerformanceMapper;
import com.heyticket.backend.repository.MemberLikeRepository;
import com.heyticket.backend.service.dto.pagable.PageResponse;
import com.heyticket.backend.service.dto.request.MemberLikeListRequest;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberLikeService {

    private final MemberLikeRepository memberLikeRepository;

    public PageResponse<PerformanceResponse> getMemberLikedPerformances(MemberLikeListRequest request, Pageable pageable) {
        Page<Performance> memberLikePerformanceResponse = memberLikeRepository.findMemberLikePerformanceByMemberEmail(request, pageable);
        List<Performance> memberLikePerformances = memberLikePerformanceResponse.getContent();
        List<PerformanceResponse> performanceResponses = memberLikePerformances.stream()
            .map(this::getPerformanceResponse)
            .collect(Collectors.toList());

        return new PageResponse<>(performanceResponses, pageable.getPageNumber() + 1, memberLikePerformanceResponse.getNumberOfElements(), memberLikePerformanceResponse.getTotalPages());
    }

    private PerformanceResponse getPerformanceResponse(Performance performance) {
        PerformanceResponse performanceResponse = PerformanceMapper.INSTANCE.toPerformanceDto(performance);
        performanceResponse.updateStoryUrls(performance.getStoryUrls());

        Place place = performance.getPlace();
        if (!ObjectUtils.isEmpty(place)) {
            performanceResponse.updateLocation(place.getLatitude(), place.getLongitude());
            performanceResponse.setAddress(place.getAddress());
            performanceResponse.setPhoneNumber(place.getPhoneNumber());
            performanceResponse.setPlaceId(place.getId());
            performanceResponse.setSido(place.getArea().getName());
            performanceResponse.setGugun(place.getGugunName());
        }
        return performanceResponse;
    }
}

package com.heyticket.backend.service;

import com.heyticket.backend.domain.Member;
import com.heyticket.backend.domain.MemberLike;
import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.domain.Place;
import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.exception.NotFoundException;
import com.heyticket.backend.module.mapper.PerformanceMapper;
import com.heyticket.backend.repository.member.MemberLikeRepository;
import com.heyticket.backend.repository.member.MemberRepository;
import com.heyticket.backend.repository.performance.PerformanceRepository;
import com.heyticket.backend.service.dto.pagable.PageResponse;
import com.heyticket.backend.service.dto.request.MemberLikeListRequest;
import com.heyticket.backend.service.dto.request.MemberLikeSaveRequest;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
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

    private final MemberRepository memberRepository;

    private final PerformanceRepository performanceRepository;

    public void hitLike(MemberLikeSaveRequest request) {
        Member member = getMemberFromDb(request.getEmail());
        String performanceId = request.getPerformanceId();
        Performance performance = performanceRepository.findById(performanceId)
            .orElseThrow(() -> new NotFoundException("No such performance.", InternalCode.NOT_FOUND));
        Optional<MemberLike> optionalMemberLike = memberLikeRepository.findMemberLikeByMemberAndPerformance(member, performance);
        if (optionalMemberLike.isPresent()) {
            return;
        }
        MemberLike memberLike = MemberLike.of(member, performance);
        memberLikeRepository.save(memberLike);
    }

    public void cancelLike(MemberLikeSaveRequest request) {
        Member member = getMemberFromDb(request.getEmail());
        String performanceId = request.getPerformanceId();
        Performance performance = performanceRepository.findById(performanceId)
            .orElseThrow(() -> new NotFoundException("No such performance.", InternalCode.NOT_FOUND));
        Optional<MemberLike> optionalMemberLike = memberLikeRepository.findMemberLikeByMemberAndPerformance(member, performance);
        if (optionalMemberLike.isEmpty()) {
            return;
        }
        memberLikeRepository.deleteByMemberAndPerformance(member, performance);
    }

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

    private Member getMemberFromDb(String email) {
        return memberRepository.findById(email)
            .orElseThrow(() -> new NotFoundException("No such member.", InternalCode.NOT_FOUND));
    }
}

package site.campingon.campingon.camp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.campingon.campingon.camp.dto.CampDetailResponseDto;
import site.campingon.campingon.camp.dto.CampListResponseDto;
import site.campingon.campingon.camp.dto.CampSiteListResponseDto;
import site.campingon.campingon.camp.entity.Camp;
import site.campingon.campingon.camp.entity.CampSite;
import site.campingon.campingon.camp.mapper.CampMapper;
import site.campingon.campingon.camp.repository.CampRepository;
import site.campingon.campingon.camp.repository.CampSiteRepository;
import site.campingon.campingon.like.repository.LikeRepository;
import site.campingon.campingon.user.repository.UserKeywordRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampService {

    private final CampRepository campRepository;
    private final CampSiteRepository campSiteRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final LikeRepository likeRepository;
    private final CampMapper campMapper;

    // 추천 캠핑장 조회
    public List<CampListResponseDto> getRecommendedCampsByKeywords(Long userId, int size) {
        List<String> userKeywords = userKeywordRepository.findKeywordsByUserId(userId);

        if (userKeywords.isEmpty()) {
            return List.of();
        }

        return campRepository.findRecommendedCampsByKeywords(userKeywords, PageRequest.of(0, size))
                .stream()
                .map(camp -> {
                    CampListResponseDto dto = campMapper.toCampListDto(camp);
                    dto.setLike(likeRepository.existsByCampIdAndUserId(camp.getId(), userId));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 인기 캠핑장 조회 - recommendCnt에 따른
    public Page<CampListResponseDto> getPopularCamps(Long userId, Pageable pageable) {
        Page<Camp> camps = campRepository.findPopularCamps(pageable);

        List<CampListResponseDto> campDtos = camps.getContent().stream()
                .map(camp -> {
                    CampListResponseDto dto = campMapper.toCampListDto(camp);
                    dto.setLike(likeRepository.existsByCampIdAndUserId(camp.getId(), userId));
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(campDtos, pageable, camps.getTotalElements());
    }

    // 캠핑장 상세 조회
    public CampDetailResponseDto getCampDetail(Long campId) {
        Camp camp = campRepository.findById(campId)
                .orElseThrow(() -> new RuntimeException("캠핑장을 찾을 수 없습니다."));

        return campMapper.toCampDetailDto(camp);
    }

    // 캠핑장의 캠핑지 목록 조회
    public List<CampSiteListResponseDto> getCampSites(Long campId) {
        List<CampSite> campSites = campSiteRepository.findByCampId(campId);

        return campSites.stream()
                .map(campMapper::toCampSiteListDto)
                .collect(Collectors.toList());
    }

}
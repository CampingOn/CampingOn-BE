package site.campingon.campingon.camp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.campingon.campingon.camp.dto.*;
import site.campingon.campingon.camp.entity.*;
import site.campingon.campingon.camp.mapper.CampMapper;
import site.campingon.campingon.camp.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampService {

    private final CampRepository campRepository;
    private final CampAddrRepository campAddrRepository;
    private final CampKeywordRepository campKeywordRepository;
    private final CampImageRepository campImageRepository;
    private final CampInfoRepository campInfoRepository;
    private final CampSiteRepository campSiteRepository;
    private final CampMapper campMapper;

    // 사용자 맞춤 추천 캠핑장 목록 조회
    public List<CampListResponseDto> getRecommendedCampsByKeywords(Long userId, int size) {
        // 사용자 키워드 기반 추천 캠핑장 조회 로직 (예: 키워드 일치도 높은 순 정렬)
        List<Camp> recommendedCamps = campRepository.findRecommendedCampsByUserId(userId, size);

        return recommendedCamps.stream()
                .map(camp -> {
                    CampAddr addr = campAddrRepository.findByCampId(camp.getId());
                    List<CampKeyword> keywords = campKeywordRepository.findByCampId(camp.getId());
                    boolean isLike = campRepository.isLikedByUser(camp.getId(), userId);

                    return campMapper.toCampListDto(camp, addr, keywords, isLike);
                })
                .collect(Collectors.toList());
    }

    // 인기 캠핑장 목록 조회
    public Page<CampListResponseDto> getPopularCamps(Long userId, PageRequest pageRequest) {
        Page<Camp> popularCamps = campRepository.findPopularCamps(pageRequest);

        return popularCamps.map(camp -> {
            CampAddr addr = campAddrRepository.findByCampId(camp.getId());
            List<CampKeyword> keywords = campKeywordRepository.findByCampId(camp.getId());
            boolean isLike = campRepository.isLikedByUser(camp.getId(), userId);

            return campMapper.toCampListDto(camp, addr, keywords, isLike);
        });
    }

    // 캠핑장 상세 조회
    public CampDetailResponseDto getCampDetail(Long campId) {
        Camp camp = campRepository.findById(campId)
                .orElseThrow(() -> new IllegalArgumentException("캠핑장을 찾을 수 없습니다: " + campId));
        CampAddr addr = campAddrRepository.findByCampId(camp.getId());
        List<CampImage> images = campImageRepository.findByCampId(camp.getId());
        List<CampKeyword> keywords = campKeywordRepository.findByCampId(camp.getId());
        CampInfo info = campInfoRepository.findByCampId(camp.getId());

        return campMapper.toCampDetailDto(camp, addr, images, keywords, info);
    }

    // 캠핑지 목록 조회
    public List<CampSiteListResponseDto> getCampSites(Long campId) {
        List<CampSite> sites = campSiteRepository.findByCampId(campId);

        return sites.stream()
                .map(campMapper::toCampSiteListDto)
                .collect(Collectors.toList());
    }

    // 검색 캠핑장 목록 (예제)
    public Page<CampListResponseDto> searchCamps(String keyword, String location, PageRequest pageRequest, Long userId) {
        Page<Camp> camps = campRepository.searchCamps(keyword, location, pageRequest);

        return camps.map(camp -> {
            CampAddr addr = campAddrRepository.findByCampId(camp.getId());
            List<CampKeyword> keywords = campKeywordRepository.findByCampId(camp.getId());
            boolean isLike = campRepository.isLikedByUser(camp.getId(), userId);

            return campMapper.toCampListDto(camp, addr, keywords, isLike);
        });
    }
}

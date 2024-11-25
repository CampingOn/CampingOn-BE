package site.campingon.campingon.camp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.campingon.campingon.camp.dto.CampSiteResponseDto;
import site.campingon.campingon.camp.dto.CampSiteListResponseDto;
import site.campingon.campingon.camp.dto.admin.CampSiteCreateRequestDto;
import site.campingon.campingon.camp.dto.admin.CampSiteUpdateRequestDto;
import site.campingon.campingon.camp.entity.Camp;
import site.campingon.campingon.camp.entity.CampSite;
import site.campingon.campingon.camp.mapper.CampSiteMapper;
import site.campingon.campingon.camp.repository.CampRepository;
import site.campingon.campingon.camp.repository.CampSiteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampSiteService {

    private final CampRepository campRepository;
    private final CampSiteRepository campSiteRepository;
    private final CampSiteMapper campSiteMapper;

    @Transactional
    public CampSiteResponseDto createCampSite(Long campId, CampSiteCreateRequestDto createRequestDto) {
        Camp camp = campRepository.findById(campId)
                .orElseThrow(() -> new RuntimeException("캠핑장을 찾을 수 없습니다."));
        CampSite campSite = campSiteMapper.toCampSite(createRequestDto, camp);
        return campSiteMapper.toCampSiteDetailResponseDto(campSiteRepository.save(campSite));
    }

    @Transactional
    public CampSiteResponseDto updateCampSite(Long campId, Long siteId, CampSiteUpdateRequestDto updateRequestDto) {
        // 캠핑장 존재 여부 확인 (Optional: 캠핑장 검증 필요 시)
        if (!campRepository.existsById(campId)) {
            throw new RuntimeException("캠핑장을 찾을 수 없습니다.");
        }
        // 캠핑지 조회
        CampSite campSite = campSiteRepository.findByIdAndCampId(siteId, campId)
                .orElseThrow(() -> new RuntimeException("캠핑지를 찾을 수 없습니다."));
        campSiteMapper.updateCampSiteFromDto(updateRequestDto, campSite);
        return campSiteMapper.toCampSiteDetailResponseDto(campSiteRepository.save(campSite));
    }

    @Transactional
    public void deleteCampSite(Long campId, Long siteId) {
        // 해당 캠핑지 존재 여부 확인 (Optional: 검증)
        if (!campSiteRepository.existsById(siteId)) {
            throw new RuntimeException("캠핑지를 찾을 수 없습니다.");
        }
        campSiteRepository.deleteByIdAndCampId(siteId, campId);
    }

    // 캠핑지 전체 조회
    public List<CampSiteListResponseDto> getCampSites(Long campId) {
        List<CampSite> campSites = campSiteRepository.findAllByCampId(campId);
        return campSites.stream()
                .map(campSiteMapper::toCampSiteListResponseDto)
                .toList();
    }


    // 특정 캠핑지 조회
    public CampSiteResponseDto getCampSite(Long campId, Long siteId) {
        CampSite campSite = campSiteRepository.findByIdAndCampId(siteId, campId)
                .orElseThrow(() -> new RuntimeException("캠핑지를 찾을 수 없습니다."));
        return campSiteMapper.toCampSiteDetailResponseDto(campSite);
    }
}
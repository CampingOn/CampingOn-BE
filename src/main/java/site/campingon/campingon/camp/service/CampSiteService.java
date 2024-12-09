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
import site.campingon.campingon.common.exception.ErrorCode;
import site.campingon.campingon.common.exception.GlobalException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampSiteService {

    private final CampRepository campRepository;
    private final CampSiteRepository campSiteRepository;
    private final CampSiteMapper campSiteMapper;

    // 새 캠핑지 생성
    @Transactional
    public CampSiteResponseDto createCampSite(Long campId, CampSiteCreateRequestDto createRequestDto) {

        Camp camp = campRepository.findById(campId)
                .orElseThrow(() -> new GlobalException(ErrorCode.CAMP_NOT_FOUND_BY_ID));

        CampSite campSite = campSiteMapper.toCampSite(createRequestDto, camp);
        return campSiteMapper.toCampSiteResponseDto(campSiteRepository.save(campSite));
    }

    // 캠핑지 수정
    @Transactional
    public CampSiteResponseDto updateCampSite(Long campId, Long siteId, CampSiteUpdateRequestDto updateRequestDto) {

        // 캠핑지 조회
        // Optional을 반환하여 존재 여부를 처리
        CampSite campSite = campSiteRepository.findByIdAndCampId(siteId, campId)
            .orElseThrow(() -> new GlobalException(ErrorCode.CAMPSITE_NOT_FOUND_BY_ID));

        campSiteMapper.updateCampSiteFromDto(updateRequestDto, campSite);
        return campSiteMapper.toCampSiteResponseDto(campSiteRepository.save(campSite));
    }

    // 캠핑지 삭제
    @Transactional
    public void deleteCampSite(Long campId, Long siteId) {

        // Optional을 반환하여 존재 여부를 처리
        CampSite campSite = campSiteRepository.findByIdAndCampId(siteId, campId)
            .orElseThrow(() -> new GlobalException(ErrorCode.CAMPSITE_NOT_FOUND_BY_ID));

        campSiteRepository.delete(campSite);
    }

    // 캠핑지 전체 조회
    @Transactional(readOnly = true)
    public List<CampSiteListResponseDto> getCampSites(Long campId) {

        List<CampSite> campSites = campSiteRepository.findAllByCampId(campId);

        return campSites.stream()
                .map(campSiteMapper::toCampSiteListResponseDto)
                .toList();
    }

    // 특정 캠핑지 조회
    @Transactional(readOnly = true)
    public CampSiteResponseDto getCampSite(Long campId, Long siteId) {

        CampSite campSite = campSiteRepository.findByIdAndCampId(siteId, campId)
                .orElseThrow(() -> new GlobalException(ErrorCode.CAMPSITE_NOT_FOUND_BY_ID));

        return campSiteMapper.toCampSiteResponseDto(campSite);
    }

    // isAvailable 상태를 토글하고 변경된 상태 반환
    @Transactional
    public boolean toggleAvailability(Long campId, Long siteId) {

        CampSite campSite = campSiteRepository.findByIdAndCampId(siteId, campId)
                .orElseThrow(() -> new GlobalException(ErrorCode.CAMPSITE_NOT_FOUND_BY_ID));

        boolean newAvailability = !campSite.isAvailable();
        CampSite updatedCampSite = campSite.toBuilder()
                .isAvailable(newAvailability)
                .build();

        campSiteRepository.save(updatedCampSite);
        return newAvailability;
    }

    // isAvailable 상태 조회
    @Transactional(readOnly = true)
    public boolean getAvailability(Long campId, Long siteId) {

        CampSite campSite = campSiteRepository.findByIdAndCampId(siteId, campId)
                .orElseThrow(() -> new GlobalException(ErrorCode.CAMPSITE_NOT_FOUND_BY_ID));

        return campSite.isAvailable();
    }

}
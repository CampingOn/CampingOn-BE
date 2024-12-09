package site.campingon.campingon.camp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.campingon.campingon.camp.dto.CampSiteListResponseDto;
import site.campingon.campingon.camp.entity.CampSite;
import site.campingon.campingon.camp.mapper.CampSiteMapper;
import site.campingon.campingon.camp.repository.CampRepository;
import site.campingon.campingon.camp.repository.CampSiteRepository;
import site.campingon.campingon.common.exception.ErrorCode;
import site.campingon.campingon.common.exception.GlobalException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampSiteReserveService {

    private final CampRepository campRepository;
    private final CampSiteRepository campSiteRepository;
    private final CampSiteMapper campSiteMapper;

    // 캠핑장의 예약 가능한 SiteType 별 캠핑지 조회
    @Transactional(readOnly = true)
    public List<CampSiteListResponseDto> getAvailableCampSites(Long campId, LocalDate checkin, LocalDate checkout) {

        campRepository.findById(campId)
                .orElseThrow(() -> new GlobalException(ErrorCode.CAMP_NOT_FOUND_BY_ID));

        List<CampSite> campSites = campSiteRepository.findAvailableCampSites(campId, checkin, checkout);

        log.debug("\n{}\n", campSites.toString());

        // 타입별 첫 번째 행 데이터만 가져오기
        return campSites.stream()
                .collect(Collectors.groupingBy(CampSite::getSiteType))
                .values().stream()
                .map(List::getFirst)
                .map(campSiteMapper::toCampSiteListResponseDto)
                .toList();
    }
}

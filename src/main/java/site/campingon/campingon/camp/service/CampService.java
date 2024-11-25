package site.campingon.campingon.camp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import site.campingon.campingon.bookmark.repository.BookMarkRepository;
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
  private final BookMarkRepository bookMarkRepository;
  private final CampMapper campMapper;

  // 추천 캠핑장 조회 (페이지네이션 - 횡스크롤 3개)
  public Page<CampListResponseDto> getMatchedCampsByKeywords(Long userId, Pageable pageable) {
    List<String> userKeywords = userKeywordRepository.findKeywordsByUserId(userId);

        if (userKeywords.isEmpty()) {
            return Page.empty(pageable);
        }

        // 키워드 매칭된 캠핑장을 페이지네이션으로 조회
        Page<Camp> recommendedCamps = campRepository.findRecommendedCampsByKeywords(userKeywords, pageable);

        List<CampListResponseDto> campDtos = recommendedCamps.getContent().stream()
                .map(camp -> {
                    CampListResponseDto dto = campMapper.toCampListDto(camp);
                    dto.setMarked(bookMarkRepository.existsByCampIdAndUserId(camp.getId(), userId));
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(campDtos, pageable, recommendedCamps.getTotalElements());
    }

    // 인기 캠핑장 조회 - recommendCnt에 따른
    public Page<CampListResponseDto> getPopularCamps(Long userId, Pageable pageable) {
        Page<Camp> camps = campRepository.findPopularCamps(pageable);

    List<CampListResponseDto> campDtos = recommendedCamps.getContent().stream()
        .map(camp -> {
          CampListResponseDto dto = campMapper.toCampListDto(camp);
          dto.setMarked(bookMarkRepository.existsByCampIdAndUserId(camp.getId(), userId));
          return dto;
        })
        .collect(Collectors.toList());

        return new PageImpl<>(campDtos, pageable, camps.getTotalElements());
    }

    // 캠핑장 상세 조회
    public CampDetailResponseDto getCampDetail(Long campId) {
        Camp camp = campRepository.findById(campId)
                .orElseThrow(() -> new RuntimeException("캠핑장을 찾을 수 없습니다."));
    List<CampListResponseDto> campDtos = camps.getContent().stream()
        .map(camp -> {
          CampListResponseDto dto = campMapper.toCampListDto(camp);
          dto.setMarked(bookMarkRepository.existsByCampIdAndUserId(camp.getId(), userId));
          return dto;
        })
        .collect(Collectors.toList());

    // 캠핑장의 캠핑지 목록 조회
    public List<CampSiteListResponseDto> getCampSites(Long campId) {
        List<CampSite> campSites = campSiteRepository.findByCampId(campId);

        return campSites.stream()
                .map(campMapper::toCampSiteListDto)
                .collect(Collectors.toList());
    }


    @Transactional
    // 캠핑장 생성
    public CampDetailResponseDto createCamp(Camp camp) {
        return campMapper.toCampDetailDto(campRepository.save(camp));
    }

    @Transactional
    // 캠핑장 수정
    public CampDetailResponseDto updateCamp(Long campId, Camp updatedCamp) {
        Camp existingCamp = campRepository.findById(campId)
                .orElseThrow(() -> new RuntimeException("캠핑장을 찾을 수 없습니다."));
        campMapper.updateCampFromDto(updatedCamp, existingCamp);
        return campMapper.toCampDetailDto(campRepository.save(existingCamp));
    }

    @Transactional
    // 캠핑장 삭제
    public void deleteCamp(Long id) {
        campRepository.deleteById(id);
    }

    // 모든 캠핑장 조회
    public List<CampListResponseDto> getAllCamps() {
        List<Camp> camps = campRepository.findAll();
        return camps.stream()
                .map(campMapper::toCampListDto)
                .toList();
    }
}
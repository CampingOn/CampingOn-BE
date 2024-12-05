package site.campingon.campingon.camp.service.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import site.campingon.campingon.bookmark.repository.BookmarkRepository;
import site.campingon.campingon.camp.dto.CampListResponseDto;
import site.campingon.campingon.camp.mapper.mongodb.SearchInfoMapper;
import site.campingon.campingon.camp.repository.mongodb.SearchInfoRepository;
import site.campingon.campingon.camp.repository.mongodb.SearchInfoRepositoryImpl;
import site.campingon.campingon.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SearchInfoService {
  private final UserService userService;
  private final SearchInfoRepository searchInfoRepository;
  private final BookmarkRepository bookmarkRepository;
  private final SearchInfoMapper searchInfoMapper;

  public Page<CampListResponseDto> searchExactMatchBySearchTermAndUserKeyword(
      String city, String searchTerm, Long userId, Pageable pageable) {


    List<String> userKeywords = userId != 0L ?
        userService.getKeywordsByUserId(userId) :
        new ArrayList<>();

    // city와 searchTerm이 비어있는 경우 빈 문자열로 초기화
    city = StringUtils.hasText(city) ? city : "";
    searchTerm = StringUtils.hasText(searchTerm) ? searchTerm : "";

    // 검색 수행 (결과와 전체 개수를 한 번에 조회)
      SearchInfoRepositoryImpl.SearchResult searchResult =
        searchInfoRepository.searchWithUserPreferences(searchTerm, userKeywords, city, pageable);

    // 검색 결과가 없는 경우 빈 페이지 반환
    if (searchResult.getResults().isEmpty()) {
      return Page.empty(pageable);
    }

    // DTO 변환 및 Page 객체 생성
    List<CampListResponseDto> dtoList = searchResult.getResults().stream()
        .map(searchInfo -> {
          CampListResponseDto dto = searchInfoMapper.toDto(searchInfo);

          // 인증된 사용자인 경우에만 북마크와 유저명 설정
          if (userId != 0L) {
            // 북마크 상태 설정
            dto.setMarked(bookmarkRepository.existsByCampIdAndUserId(searchInfo.getCampId(), userId));
          }
          return dto;
        })
        .collect(Collectors.toList());

    return new PageImpl<>(dtoList, pageable, searchResult.getTotal());
  }
}

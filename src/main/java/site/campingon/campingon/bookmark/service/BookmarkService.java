package site.campingon.campingon.bookmark.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.campingon.campingon.bookmark.entity.Bookmark;
import site.campingon.campingon.bookmark.repository.BookmarkRepository;
import site.campingon.campingon.camp.entity.Camp;
import site.campingon.campingon.camp.repository.CampRepository;
import site.campingon.campingon.user.entity.User;
import site.campingon.campingon.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {
  private final BookmarkRepository bookmarkRepository;
  private final CampRepository campRepository;
  private final UserRepository userRepository;

  // 찜 기능 (토글활용)
  public void bookmarkCamp(Long campId, Long userId) {
    // 이미 찜 관계가 있는지 확인
    Optional<Bookmark> existingBookmark = bookmarkRepository.findByCampIdAndUserId(campId, userId);

    // 이미 찜 관계가 있는 상태
    if (existingBookmark.isPresent()) {
      // isMarked 상태를 반대로 토글 - true->false, false->true로 전환
      Bookmark bookmark = existingBookmark.get().toBuilder()
          .isMarked(!existingBookmark.get().isMarked())
          .build();
      bookmarkRepository.save(bookmark);
      return;  // 변경 후 반환
    }

    // 새로운 찜 관계 생성
    Camp camp = campRepository.findById(campId)
        .orElseThrow(() -> new RuntimeException("캠핑장을 찾을 수 없습니다."));

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

    Bookmark bookmark = Bookmark.builder()
        .camp(camp)
        .user(user)
        .build();  // isMarked는 기본값 true

    bookmarkRepository.save(bookmark);
  }

}

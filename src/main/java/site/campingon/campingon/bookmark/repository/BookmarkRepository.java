package site.campingon.campingon.bookmark.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.campingon.campingon.bookmark.entity.Bookmark;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
  boolean existsByCampIdAndUserId(Long campId, Long userId);

  Optional<Bookmark> findByCampIdAndUserId(Long campId, Long userId);
}
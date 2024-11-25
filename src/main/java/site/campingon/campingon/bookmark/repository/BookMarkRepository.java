package site.campingon.campingon.bookmark.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.campingon.campingon.bookmark.entity.BookMark;

@Repository
public interface BookMarkRepository extends JpaRepository<BookMark, Long> {
  boolean existsByCampIdAndUserId(Long campId, Long userId);
}
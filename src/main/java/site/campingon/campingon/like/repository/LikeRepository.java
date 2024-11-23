package site.campingon.campingon.like.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.campingon.campingon.like.entity.Like;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
  boolean existsByCampIdAndUserId(Long campId, Long userId);
  // 특정 회원이 특정 캠핑장을 좋아요했는지 여부를 확인하는 메서드
  Optional<Like> findByUserIdAndCampId(Long userId, Long campId);
}

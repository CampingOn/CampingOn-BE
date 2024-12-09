package site.campingon.campingon.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.campingon.campingon.user.entity.UserKeyword;

import java.util.List;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
  @Query("SELECT k.keyword FROM UserKeyword k WHERE k.user.id = :userId")
  List<String> findKeywordsByUserId(@Param("userId")Long userId);


  @Query("SELECT k FROM UserKeyword k WHERE k.user.id = :userId AND k.keyword = :keyword")
  Optional<UserKeyword> findByUserIdAndKeyword(@Param("keyword")String keyword, @Param("userId")Long userId);
}
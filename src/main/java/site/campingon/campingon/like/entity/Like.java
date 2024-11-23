package site.campingon.campingon.like.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.campingon.campingon.camp.entity.Camp;
import site.campingon.campingon.user.entity.User;


@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Like {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(columnDefinition = "INT UNSIGNED")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "camp_id", nullable = false)
  private Camp camp;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Builder.Default
  @Column(name = "is_like", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")  // 새로 찜관계 DB 삽입 시 true
  private boolean isLike = true;  // 객체 생성 시 true

  @Builder
  public Like(Camp camp, User user) {
    this.camp = camp;
    this.user = user;
  }
}
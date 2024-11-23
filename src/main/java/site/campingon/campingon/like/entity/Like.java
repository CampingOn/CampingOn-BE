package site.campingon.campingon.like.entity;

import jakarta.persistence.*;
import lombok.*;
import site.campingon.campingon.user.entity.User;
import site.campingon.campingon.camp.entity.Camp;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "like")
public class Like {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(columnDefinition = "BIGINT UNSIGNED")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "camp_id", nullable = false)
  private Camp camp;

  @Column(name = "is_like", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")  // 새로 찜관계 DB 삽입 시 true
  private boolean isLike = true;  // 객체 생성 시 true

  public void setLike(boolean isLike) {
    this.isLike = isLike;
  }

  public void toggleLike() {
    this.isLike = !this.isLike;
  }
}
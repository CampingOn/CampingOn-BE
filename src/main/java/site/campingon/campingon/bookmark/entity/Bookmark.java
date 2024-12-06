package site.campingon.campingon.bookmark.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.campingon.campingon.camp.entity.Camp;
import site.campingon.campingon.common.entity.BaseEntity;
import site.campingon.campingon.user.entity.User;


@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(columnDefinition = "INT UNSIGNED")
  private Long id;

  @ManyToOne
  @JsonIgnore
  @JoinColumn(name = "camp_id", nullable = false)
  private Camp camp;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

}
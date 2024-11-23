package site.campingon.campingon.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.campingon.campingon.common.entity.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder(toBuilder = true)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 60)
    private String password;

    // oauth 로그인인 경우에 생성되는 값
    private String oauthName;

    @Column(nullable = false, length = 24, unique = true)
    private String nickname;

    @Builder.Default
    @Column(name="is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name="delete_reason", columnDefinition = "TEXT")
    private String deleteReason;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 회원 탈퇴 로직
    public void deleteUser(String deleteReason) {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deleteReason = deleteReason;
    }

    // 회원 닉네임 변경 로직
    public void updateNickname(String newNickname) {
        if (newNickname == null || newNickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 비어 있을 수 없습니다.");
        }
        this.nickname = newNickname;
    }

    // 회원 비밀번호 변경 로직
    public void updatePassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
        }
        this.password = newPassword;
    }

}
package site.campingon.campingon.like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.campingon.campingon.camp.entity.Camp;
import site.campingon.campingon.camp.repository.CampRepository;
import site.campingon.campingon.like.entity.Like;
import site.campingon.campingon.like.repository.LikeRepository;
import site.campingon.campingon.user.entity.User;
import site.campingon.campingon.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final CampRepository campRepository;

    @Transactional
    public void toggleLike(Long userId, Long campId) {
        Like like = likeRepository.findByUserIdAndCampId(userId, campId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
                    Camp camp = campRepository.findById(campId)
                            .orElseThrow(() -> new IllegalArgumentException("캠핑장이 존재하지 않습니다."));

                    return Like.builder()
                            .user(user)
                            .camp(camp)
                            .isLike(false)
                            .build();
                });

        like.setLike(!like.isLike());
        likeRepository.save(like);
    }
}

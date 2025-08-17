package com.speako.domain.userinfo.service.query.userAchievement;

import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.repository.UserBadgeRepository;
import com.speako.domain.user.domain.User;
import com.speako.domain.userinfo.domain.UserAchievement;
import com.speako.domain.userinfo.dto.resDTO.userAchievement.UserAchievementResDTO;
import com.speako.domain.userinfo.exception.UserAchievementErrorCode;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAchievementQueryService {

    private final UserBadgeRepository userBadgeRepository;

    // 사용자의 대표정보 조회
    public UserAchievementResDTO getUserAchievement(User user) {

        // 사용자의 대표 뱃지 이름 조회
        UserAchievement achievement = user.getUserAchievement();
        if (achievement == null) {
            throw new CustomException(UserAchievementErrorCode.USER_ACHIEVEMENT_NOT_FOUND);
        }
        Optional<UserBadge> userMainBadge = userBadgeRepository.findByUserIdAndIsMain(user.getId());
        Optional<String> userMainBadgeName = userMainBadge.map(userBadge -> userBadge.getBadge().getName());

        // 뱃지의 총 개수와 현재 획득 수를 통한 뱃지 획득률 계산
        int current = achievement.getCurrentBadgeCount();
        int total = achievement.getTotalBadgeCount();
        // 미획득 뱃지를 포함한 전체 뱃지 수는 0일 수 없음
        if (total == 0) {
            throw new CustomException(UserAchievementErrorCode.USER_ACHIEVEMENT_BADGE_TOTAL_ZERO);
        }
        float badgeAcquisitionRate = ((float) current / total) * 100.0f;

        return new UserAchievementResDTO(
                user.getUsername(),
                user.getImageType().getImageUrl(),
                userMainBadgeName,
                achievement.getSelfComment(),
                achievement.getTotalRecordedDays(),
                achievement.getAvgPositiveRatio(),
                badgeAcquisitionRate
        );
    }
}

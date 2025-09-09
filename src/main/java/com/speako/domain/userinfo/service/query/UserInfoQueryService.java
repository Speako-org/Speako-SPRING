package com.speako.domain.userinfo.service.query;

import com.speako.domain.challenge.converter.UserBadgeConverter;
import com.speako.domain.challenge.dto.UserBadgeResponse;
import com.speako.domain.challenge.service.query.UserChallengeQueryService;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.exception.UserErrorCode;
import com.speako.domain.user.repository.UserRepository;
import com.speako.domain.userinfo.dto.resDTO.monthlyStat.MonthlyStatResDTO;
import com.speako.domain.userinfo.dto.resDTO.userAchievement.UserAchievementResDTO;
import com.speako.domain.userinfo.dto.resDTO.MyPageResDTO;
import com.speako.domain.userinfo.service.query.monthlyStat.MonthlyStatQueryService;
import com.speako.domain.userinfo.service.query.userAchievement.UserAchievementQueryService;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserInfoQueryService {

    private final UserRepository userRepository;

    private final UserAchievementQueryService userAchievementService;
    private final MonthlyStatQueryService monthlyStatService;
    private final UserChallengeQueryService userChallengeQueryService;

    // 본인 또는 타인의 마이페이지 정보 조회 API
    public MyPageResDTO getMyPage(Long userId, Long targetUserId) {
        /*
            로직 상단에서 findById(targetUserId) 하는 이유
            1. targetUserId의 유효성 판단 (존재하는 userId인가?)
            2. 분기 시에도 공통으로 사용하기 위함 (본인이든 타인이든, 조회할 마이페이지는 targetUser 것이기 때문)
        */
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        // 두 경우에서 공통으로 조회되는 UserAchievement 미리 조회
        UserAchievementResDTO achievement = userAchievementService.getUserAchievement(targetUser);

        if (userId.equals(targetUserId)) {
            // 본인의 마이페이지 조회 (유저 대표정보와 월별통계 조회)
            List<MonthlyStatResDTO> monthlyStats = monthlyStatService.get6RecentMonthlyStats(targetUser.getId());
            return new MyPageResDTO(true, targetUserId, achievement, monthlyStats, null);
        } else {
            // 타인의 마이페이지 조회 (유저 대표정보와 획득 뱃지리스트 조회)
            List<UserBadgeResponse> userBadges = userChallengeQueryService.getUserBadges(targetUserId)
                    .stream().map(UserBadgeConverter::toUserBadgeResponse)
                    .toList();
            return new MyPageResDTO(false, targetUserId, achievement, null, userBadges);
        }
    }
}

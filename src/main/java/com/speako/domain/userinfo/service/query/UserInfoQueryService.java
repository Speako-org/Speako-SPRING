package com.speako.domain.userinfo.service.query;

import com.speako.domain.user.domain.User;
import com.speako.domain.user.exception.UserErrorCode;
import com.speako.domain.user.repository.UserRepository;
import com.speako.domain.userinfo.dto.resDTO.monthlyStat.MonthlyStatResDTO;
import com.speako.domain.userinfo.dto.resDTO.userAchievement.UserAchievementResDTO;
import com.speako.domain.userinfo.dto.resDTO.UserInfoResDTO;
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

    // 사용자의 마이페이지 정보(유저 대표정보+월별통계)를 조회
    public UserInfoResDTO getUserInfo(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        // 유저 대표정보와 월별통계를 조회한 후 UserInfoResDTO를 생성하여 반환
        UserAchievementResDTO achievement = userAchievementService.getUserAchievement(user);
        List<MonthlyStatResDTO> monthlyStats = monthlyStatService.get6RecentMonthlyStats(user.getId());

        return new UserInfoResDTO(userId, achievement, monthlyStats);
    }
}

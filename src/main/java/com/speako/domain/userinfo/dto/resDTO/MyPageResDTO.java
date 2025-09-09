package com.speako.domain.userinfo.dto.resDTO;

import com.speako.domain.challenge.dto.UserBadgeResponse;
import com.speako.domain.userinfo.dto.resDTO.monthlyStat.MonthlyStatResDTO;
import com.speako.domain.userinfo.dto.resDTO.userAchievement.UserAchievementResDTO;

import java.util.List;

// 마이페이지 조회 통합 DTO
public record MyPageResDTO(

        boolean isMyPage, // 조회한 마이페이지가 본인 소유인가?
        Long userId,
        UserAchievementResDTO achievement,
        List<MonthlyStatResDTO> monthlyStats,
        List<UserBadgeResponse> userBadges
) {}

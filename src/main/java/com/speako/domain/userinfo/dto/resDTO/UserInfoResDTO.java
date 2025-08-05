package com.speako.domain.userinfo.dto.resDTO;

import com.speako.domain.userinfo.dto.resDTO.monthlyStat.MonthlyStatResDTO;
import com.speako.domain.userinfo.dto.resDTO.userAchievement.UserAchievementResDTO;

import java.util.List;

// 마이페이지 조회 통합 DTO
public record UserInfoResDTO(

        Long userId,
        UserAchievementResDTO achievement,
        List<MonthlyStatResDTO> monthlyStats
) {}

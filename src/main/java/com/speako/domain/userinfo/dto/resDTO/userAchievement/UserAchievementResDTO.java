package com.speako.domain.userinfo.dto.resDTO.userAchievement;

import java.util.Optional;

// 마이페이지 속 유저 대표정보 DTO
public record UserAchievementResDTO(

        String nickname,
        String profileImageUrl,
        Optional<String> mainBadgeName,
        String selfComment,
        int totalRecordedDays,
        float avgPositiveRatio,
        float badgeAcquisitionRate
) {
}

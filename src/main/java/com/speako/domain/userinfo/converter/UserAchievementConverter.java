package com.speako.domain.userinfo.converter;

import com.speako.domain.user.domain.User;
import com.speako.domain.userinfo.domain.UserAchievement;

public class UserAchievementConverter {

    public static UserAchievement toUserAchievement(User user, int totalBadgeCount) {

        return UserAchievement.builder()
                .user(user)
                .selfComment("안녕하세요 :)")
                .totalBadgeCount(totalBadgeCount)
                .build();
    }
}

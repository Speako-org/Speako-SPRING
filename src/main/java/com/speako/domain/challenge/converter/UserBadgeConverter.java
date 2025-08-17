package com.speako.domain.challenge.converter;

import com.speako.domain.challenge.domain.Badge;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.dto.UserBadgeResponse;
import org.springframework.stereotype.Component;

@Component
public class UserBadgeConverter {

    public static UserBadgeResponse toUserBadgeResponse(UserBadge userBadge) {
        Badge badge = userBadge.getBadge();

        return UserBadgeResponse.builder()
                .userBadgeId(userBadge.getId())
                .badgeName(badge.getName())
                .description(badge.getDescription())
                .icon(badge.getIconCode().getEmoji())
                .build();
    }
}

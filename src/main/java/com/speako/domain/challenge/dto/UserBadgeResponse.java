package com.speako.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserBadgeResponse {
    private Long userBadgeId;
    private String badgeName;
    private String description;
    private String icon;
}

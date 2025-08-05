package com.speako.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserChallengeResponse {
    private String challengeName;
    private String description;
    private String levelName;
    private Integer currentAmount;
    private Integer requiredAmount;
    private Double progressPercentage;
}

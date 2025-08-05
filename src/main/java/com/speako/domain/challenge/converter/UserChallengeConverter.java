package com.speako.domain.challenge.converter;

import com.speako.domain.challenge.domain.Challenge;
import com.speako.domain.challenge.domain.UserChallenge;
import com.speako.domain.challenge.dto.UserChallengeResponse;
import org.springframework.stereotype.Component;

@Component
public class UserChallengeConverter {
    public static UserChallengeResponse toChallengeResponse(UserChallenge userChallenge) {
        Challenge challenge = userChallenge.getChallenge();
        double progressPercentage = (double) userChallenge.getAmount() / challenge.getRequiredAmount() * 100;

        return UserChallengeResponse.builder()
                .challengeName(challenge.getName())
                .description(challenge.getDescription())
                .levelName(challenge.getLevelName())
                .currentAmount(userChallenge.getAmount())
                .requiredAmount(challenge.getRequiredAmount())
                .progressPercentage(progressPercentage)
                .build();
    }
}

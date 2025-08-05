package com.speako.domain.challenge.service.query;

import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.domain.UserChallenge;

import java.util.List;

public interface UserChallengeQueryService {
    /**
     * 활설화된 챌린지 가져오기
     * @param userId
     * @return
     */
    List<UserChallenge> getActiveChallenges(Long userId);

    /**
     * 획득한 뱃지 가져오기
     * @param userId
     * @return
     */
    List<UserBadge> getUserBadges(Long userId);
}

package com.speako.domain.challenge.service.command;

import com.speako.domain.analysis.domain.Analysis;
import com.speako.domain.user.domain.User;

public interface UserChallengeService {

    /**
     * 챌린지 업데이트
     * @param user
     * @param analysis
     */
    void updateChallengeProgress(User user, Analysis analysis);

    /**
     * 사용자 계정 생성시 챌린지 부여
     * @param user
     */
    void initializeUserChallenges(User user);
}

package com.speako.domain.challenge.service.query;

import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.domain.UserChallenge;
import com.speako.domain.challenge.repository.UserBadgeRepository;
import com.speako.domain.challenge.repository.UserChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserChallengeQueryServiceImpl implements UserChallengeQueryService{
    private final UserChallengeRepository userChallengeRepository;
    private final UserBadgeRepository userBadgeRepository;
    /**
     * 활성 챌린지 조회
     */
    public List<UserChallenge> getActiveChallenges(Long userId) {
        return userChallengeRepository.findByUserIdAndIsActiveTrue(userId);
    }

    /**
     * 획득한 뱃지 조회
     */
    public List<UserBadge> getUserBadges(Long userId) {
        return userBadgeRepository.findByUserId(userId);
    }
}

package com.speako.domain.challenge.service.command;

import com.speako.domain.analysis.domain.Analysis;
import com.speako.domain.analysis.repository.AnalysisRepository;
import com.speako.domain.challenge.domain.Badge;
import com.speako.domain.challenge.domain.Challenge;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.domain.UserChallenge;
import com.speako.domain.challenge.domain.enums.ChallengeType;
import com.speako.domain.challenge.repository.BadgeRepository;
import com.speako.domain.challenge.repository.ChallengeRepository;
import com.speako.domain.challenge.repository.UserBadgeRepository;
import com.speako.domain.challenge.repository.UserChallengeRepository;
import com.speako.domain.record.repository.RecordRepository;
import com.speako.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserChallengeServiceImpl implements UserChallengeService {
    private final UserChallengeRepository userChallengeRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeProgressProcessor progressProcessor;


    /**
     * 기록 저장 후 챌린지 진행도 업데이트
     */
    public void updateChallengeProgress(User user, Analysis analysis) {
        List<UserChallenge> activeChallenges = userChallengeRepository.findByUserAndIsActiveTrue(user);

        for (UserChallenge userChallenge : activeChallenges) {
            // 챌린지 진행도 업데이트
            boolean updated = progressProcessor.processChallenge(userChallenge, analysis);

            // 챌린지를 달성했으면 완료처리 뱃지 발급 후 다음 챌린지 생성
            if (updated && userChallenge.isAchieved()) {
                progressProcessor.completeChallenge(userChallenge);
                progressProcessor.createNextLevelChallenge(userChallenge);
                progressProcessor.createBadge(userChallenge);
            }
        }
    }

    /**
     * 사용자 신규 가입 시 레벨 1 챌린지들 생성
     */
    public void initializeUserChallenges(User user) {
        // ChallengeType Enum의 모든 값들로 초기화
        for (ChallengeType challengeType : ChallengeType.values()) {
            Optional<Challenge> challenge = challengeRepository.findByNameAndLevel(challengeType.getDisplayName(), 1);

            UserChallenge userChallenge = UserChallenge.builder()
                    .user(user)
                    .challenge(challenge.get())
                    .amount(0)
                    .isActive(true)
                    .build();

            userChallengeRepository.save(userChallenge);
        }
    }
}

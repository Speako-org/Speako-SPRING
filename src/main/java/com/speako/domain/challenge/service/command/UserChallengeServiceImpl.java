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
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    /**
     * 기록 저장 후 챌린지 진행도 업데이트
     */
    public void updateChallengeProgress(User user, Analysis analysis) {
        List<UserChallenge> activeChallenges = userChallengeRepository.findByUserAndIsActiveTrue(user);

        for (UserChallenge userChallenge : activeChallenges) {
            // 챌린지 진행도 업데이트
            boolean updated = processChallenge(userChallenge, analysis);

            // 챌린지를 달성했으면 완료처리 뱃지 발급 후 다음 챌린지 생성
            if (updated && userChallenge.isAchieved()) {
                completeChallenge(userChallenge);
                createBadge(userChallenge);
                createNextLevelChallenge(userChallenge);
            }
        }
    }

    /**
     * 각 챌린지별 진행도 업데이트
     */
    private boolean processChallenge(UserChallenge userChallenge, Analysis analysis) {
        String challengeName = userChallenge.getChallenge().getName();
        ChallengeType challengeType = ChallengeType.ofDisplayName(challengeName);

        return switch (challengeType) {
            case CONTINUOUS_RECORD -> processContinuousRecordChallenge(userChallenge);
            case RECORD_COUNT -> processRecordCountChallenge(userChallenge);
            case POSITIVE_EXPRESSION -> processPositiveIncreaseChallenge(userChallenge, analysis);
            case NEGATIVE_REDUCTION -> processNegativeDecreaseChallenge(userChallenge, analysis);
        };
    }

    /**
     * 연속 기록 챌린지
     */
    private boolean processContinuousRecordChallenge(UserChallenge userChallenge) {
        LocalDate today = LocalDate.now();

        // 이미 오늘 기록했으면 그냥 넘김
        if (userChallenge.isTodayRecorded(today)) {
            return false;
        }

        // 첫 기록이거나 어제 기록한 경우만 연속으로 인정
        if (userChallenge.getLastRecordDate() == null || userChallenge.isYesterdayRecorded(today)) {
            userChallenge.updateProgress(1, today);
            return true;
        } else {
            // 1부터 다시 시작
            userChallenge.updateProgress(1 - userChallenge.getAmount(), today);
            return true;
        }
    }

    /**
     * 긍정 표현 달성 챌린지 처리
     */
    private boolean processPositiveIncreaseChallenge(UserChallenge userChallenge, Analysis analysis) {
        if (analysis.getPositiveRatio() >= 0.6) {
            userChallenge.updateProgress(1);
            return true;
        }
        return false;
    }

    /**
     * 부정 감소 챌린지 처리
     */
    private boolean processNegativeDecreaseChallenge(UserChallenge userChallenge, Analysis analysis) {
        if (analysis.getNegativeRatio() <= 0.2) {
            userChallenge.updateProgress(1);
            return true;
        }
        return false;
    }

    /**
     * 기록 수 달성 챌린지 처리
     */
    private boolean processRecordCountChallenge(UserChallenge userChallenge) {
        userChallenge.updateProgress(1);
        return true;
    }

    /**
     * 챌린지 완료 처리
     */
    private void completeChallenge(UserChallenge userChallenge) {
        userChallenge.complete();
        userChallengeRepository.save(userChallenge);
    }

    /**
     * 다음 레벨 챌린지 생성
     */
    private void createNextLevelChallenge(UserChallenge completedChallenge) {
        String challengeName = completedChallenge.getChallenge().getName();
        Integer nextLevel = completedChallenge.getChallenge().getLevel() + 1;

        Optional<Challenge> nextChallenge = challengeRepository.findByNameAndLevel(challengeName, nextLevel);

        if (nextChallenge.isPresent()) {
            UserChallenge newUserChallenge = UserChallenge.builder()
                    .user(completedChallenge.getUser())
                    .challenge(nextChallenge.get())
                    .amount(0)
                    .isActive(true)
                    .build();

            userChallengeRepository.save(newUserChallenge);
        }
    }

    /**
     * 뱃지 생성
     * - 해당 챌린지의 레벨에 맞는 뱃지를 가져옴
     * - 모든 뱃지를 가져화서 해당 하는 뱃지를 부여
     */
    private void createBadge(UserChallenge userChallenge) {
        String challengeName = userChallenge.getChallenge().getName();
        Integer level = userChallenge.getChallenge().getLevel();

        ChallengeType challengeType = ChallengeType.ofDisplayName(challengeName);
        int badgeIndex = challengeType.getBadgeIndex(level);

        List<Badge> allBadges = badgeRepository.findAll();

        Badge targetBadge = allBadges.get(badgeIndex);

        // 해당 뱃지를 소유하고 있지 않다면
        if (!userBadgeRepository.existsByUserAndBadge(userChallenge.getUser(), targetBadge)) {
            UserBadge userBadge = UserBadge.builder()
                    .user(userChallenge.getUser())
                    .badge(targetBadge)
                    .build();

            userBadgeRepository.save(userBadge);
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

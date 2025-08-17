package com.speako.domain.challenge.service.command;

import com.speako.domain.analysis.domain.Analysis;
import com.speako.domain.challenge.domain.Badge;
import com.speako.domain.challenge.domain.Challenge;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.domain.UserChallenge;
import com.speako.domain.challenge.domain.enums.ChallengeType;
import com.speako.domain.challenge.repository.BadgeRepository;
import com.speako.domain.challenge.repository.ChallengeRepository;
import com.speako.domain.challenge.repository.UserBadgeRepository;
import com.speako.domain.challenge.repository.UserChallengeRepository;
import com.speako.domain.userinfo.service.command.userAchievement.UserAchievementCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChallengeProgressProcessorImpl implements ChallengeProgressProcessor{

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final ChallengeRepository challengeRepository;
    private final UserAchievementCommandService userAchievementCommandService;

    @Override
    public boolean processChallenge(UserChallenge userChallenge, Analysis analysis) {
        String challengeName = userChallenge.getChallenge().getName();
        ChallengeType challengeType = ChallengeType.ofDisplayName(challengeName);

        return switch (challengeType) {
            case CONTINUOUS_RECORD -> processContinuousRecordChallenge(userChallenge);
            case RECORD_COUNT -> processRecordCountChallenge(userChallenge);
            case POSITIVE_EXPRESSION -> processPositiveIncreaseChallenge(userChallenge, analysis);
            case NEGATIVE_REDUCTION -> processNegativeDecreaseChallenge(userChallenge, analysis);
        };
    }


    @Override
    public boolean processContinuousRecordChallenge(UserChallenge userChallenge) {
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


    @Override
    public boolean processPositiveIncreaseChallenge(UserChallenge userChallenge, Analysis analysis) {
        if (analysis.getPositiveRatio() >= 0.6f) {
            userChallenge.updateProgress(1);
            return true;
        }
        return false;
    }


    @Override
    public boolean processNegativeDecreaseChallenge(UserChallenge userChallenge, Analysis analysis) {
        if (analysis.getNegativeRatio() <= 0.2f) {
            userChallenge.updateProgress(1);
            return true;
        }
        return false;
    }


    @Override
    public boolean processRecordCountChallenge(UserChallenge userChallenge) {
        userChallenge.updateProgress(1);
        return true;
    }

    @Override
    public void completeChallenge(UserChallenge userChallenge) {
        userChallenge.complete();
        userChallengeRepository.save(userChallenge);
    }


    @Override
    public void createNextLevelChallenge(UserChallenge completedChallenge) {
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


    @Override
    public void createBadge(UserChallenge userChallenge) {
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

            // UserAchievement 속 currentBadgeCount 업데이트
            userAchievementCommandService.increaseCurrentBadgeCount(userChallenge.getUser());
        }
    }
}

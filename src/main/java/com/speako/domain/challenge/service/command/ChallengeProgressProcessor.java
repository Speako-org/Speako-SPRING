package com.speako.domain.challenge.service.command;

import com.speako.domain.analysis.domain.Analysis;
import com.speako.domain.challenge.domain.UserChallenge;

public interface ChallengeProgressProcessor {

    /**
     * 각 챌린지별 진행도 업데이트
     * @param userChallenge
     * @param analysis
     * @return
     */
    boolean processChallenge(UserChallenge userChallenge, Analysis analysis);

    /**
     * 연속 기록 챌린지
     * @param userChallenge
     * @return
     */
    boolean processContinuousRecordChallenge(UserChallenge userChallenge);

    /**
     * 긍정 표현 달성 챌린지 처리
     * @param userChallenge
     * @param analysis
     * @return
     */
    boolean processPositiveIncreaseChallenge(UserChallenge userChallenge, Analysis analysis);

    /**
     * 부정 감소 챌린지 처리
     * @param userChallenge
     * @param analysis
     * @return
     */
    boolean processNegativeDecreaseChallenge(UserChallenge userChallenge, Analysis analysis);

    /**
     * 기록 수 달성 챌린지 처리
     * @param userChallenge
     * @return
     */
    boolean processRecordCountChallenge(UserChallenge userChallenge);

    /**
     * 챌린지 완료 처리
     *
     * @param userChallenge
     */
    void completeChallenge(UserChallenge userChallenge);

    /**
     * 다음 레벨 챌린지 생성
     * @param completedChallenge
     */
    void createNextLevelChallenge(UserChallenge completedChallenge);

    /**
     * 뱃지 생성
     * - 해당 챌린지의 레벨에 맞는 뱃지를 가져옴
     * - 모든 뱃지를 가져화서 해당 하는 뱃지를 부여
     * @param userChallenge
     */
    void createBadge(UserChallenge userChallenge);
}

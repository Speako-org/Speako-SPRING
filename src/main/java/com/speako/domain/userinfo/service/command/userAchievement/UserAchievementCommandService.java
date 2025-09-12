package com.speako.domain.userinfo.service.command.userAchievement;

import com.speako.domain.analysis.domain.Analysis;
import com.speako.domain.analysis.repository.AnalysisRepository;
import com.speako.domain.analysis.service.query.AnalysisQueryService;
import com.speako.domain.user.domain.User;
import com.speako.domain.userinfo.domain.UserAchievement;
import com.speako.domain.userinfo.exception.UserAchievementErrorCode;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAchievementCommandService {

    private final AnalysisRepository analysisRepository;
    private final AnalysisQueryService analysisQueryService;

    // 분석결과(Analysis)를 사용하여 사용자의 UserAchievement를 update
    public void updateUserAchievement(Analysis analysis) {

        User user = analysis.getTranscription().getUser();
        UserAchievement achievement = user.getUserAchievement();
        if (achievement == null) {
            throw new CustomException(UserAchievementErrorCode.USER_ACHIEVEMENT_NOT_FOUND);
        }
        // 기록 추가에 따른 UserAchievement 업데이트
        achievement.addRecord(analysis.getPositiveRatio(), analysis.getCreatedAt());
    }

    // UserAchievement rollback
    public void rollbackUserAchievement(Analysis analysis) {

        User user = analysis.getTranscription().getUser();
        UserAchievement userAchievement = user.getUserAchievement();
        if (userAchievement == null) {
            throw new CustomException(UserAchievementErrorCode.USER_ACHIEVEMENT_NOT_FOUND);
        }
        // 기록 삭제에 따른 UserAchievement 업데이트
        boolean isOnlyAnalysisOnSameDay = analysisQueryService.isOnlyAnalysisOnSameDay(user.getId(), analysis.getCreatedAt());
        userAchievement.subtractRecord(analysis.getPositiveRatio(), isOnlyAnalysisOnSameDay);

        // 기록 삭제 후 UserAchievement의 lastRecordedAt 업데이트
        Optional<LocalDateTime> earliestCreatedAtOfLastRecordedDate = analysisRepository.findFirstCreatedAtOfLastRecordedDate(user.getId());
        if (earliestCreatedAtOfLastRecordedDate.isPresent()) {
            userAchievement.updateLastRecordedDate(earliestCreatedAtOfLastRecordedDate.get().toLocalDate());
        } else {
            userAchievement.updateLastRecordedDate(null);
        }
    }

    // 해당 유저의 UserAchievement 속 currentBadgeCount 증가
    public void increaseCurrentBadgeCount(User user) {

        UserAchievement userAchievement = user.getUserAchievement();
        if (userAchievement == null) {
            throw new CustomException(UserAchievementErrorCode.USER_ACHIEVEMENT_NOT_FOUND);
        }
        userAchievement.increaseCurrentBadgeCount();
    }
}

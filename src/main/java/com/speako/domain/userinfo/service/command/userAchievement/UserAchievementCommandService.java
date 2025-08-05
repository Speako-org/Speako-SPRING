package com.speako.domain.userinfo.service.command.userAchievement;

import com.speako.domain.analysis.domain.Analysis;
import com.speako.domain.user.domain.User;
import com.speako.domain.userinfo.domain.UserAchievement;
import com.speako.domain.userinfo.exception.UserAchievementErrorCode;
import com.speako.domain.userinfo.repository.UserAchievementRepository;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAchievementCommandService {

    private final UserAchievementRepository userAchievementRepository;

    // 분석결과(Analysis)를 사용하여 사용자의 UserAchievement를 update
    public void updateUserAchievement(Analysis analysis) {

        User user = analysis.getTranscription().getUser();
        UserAchievement achievement = user.getUserAchievement();
        if (achievement == null) {
            throw new CustomException(UserAchievementErrorCode.USER_ACHIEVEMENT_NOT_FOUND);
        }

        // 오늘 첫 기록일 시, totalRecordedDays와 lastRecordedDate를 update
        if (achievement.getLastRecordedDate() == null || !achievement.getLastRecordedDate().isEqual(LocalDate.now())) {
            achievement.updateLastRecordedDate();
        }

        // 전체 평균 긍정표현 사용률 계산 및 저장
        float newAvgPositiveRatio = calculateNewAvgPositiveRatio(achievement, analysis.getPositiveRatio());
        achievement.updateAvgPositiveRatio(newAvgPositiveRatio);
    }

    // 전체 평균 긍정표현 사용률 계산 및 저장
    private float calculateNewAvgPositiveRatio(UserAchievement achievement, float newRatio) {

        float oldTotal = achievement.getAvgPositiveRatio() * achievement.getTotalRecordedDays();
        return (oldTotal + newRatio) / (achievement.getTotalRecordedDays() + 1);
    }
}

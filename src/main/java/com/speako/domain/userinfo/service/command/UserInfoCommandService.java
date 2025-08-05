package com.speako.domain.userinfo.service.command;

import com.speako.domain.analysis.domain.Analysis;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.exception.UserErrorCode;
import com.speako.domain.user.repository.UserRepository;
import com.speako.domain.userinfo.domain.UserAchievement;
import com.speako.domain.userinfo.dto.reqDTO.UpdateSelfCommentReqDTO;
import com.speako.domain.userinfo.dto.resDTO.userAchievement.UpdateSelfCommentResDTO;
import com.speako.domain.userinfo.exception.UserAchievementErrorCode;
import com.speako.domain.userinfo.service.command.monthlyStat.MonthlyStatCommandService;
import com.speako.domain.userinfo.service.command.userAchievement.UserAchievementCommandService;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserInfoCommandService {

    private final UserRepository userRepository;
    private final UserAchievementCommandService userAchievementCommandService;
    private final MonthlyStatCommandService monthlyStatCommandService;

    // 한줄소개 update
    public UpdateSelfCommentResDTO updateSelfComment(Long userId, UpdateSelfCommentReqDTO reqDTO) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 현재 유저의 UserAchievement 조회 후 selfComment 컬럼 업데이트
        UserAchievement achievement = user.getUserAchievement();
        if (achievement == null) {
            throw new CustomException(UserAchievementErrorCode.USER_ACHIEVEMENT_NOT_FOUND);
        }
        achievement.updateSelfComment(reqDTO.selfComment());

        return new UpdateSelfCommentResDTO(
                userId,
                reqDTO.selfComment()
        );
    }

    // userInfo update
    public void updateUserInfo(Analysis analysis) {

        // 각 테이블 데이터 update 로직 호출
        userAchievementCommandService.updateUserAchievement(analysis);
        monthlyStatCommandService.updateMonthlyStat(analysis);
    }
}

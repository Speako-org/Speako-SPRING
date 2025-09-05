package com.speako.domain.user.service.command;

import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.exception.UserBadgeErrorCode;
import com.speako.domain.challenge.repository.UserBadgeRepository;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.domain.enums.ImageType;
import com.speako.domain.user.dto.resDTO.UpdateUserNameResDTO;
import com.speako.domain.user.exception.UserErrorCode;
import com.speako.domain.user.repository.UserRepository;
import com.speako.domain.userinfo.dto.resDTO.UpdateMainUserBadgeResDTO;
import com.speako.domain.userinfo.dto.resDTO.userAchievement.UpdateImageTypeResDTO;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;

    // 대표 뱃지 update
    public UpdateMainUserBadgeResDTO updateMainUserBadge(Long userId, Long newMainBadgeId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        UserBadge newMain = userBadgeRepository.findByIdAndUserId(newMainBadgeId, userId)
                .orElseThrow(() -> new CustomException(UserBadgeErrorCode.USER_BADGE_NOT_FOUND));

        // 기존 대표 뱃지가 존재할 시 false 처리
        try {
            Optional<UserBadge> currentMainBadge = userBadgeRepository.findByUserIdAndIsMain(userId);
            currentMainBadge.ifPresent(current -> current.updateIsMain(false));
        } catch (IncorrectResultSizeDataAccessException e) {
            // true가 2개 이상인, 즉 유저의 대표뱃지가 여러 개인 경우 에러 발생 (DB 레벨에서의 unique 처리 혹은 서비스 레벨에서의 동시성 관리 등은 추후 고려)
            throw new CustomException(UserBadgeErrorCode.DUPLICATE_MAIN_BADGE_FOUND);
        }

        // 변경될 대표 뱃지를 true 처리
        newMain.updateIsMain(true);
        String updatedBadgeName = newMain.getBadge().getName();

        return new UpdateMainUserBadgeResDTO(
                userId,
                newMainBadgeId,
                updatedBadgeName
        );
    }

    // 프로필 이미지 변경
    public UpdateImageTypeResDTO updateProfileImage(Long userId, String newImageName) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 입력된 newImageName과 일치하는 enum 조회 후, user에 할당
        ImageType imageType = ImageType.fromDisplayName(newImageName);
        user.updateImageType(imageType);

        return new UpdateImageTypeResDTO(
                userId,
                imageType.getImageUrl()
        );
    }

    public UpdateUserNameResDTO updateUserName(Long userId, String newUserName) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        user.updateUserName(newUserName);

        return new UpdateUserNameResDTO(
                user.getId(),
                user.getUsername()
        );
    }
}

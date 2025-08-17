package com.speako.domain.user.service.command;

import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.exception.UserBadgeErrorCode;
import com.speako.domain.challenge.repository.UserBadgeRepository;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.domain.enums.ImageType;
import com.speako.domain.user.exception.UserErrorCode;
import com.speako.domain.user.repository.UserRepository;
import com.speako.domain.userinfo.dto.resDTO.UpdateMainUserBadgeResDTO;
import com.speako.domain.userinfo.dto.resDTO.userAchievement.UpdateImageTypeResDTO;
import com.speako.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;

    // 대표 뱃지 update
    public UpdateMainUserBadgeResDTO UpdateMainUserBadge(Long userId, Long currentMainBadgeId, Long newMainBadgeId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 수정 전 대표 뱃지 false 처리
        if (currentMainBadgeId != null) {
            UserBadge current = userBadgeRepository.findById(currentMainBadgeId)
                    .orElseThrow(() -> new CustomException(UserBadgeErrorCode.USER_BADGE_NOT_FOUND));
            if (!current.getUser().getId().equals(userId)) {
                throw new CustomException(UserBadgeErrorCode.USER_BADGE_NOT_OWNED_BY_USER);
            }
            current.updateIsMain(false);
        }

        String updatedBadgeName = null;
        // 변경될 대표 뱃지를 true 처리
        if (newMainBadgeId != null) {
            UserBadge newMain = userBadgeRepository.findById(newMainBadgeId)
                    .orElseThrow(() -> new CustomException(UserBadgeErrorCode.USER_BADGE_NOT_FOUND));
            if (!newMain.getUser().getId().equals(userId)) {
                throw new CustomException(UserBadgeErrorCode.USER_BADGE_NOT_OWNED_BY_USER);
            }
            newMain.updateIsMain(true);
            updatedBadgeName = newMain.getBadge().getName();
        }

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
}

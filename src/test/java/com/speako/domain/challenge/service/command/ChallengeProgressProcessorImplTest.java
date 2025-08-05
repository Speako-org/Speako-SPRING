package com.speako.domain.challenge.service.command;

import com.speako.domain.analysis.domain.Analysis;
import com.speako.domain.challenge.domain.Badge;
import com.speako.domain.challenge.domain.Challenge;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.domain.UserChallenge;
import com.speako.domain.challenge.repository.BadgeRepository;
import com.speako.domain.challenge.repository.ChallengeRepository;
import com.speako.domain.challenge.repository.UserBadgeRepository;
import com.speako.domain.challenge.repository.UserChallengeRepository;
import com.speako.domain.transcription.domain.Transcription;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.domain.enums.UserGender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeProgressProcessorImplTest {

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @Mock
    private UserChallengeRepository userChallengeRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ChallengeProgressProcessorImpl challengeProgressProcessor;

    private User mockUser;
    private Challenge continuousChallenge, positiveChallenge, negativeChallenge, recordCountChallenge;
    private Analysis mockAnalysis;

    @BeforeEach
    void setup() {
        mockUser = User.builder()
                .email("test@test.com")
                .username("test")
                .age(25)
                .gender(UserGender.MALE)
                .build();

        continuousChallenge = Challenge.builder()
                .id(1L)
                .name("연속 기록")
                .level(1)
                .requiredAmount(7)
                .build();


        positiveChallenge = Challenge.builder()
                .id(2L)
                .name("긍정 표현 달성")
                .level(1)
                .requiredAmount(10)
                .build();

        negativeChallenge = Challenge.builder()
                .id(3L)
                .name("부정 감소")
                .level(1)
                .requiredAmount(5)
                .build();

        recordCountChallenge = Challenge.builder()
                .id(4L)
                .name("기록 수 달성")
                .level(1)
                .requiredAmount(20)
                .build();

        mockAnalysis = Analysis.builder()
                .id(1L)
                .transcription(mock(Transcription.class))
                .positiveRatio(0.7f)
                .negativeRatio(0.1f)
                .neutralRatio(0.2f)
                .build();
    }

    @Test
    @DisplayName("연속 기록 챌린지 타입")
    void ContinuousRecordType() {
        // given
        UserChallenge userChallenge = createUserChallenge(continuousChallenge, 1);

        // when
        boolean result = challengeProgressProcessor.processChallenge(userChallenge, mockAnalysis);

        // then
        assertThat(result).isTrue();
        assertThat(userChallenge.getAmount()).isEqualTo(2);
        assertThat(userChallenge.getLastRecordDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("긍정 상승 챌린지 성공")
    void PositiveExpressionTypeSuccess() {
        // given
        UserChallenge userChallenge = createUserChallenge(positiveChallenge, 1);
        Analysis analysis = createAnalysis(0.7f, 0.2f, 0.1f);

        // when
        boolean result = challengeProgressProcessor.processChallenge(userChallenge, analysis);

        // then
        assertThat(result).isTrue();
        assertThat(userChallenge.getAmount()).isEqualTo(2);
        assertThat(userChallenge.getLastRecordDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("긍정 상승 챌린지 실패")
    void PositiveExpressionTypeFail() {
        // given
        UserChallenge userChallenge = createUserChallenge(positiveChallenge, 1);
        Analysis analysis = createAnalysis(0.5f, 0.4f, 0.1f);

        // when
        boolean result = challengeProgressProcessor.processChallenge(userChallenge, analysis);

        // then
        assertThat(result).isFalse();
        assertThat(userChallenge.getAmount()).isEqualTo(1);
        assertThat(userChallenge.getLastRecordDate()).isNull();
    }

    @Test
    @DisplayName("부정 감소 챌린지 타입 성공")
    void NegativeReductionTypeSuccess() {
        // given
        UserChallenge userChallenge = createUserChallenge(negativeChallenge, 1);
        Analysis analysis = createAnalysis(0.7f, 0.2f, 0.2f);

        // when
        boolean result = challengeProgressProcessor.processChallenge(userChallenge, analysis);

        // then
        assertThat(result).isTrue();
        assertThat(userChallenge.getAmount()).isEqualTo(2);
        assertThat(userChallenge.getLastRecordDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("부정 감소 챌린지 타입 실패")
    void NegativeReductionTypeFail() {
        // given
        UserChallenge userChallenge = createUserChallenge(negativeChallenge, 1);
        Analysis analysis = createAnalysis(0.6f, 0.3f, 0.1f);

        // when
        boolean result = challengeProgressProcessor.processChallenge(userChallenge, analysis);

        // then
        assertThat(result).isFalse();
        assertThat(userChallenge.getAmount()).isEqualTo(1);
        assertThat(userChallenge.getLastRecordDate()).isNull();
    }

    @Test
    @DisplayName("기록 상승 챌린지 타입")
    void RecordType() {
        // given
        UserChallenge userChallenge = createUserChallenge(positiveChallenge, 1);

        // when
        boolean result = challengeProgressProcessor.processChallenge(userChallenge, mockAnalysis);

        // then
        assertThat(result).isTrue();
        assertThat(userChallenge.getAmount()).isEqualTo(2);
        assertThat(userChallenge.getLastRecordDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("챌린지 완료 처리 테스트")
    void set_CompletedChallenge_isActivateFalse() throws Exception{
        // given
        UserChallenge userChallenge = createUserChallenge(continuousChallenge, 7);

        // when
        challengeProgressProcessor.completeChallenge(userChallenge);

        // then
        assertThat(userChallenge.getIsActive()).isFalse();
        verify(userChallengeRepository).save(userChallenge);
    }

    @Test
    @DisplayName("다음 레벨 챌린지 생성 테스트")
    void can_createNextLevelChallenge() throws Exception{
        // given
        UserChallenge completedUserChallenge = createUserChallenge(continuousChallenge, 7);
        Challenge nextLevelChallenge = Challenge.builder()
                .id(5L)
                .name("연속 기록")
                .level(2)
                .requiredAmount(14)
                .build();

        when(challengeRepository.findByNameAndLevel("연속 기록", 2))
                .thenReturn(Optional.ofNullable(nextLevelChallenge));

        // when
        challengeProgressProcessor.createNextLevelChallenge(completedUserChallenge);

        // then
        verify(userChallengeRepository).save(any(UserChallenge.class));
    }

    @Test
    @DisplayName("다음 레벨 챌린지가 존재하지 않는 경우")
    void nextLevel_Not_Exist() {
        // given
        UserChallenge completedChallenge = createUserChallenge(continuousChallenge, 7);
        when(challengeRepository.findByNameAndLevel("연속 기록", 2))
                .thenReturn(Optional.empty());

        // when
        challengeProgressProcessor.createNextLevelChallenge(completedChallenge);

        // then
        verify(userChallengeRepository, never()).save(any(UserChallenge.class));
    }

    @Test
    @DisplayName("사용자가 해당 뱃지를 소유하지 않았으면 새로운 뱃지 생성")
    void createNewUserBadge_When_UserDoesNotOwnBadge() {
        // given
        UserChallenge userChallenge = createUserChallenge(continuousChallenge, 7);
        Badge mockBadge = mock(Badge.class);

        when(badgeRepository.findAll()).thenReturn(List.of(mockBadge));
        when(userBadgeRepository.existsByUserAndBadge(mockUser, mockBadge)).thenReturn(false);

        // when
        challengeProgressProcessor.createBadge(userChallenge);

        // then
        verify(userBadgeRepository).existsByUserAndBadge(mockUser, mockBadge);
        verify(userBadgeRepository).save(any(UserBadge.class));
    }

    @Test
    @DisplayName("사용자가 이미 해당 뱃지를 소유했으면 중복 생성하지 않는다")
    void notCreateBadge_When_UserOwnsBadge() {
        // given
        UserChallenge userChallenge = createUserChallenge(continuousChallenge, 7);
        Badge mockBadge = mock(Badge.class);

        when(badgeRepository.findAll()).thenReturn(List.of(mockBadge));
        when(userBadgeRepository.existsByUserAndBadge(mockUser, mockBadge)).thenReturn(true);

        // when
        challengeProgressProcessor.createBadge(userChallenge);

        // then
        verify(userBadgeRepository, never()).save(any(UserBadge.class));
    }

    @Test
    @DisplayName("올바른 뱃지 인덱스를 계산하여 뱃지를 가져온다")
    void should_GetCorrectBadgeByIndex_When_CreatingBadge() {
        // given
        UserChallenge userChallenge = createUserChallenge(continuousChallenge, 7);
        Badge badge = mock(Badge.class);
        Badge targetBadge = mock(Badge.class);

        when(badgeRepository.findAll()).thenReturn(List.of(
                targetBadge, badge, badge, badge, badge
        ));
        when(userBadgeRepository.existsByUserAndBadge(mockUser, targetBadge)).thenReturn(false);

        // when
        challengeProgressProcessor.createBadge(userChallenge);

        // then
        verify(userBadgeRepository).existsByUserAndBadge(mockUser, targetBadge);
        verify(userBadgeRepository).save(any(UserBadge.class));
    }


    private UserChallenge createUserChallenge(Challenge challenge, int amount) {
        return UserChallenge.builder()
                .user(mockUser)
                .challenge(challenge)
                .amount(amount)
                .isActive(true)
                .build();
    }

    private Analysis createAnalysis(float positiveRatio, float negativeRatio, float neutralRatio) {
        return Analysis.builder()
                .id(1L)
                .transcription(mock(Transcription.class))
                .positiveRatio(positiveRatio)
                .negativeRatio(negativeRatio)
                .neutralRatio(neutralRatio)
                .build();
    }
}
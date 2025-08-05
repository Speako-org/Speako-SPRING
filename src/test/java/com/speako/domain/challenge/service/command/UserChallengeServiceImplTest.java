package com.speako.domain.challenge.service.command;

import com.speako.domain.analysis.domain.Analysis;
import com.speako.domain.challenge.domain.Challenge;
import com.speako.domain.challenge.domain.UserChallenge;
import com.speako.domain.challenge.repository.ChallengeRepository;
import com.speako.domain.challenge.repository.UserChallengeRepository;
import com.speako.domain.transcription.domain.Transcription;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.domain.enums.UserGender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserChallengeServiceImplTest {

    @Mock
    private UserChallengeRepository userChallengeRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private ChallengeProgressProcessor progressProcessor;

    @InjectMocks
    private UserChallengeServiceImpl userChallengeService;

    private User mockUser;
    private List<Challenge> mockChallenges;
    private Analysis mockAnalysis;
    private Transcription mockTranscription;


    @BeforeEach
    void setup() {
        mockUser = User.builder()
                .email("test@test.com")
                .username("test")
                .age(25)
                .gender(UserGender.MALE)
                .build();

        mockChallenges = List.of(
                Challenge.builder()
                        .id(1L)
                        .name("연속 기록")
                        .level(1)
                        .requiredAmount(7)
                        .build(),
                Challenge.builder()
                        .id(2L)
                        .name("긍정 표현 달성")
                        .level(1)
                        .requiredAmount(10)
                        .build(),
                Challenge.builder()
                        .id(3L)
                        .name("부정 감소")
                        .level(1)
                        .requiredAmount(5)
                        .build(),
                Challenge.builder()
                        .id(4L)
                        .name("기록 수 달성")
                        .level(1)
                        .requiredAmount(20)
                        .build());

        mockTranscription = mock(Transcription.class);

        mockAnalysis = Analysis.builder()
                .id(1L)
                .transcription(mockTranscription)
                .positiveRatio(0.7f)
                .negativeRatio(0.1f)
                .neutralRatio(0.2f)
                .build();
    }

    @Test
    @DisplayName("사용자 회원가입 시 챌린지 생성 확인")
    void userSignUpWithChallenge() {
        // given

        // findByNameAndLevel -> mockChallenge 반환
        when(challengeRepository.findByNameAndLevel("연속 기록", 1))
                .thenReturn(Optional.ofNullable(mockChallenges.get(0)));
        when(challengeRepository.findByNameAndLevel("긍정 표현 달성", 1))
                .thenReturn(Optional.ofNullable(mockChallenges.get(1)));
        when(challengeRepository.findByNameAndLevel("부정 감소", 1))
                .thenReturn(Optional.ofNullable(mockChallenges.get(2)));
        when(challengeRepository.findByNameAndLevel("기록 수 달성", 1))
                .thenReturn(Optional.ofNullable(mockChallenges.get(3)));

        // when
        userChallengeService.initializeUserChallenges(mockUser);

        // then
        // 각 챌린지 타입 별로 호출이 이루어 졌는지 확인
        verify(challengeRepository).findByNameAndLevel("연속 기록", 1);
        verify(challengeRepository).findByNameAndLevel("긍정 표현 달성", 1);
        verify(challengeRepository).findByNameAndLevel("부정 감소", 1);
        verify(challengeRepository).findByNameAndLevel("기록 수 달성", 1);
    }


    @Test
    @DisplayName("활성 챌린지가 존재하지 않는 경우")
    void noActivateChallenge() throws Exception {
        // given
        when(userChallengeRepository.findByUserAndIsActiveTrue(mockUser)).thenReturn(List.of());

        // when
        userChallengeService.updateChallengeProgress(mockUser, mockAnalysis);

        // then
        verify(progressProcessor, never()).processChallenge(any(), any());
        verify(progressProcessor, never()).completeChallenge(any());
        verify(progressProcessor, never()).createBadge(any());
        verify(progressProcessor, never()).createNextLevelChallenge(any());
    }

    @Test
    @DisplayName("updated가 false이면 하위 메서드가 실행되지 않는다.")
    void NotCompleted_When_ProgressNotUpdated() throws Exception {
        // given
        UserChallenge mockUserChallenge = createMockUserChallenge(mockChallenges.get(0), 1);
        when(userChallengeRepository.findByUserAndIsActiveTrue(mockUser))
                .thenReturn(List.of(mockUserChallenge));
        when(progressProcessor.processChallenge(mockUserChallenge, mockAnalysis))
                .thenReturn(false);

        // when
        userChallengeService.updateChallengeProgress(mockUser, mockAnalysis);

        // then
        verify(progressProcessor).processChallenge(mockUserChallenge, mockAnalysis);
        verify(progressProcessor, never()).completeChallenge(any());
        verify(progressProcessor, never()).createBadge(any());
        verify(progressProcessor, never()).createNextLevelChallenge(any());
    }

    @Test
    @DisplayName("userChallenge.isAchieved()가 false이면 하위 메서드가 실행되지 않는다.")
    void NotCompleted_When_ProgressNotAchieved() throws Exception {
        // given
        //requiredAmount = 7 따라서 미달성
        UserChallenge mockUserChallenge = createMockUserChallenge(mockChallenges.get(0), 1);
        when(userChallengeRepository.findByUserAndIsActiveTrue(mockUser))
                .thenReturn(List.of(mockUserChallenge));
        when(progressProcessor.processChallenge(mockUserChallenge, mockAnalysis))
                .thenReturn(true);

        // when
        userChallengeService.updateChallengeProgress(mockUser, mockAnalysis);

        // then
        verify(progressProcessor).processChallenge(mockUserChallenge, mockAnalysis);
        verify(progressProcessor, never()).completeChallenge(any());
        verify(progressProcessor, never()).createBadge(any());
        verify(progressProcessor, never()).createNextLevelChallenge(any());
    }

    @Test
    @DisplayName("조건 만족시 완료 처리, 뱃지 발급, 다음 챌린지 생성이 일어남")
    void completeScenario() throws Exception {
        // given
        UserChallenge mockUserChallenge = createMockUserChallenge(mockChallenges.get(0), 7);
        when(userChallengeRepository.findByUserAndIsActiveTrue(mockUser))
                .thenReturn(List.of(mockUserChallenge));
        when(progressProcessor.processChallenge(mockUserChallenge, mockAnalysis))
                .thenReturn(true);

        // when
        userChallengeService.updateChallengeProgress(mockUser, mockAnalysis);

        // then
        verify(progressProcessor).processChallenge(mockUserChallenge, mockAnalysis);
        verify(progressProcessor).completeChallenge(any());
        verify(progressProcessor).createBadge(any());
        verify(progressProcessor).createNextLevelChallenge(any());
    }

    @Test
    @DisplayName("챌린지 진행도가 전부 정상적으로 업데이트 되는지 테스트")
    void updatedAllActivateChallenge() throws Exception {
        // given
        UserChallenge mockUserChallenge1 = createMockUserChallenge(mockChallenges.get(0), 1);
        UserChallenge mockUserChallenge2 = createMockUserChallenge(mockChallenges.get(1), 1);
        UserChallenge mockUserChallenge3 = createMockUserChallenge(mockChallenges.get(2), 1);
        UserChallenge mockUserChallenge4 = createMockUserChallenge(mockChallenges.get(3), 1);

        when(userChallengeRepository.findByUserAndIsActiveTrue(mockUser))
                .thenReturn(List.of(
                        mockUserChallenge1,
                        mockUserChallenge2,
                        mockUserChallenge3,
                        mockUserChallenge4
                ));
        when(progressProcessor.processChallenge(mockUserChallenge1, mockAnalysis)).thenReturn(true);
        when(progressProcessor.processChallenge(mockUserChallenge2, mockAnalysis)).thenReturn(true);
        when(progressProcessor.processChallenge(mockUserChallenge3, mockAnalysis)).thenReturn(true);
        when(progressProcessor.processChallenge(mockUserChallenge4, mockAnalysis)).thenReturn(true);

        // when
        userChallengeService.updateChallengeProgress(mockUser, mockAnalysis);

        // then
        verify(progressProcessor).processChallenge(mockUserChallenge1, mockAnalysis);
        verify(progressProcessor).processChallenge(mockUserChallenge2, mockAnalysis);
        verify(progressProcessor).processChallenge(mockUserChallenge3, mockAnalysis);
        verify(progressProcessor).processChallenge(mockUserChallenge4, mockAnalysis);
    }


    private UserChallenge createMockUserChallenge(Challenge challenge, int currentAmount) {
        return UserChallenge.builder()
                .user(mockUser)
                .challenge(challenge)
                .amount(currentAmount)
                .isActive(true)
                .build();
    }
}
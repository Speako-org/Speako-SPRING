package com.speako.domain.article;

import com.speako.domain.article.domain.Article;
import com.speako.domain.article.domain.Like;
import com.speako.domain.article.repository.ArticleRepository;
import com.speako.domain.article.repository.LikeRepository;
import com.speako.domain.article.service.command.ArticleLikeCommandService;
import com.speako.domain.challenge.domain.Badge;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.domain.enums.ImageType;
import com.speako.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LikeCommandServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private ArticleLikeCommandService articleLikeCommandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("좋아요 생성")
    void createLikeSuccess() {
        User user = User.builder()
                .id(1L)
                .username("testUser")
                .imageType(ImageType.DEFAULT)
                .build();

        Badge badge = Badge.builder()
                .id(1L)
                .name("Test Badge")
                .description("설명")
                .level(1)
                .build();

        UserBadge userBadge = UserBadge.builder()
                .id(1L)
                .user(user)
                .badge(badge)
                .isMain(true)
                .build();

        Article article = Article.builder()
                .id(1L)
                .content("Content 1")
                .user(user)
                .userBadge(userBadge)
                .likedNum(0)
                .build();

        Long userId = user.getId();
        Long articleId = article.getId();

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(likeRepository.existsByUserIdAndArticleId(userId, articleId)).thenReturn(false);

        articleLikeCommandService.likeArticle(articleId, userId);

        assertThat(article.getLikedNum()).isEqualTo(1);
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    void unlikeArticleSuccess() {

        User user = User.builder()
                .id(1L)
                .username("testUser")
                .imageType(ImageType.DEFAULT)
                .build();

        Badge badge = Badge.builder()
                .id(1L)
                .name("Test Badge")
                .description("설명")
                .level(1)
                .build();

        UserBadge userBadge = UserBadge.builder()
                .id(1L)
                .user(user)
                .badge(badge)
                .isMain(true)
                .build();

        Article article = Article.builder()
                .id(1L)
                .content("Content 1")
                .user(user)
                .userBadge(userBadge)
                .likedNum(1)
                .build();

        Like like = Like.builder()
                .id(1L)
                .article(article)
                .user(user)
                .build();

        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(likeRepository.existsByUserIdAndArticleId(1L, 1L)).thenReturn(true);

        articleLikeCommandService.unlikeArticle(1L, 1L);

        assertThat(article.getLikedNum()).isEqualTo(0);
        verify(likeRepository, times(1)).deleteByUserIdAndArticleId(1L, 1L);
    }
}

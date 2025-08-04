package com.speako.domain.article;

import com.speako.domain.article.domain.Article;
import com.speako.domain.article.domain.Like;
import com.speako.domain.article.exception.ArticleErrorCode;
import com.speako.domain.article.repository.ArticleRepository;
import com.speako.domain.article.repository.LikeRepository;
import com.speako.domain.article.service.command.ArticleLikeCommandService;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.repository.UserRepository;
import com.speako.global.apiPayload.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class likeCommandServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private ArticleLikeCommandService articleLikeCommandService;

    private Article article;
    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        article = Article.builder()
                .id(1L)
                .likedNum(0)
                .build();
        user = User.builder()
                .id(1L)
                .build();
    }

    @Test
    void likeArticle() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(likeRepository.existsByUserIdAndArticleId(1L, 1L)).thenReturn(false);

        articleLikeCommandService.likeArticle(1L, 1L);

        assertThat(article.getLikedNum()).isEqualTo(1);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void unlikeArticle() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(likeRepository.existsByUserIdAndArticleId(1L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> articleLikeCommandService.unlikeArticle(1L, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ArticleErrorCode.LIKE_NOT_FOUND.getMessage());
    }

}

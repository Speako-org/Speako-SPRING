package com.speako.domain.article;

import com.speako.domain.article.domain.Article;
import com.speako.domain.article.dto.resDTO.GetArticleResDTO;
import com.speako.domain.article.repository.ArticleRepository;
import com.speako.domain.article.service.query.ArticleQueryService;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArticleQueryServiceTest {
    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleQueryService articleQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("전체 게시글 조회 성공")
    void getAllArticlesSuccess() {
        // given
        User user = mock(User.class);
        UserBadge userBadge = mock(UserBadge.class);
        when(user.getUserBadge()).thenReturn(userBadge);

        Article article1 = mock(Article.class);
        Article article2 = mock(Article.class);

        when(article1.getUser()).thenReturn(user);
        when(article2.getUser()).thenReturn(user);

        List<Article> mockArticles = Arrays.asList(article1, article2);
        when(articleRepository.findAllByOrderByCreatedAtDesc()).thenReturn(mockArticles);

        // when
        List<GetArticleResDTO> result = articleQueryService.getAllArticles();

        // then
        assertThat(result).hasSize(2);
        verify(articleRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("게시글 단건 조회 성공")
    void getArticleSuccess() {
        // given
        Long articleId = 1L;

        User user = mock(User.class);
        UserBadge userBadge = mock(UserBadge.class);
        when(user.getUserBadge()).thenReturn(userBadge);

        Article article = mock(Article.class);
        when(article.getUser()).thenReturn(user);

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        // when
        GetArticleResDTO result = articleQueryService.getArticle(articleId);

        // then
        assertThat(result).isNotNull();
        verify(articleRepository, times(1)).findById(articleId);
    }
}

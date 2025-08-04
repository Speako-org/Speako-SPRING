package com.speako.domain.article;

import com.speako.domain.article.domain.Article;
import com.speako.domain.article.dto.reqDTO.ArticleContentReqDTO;
import com.speako.domain.article.repository.ArticleRepository;
import com.speako.domain.article.service.command.ArticleCommandService;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.repository.UserBadgeRepository;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class ArticleCommandServiceTest {
    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @InjectMocks
    private ArticleCommandService articleCommandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void createArticleSuccess() {
        Long userId = 1L;
        Long userBadgeId = 10L;
        String content = "테스트 글";

        User user = mock(User.class);
        UserBadge userBadge = mock(UserBadge.class);

        ArticleContentReqDTO dto = new ArticleContentReqDTO(userBadgeId, content);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userBadgeRepository.findById(userBadgeId)).thenReturn(Optional.of(userBadge));

        articleCommandService.createArticle(userId, dto);

        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deleteArticleSuccess() {
        Long userId = 1L;
        Long articleId = 100L;

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        Article article = mock(Article.class);
        when(article.getUser()).thenReturn(user);

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        articleCommandService.deleteArticle(userId, articleId);

        verify(articleRepository, times(1)).delete(article);
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updateArticleSuccess() {
        Long userId = 1L;
        Long articleId = 100L;
        Long badgeId = 10L;

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        Article article = mock(Article.class);
        when(article.getUser()).thenReturn(user);

        UserBadge badge = mock(UserBadge.class);
        ArticleContentReqDTO dto = new ArticleContentReqDTO(badgeId, "수정된 내용");

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(userBadgeRepository.findById(badgeId)).thenReturn(Optional.of(badge));

        articleCommandService.updateArticle(userId, articleId, dto);

        verify(article).updateArticle("수정된 내용", badge);
    }

}

package com.speako.domain.article;

import com.speako.domain.article.domain.Article;
import com.speako.domain.article.dto.reqDTO.CursorPageRequest;
import com.speako.domain.article.dto.resDTO.CursorPageResDTO;
import com.speako.domain.article.dto.resDTO.GetArticleResDTO;
import com.speako.domain.article.repository.ArticleRepository;
import com.speako.domain.article.repository.CommentRepository;
import com.speako.domain.article.service.query.ArticleQueryService;
import com.speako.domain.challenge.domain.Badge;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.repository.UserBadgeRepository;
import com.speako.domain.user.domain.User;
import com.speako.domain.user.domain.enums.ImageType;
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
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

public class ArticleQueryServiceTest {
    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ArticleQueryService articleQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("전체 게시글 조회 성공")
    void getAllArticlesSuccess() {

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


        Article article1 = Article.builder()
                .id(1L)
                .content("Content 1")
                .user(user)
                .userBadge(userBadge)
                .likedNum(10)
                .build();

        Article article2 = Article.builder()
                .id(1L)
                .content("Content 2")
                .user(user)
                .userBadge(userBadge)
                .likedNum(10)
                .build();

        List<Article> mockArticles = Arrays.asList(article1, article2);

        when(userBadgeRepository.findByUserIdAndIsMain(anyLong()))
                .thenReturn(Optional.of(userBadge));

        when(articleRepository.findTopNOrderByIdDesc(any()))
                .thenReturn(mockArticles);

        when(commentRepository.countByArticleIdIn(anyList()))
                .thenReturn(Arrays.asList(
                        new Object[]{1L, 2L},
                        new Object[]{2L, 3L}
                ));

        when(commentRepository.countByArticleId(anyLong()))
                .thenReturn(2);

        CursorPageRequest pageRequest = new CursorPageRequest(null, 10);
        CursorPageResDTO<GetArticleResDTO> result = articleQueryService.getAllArticles(pageRequest);

        assertThat(result.content()).hasSize(2);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursorId()).isEqualTo(1L);
        verify(articleRepository, times(1)).findTopNOrderByIdDesc(any());
        verify(userBadgeRepository, atLeastOnce()).findByUserIdAndIsMain(anyLong());
    }

    @Test
    @DisplayName("게시글 단건 조회 성공")
    void getArticleSuccess() {
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
                .content("Single Content")
                .user(user)
                .userBadge(userBadge)
                .likedNum(0)
                .build();

        when(userBadgeRepository.findByUserIdAndIsMain(anyLong()))
                .thenReturn(Optional.of(userBadge));

        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

        GetArticleResDTO result = articleQueryService.getArticle(1L);

        assertThat(result).isNotNull();
        verify(articleRepository, times(1)).findById(1L);
    }
}

package com.speako.domain.article;

import com.speako.domain.article.domain.Comment;
import com.speako.domain.article.dto.reqDTO.CursorPageRequest;
import com.speako.domain.article.dto.resDTO.CommentResDTO;
import com.speako.domain.article.repository.CommentRepository;
import com.speako.domain.article.service.query.CommentQueryService;
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
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentQueryServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @InjectMocks
    private CommentQueryService commentQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("특정 게시글 댓글 조회 성공")
    void getCommentsByArticleIdSuccess() {
        Long articleId = 1L;
        Long cursorId = null;
        int size = 10;

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

        Comment comment1 = Comment.builder()
                .id(1L)
                .user(user)
                .content("댓글 내용 1")
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .user(user)
                .content("댓글 내용 2")
                .build();

        List<Comment> mockComments = List.of(comment1, comment2);

        when(commentRepository.findByArticleIdWithCursor(eq(articleId), any(), any(PageRequest.class)))
                .thenReturn(mockComments);

        when(userBadgeRepository.findByUserIdAndIsMain(user.getId()))
                .thenReturn(Optional.of(userBadge));

        List<CommentResDTO> result = commentQueryService.getCommentsByArticleId(
                articleId,
                new CursorPageRequest(cursorId, size)
        );

        assertThat(result).hasSize(2);
        verify(commentRepository, times(1)).findByArticleIdWithCursor(eq(articleId), any(), any(PageRequest.class));
        verify(userBadgeRepository, atLeastOnce()).findByUserIdAndIsMain(user.getId());
    }
}

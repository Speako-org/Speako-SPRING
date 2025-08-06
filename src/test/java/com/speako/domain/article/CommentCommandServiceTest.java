package com.speako.domain.article;

import com.speako.domain.article.domain.Article;
import com.speako.domain.article.domain.Comment;
import com.speako.domain.article.dto.reqDTO.CommentReqDTO;
import com.speako.domain.article.repository.ArticleRepository;
import com.speako.domain.article.repository.CommentRepository;
import com.speako.domain.article.service.command.CommentCommandService;
import com.speako.domain.challenge.domain.Badge;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.repository.UserBadgeRepository;
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

import static org.mockito.Mockito.*;

public class CommentCommandServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private CommentCommandService commentCommandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("댓글 생성 성공")
    void createCommentSuccess() {
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
                .user(user)
                .content("테스트 게시글")
                .likedNum(0)
                .userBadge(userBadge)
                .build();

        Long userId = user.getId();
        Long userBadgeId = userBadge.getId();
        Long articleId = article.getId();
        String commentContent = "댓글 내용";

        CommentReqDTO dto = new CommentReqDTO(userBadgeId, commentContent);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userBadgeRepository.findById(userBadgeId)).thenReturn(Optional.of(userBadge));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        commentCommandService.createComment(userId, dto);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteCommentSuccess() {
        User user = User.builder()
                .id(1L)
                .username("testUser")
                .imageType(ImageType.DEFAULT)
                .build();

        Comment comment = Comment.builder()
                .id(100L)
                .user(user)
                .content("삭제할 댓글")
                .build();

        Long userId = user.getId();
        Long commentId = comment.getId();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentCommandService.deleteComment(commentId, userId);

        verify(commentRepository, times(1)).delete(comment);
    }

}

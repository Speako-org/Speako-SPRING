package com.speako.domain.article.service.query;

import com.speako.domain.article.converter.CommentConverter;
import com.speako.domain.article.domain.Comment;
import com.speako.domain.article.dto.reqDTO.CursorPageRequest;
import com.speako.domain.article.dto.resDTO.CommentResDTO;
import com.speako.domain.article.repository.CommentRepository;
import com.speako.domain.challenge.domain.Badge;
import com.speako.domain.challenge.domain.UserBadge;
import com.speako.domain.challenge.repository.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryService {
    private final CommentRepository commentRepository;
    private final UserBadgeRepository userBadgeRepository;

    public List<CommentResDTO> getCommentsByArticleId(Long articleId, CursorPageRequest pageRequest) {
        Long lastCommentId = pageRequest.cursorId();
        int size = pageRequest.size();

        Pageable pageable = PageRequest.of(0, size);
        List<Comment> comments = commentRepository.findByArticleIdWithCursor(articleId, lastCommentId, pageable);

        List<Long> userIds = comments.stream()
                .map(comment -> comment.getUser().getId())
                .distinct()
                .toList();

        Map<Long, Badge> mainBadgeMap = new java.util.HashMap<>();

        for(Long userId : userIds){
            Badge badge = userBadgeRepository.findByUserIdAndIsMain(userId)
                    .map(UserBadge::getBadge)
                    .orElse(null);
            mainBadgeMap.put(userId, badge);
        }


        return comments.stream()
                .map(comment -> {
                    Long userId = comment.getUser().getId();
                    Badge badge = mainBadgeMap.get(userId);
                    return CommentConverter.toDTO(comment, badge);
                })
                .toList();
    }
}

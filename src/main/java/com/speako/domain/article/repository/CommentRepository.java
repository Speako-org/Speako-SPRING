package com.speako.domain.article.repository;

import com.speako.domain.article.domain.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.article.id = :articleId ORDER BY c.createdAt")
    List<Comment> findAllByArticleIdWithUser(@Param("articleId") Long articleId);

    @Query("""
        SELECT c FROM Comment c
        JOIN FETCH c.user u
        WHERE c.article.id = :articleId
        AND (:lastCommentId IS NULL OR c.id < :lastCommentId)
        ORDER BY c.id DESC
    """)
    List<Comment> findByArticleIdWithCursor(
            @Param("articleId") Long articleId,
            @Param("lastCommentId") Long lastCommentId,
            Pageable pageable
    );

    int countByArticleId(Long articleId);

    @Query("""
        SELECT c.article.id, COUNT(c)
        FROM Comment c
        WHERE c.article.id IN :articleIds
        GROUP BY c.article.id
    """)
    Map<Long, Long> countByArticleIdIn(@Param("articleIds") List<Long> articleIds);
}

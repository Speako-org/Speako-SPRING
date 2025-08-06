package com.speako.domain.article.repository;

import com.speako.domain.article.domain.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
        SELECT c FROM Comment c
        JOIN FETCH c.user u
        WHERE c.article.id = :articleId
        AND (:lastCommentId IS NULL OR c.id > :lastCommentId)
        ORDER BY c.id ASC
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
    List<Object[]> countByArticleIdIn(List<Long> articleIds);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.article.id = :articleId")
    void deleteByArticleId(@Param("articleId") Long articleId);
}

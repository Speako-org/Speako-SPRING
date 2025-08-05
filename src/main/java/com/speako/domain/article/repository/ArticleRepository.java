package com.speako.domain.article.repository;

import com.speako.domain.article.domain.Article;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findAllByOrderByCreatedAtDesc();

    @Query("""
        SELECT a FROM Article a
        JOIN FETCH a.user
        WHERE (:cursorCreatedAt IS NULL OR a.createdAt < :cursorCreatedAt)
        ORDER BY a.createdAt DESC
    """)
    List<Article> findByCursor(
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            Pageable pageable
    );

}

package com.speako.domain.article.repository;

import com.speako.domain.article.domain.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("SELECT a FROM Article a ORDER BY a.id DESC")
    List<Article> findTopNOrderByIdDesc(Pageable pageable);


    @Query("SELECT a FROM Article a WHERE a.id < :lastArticleId ORDER BY a.id DESC")
    List<Article> findByIdLessThanOrderByIdDesc(@Param("lastArticleId") Long lastArticleId, Pageable pageable);
}

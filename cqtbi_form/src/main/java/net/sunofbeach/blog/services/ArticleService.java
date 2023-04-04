package net.sunofbeach.blog.services;

import net.sunofbeach.blog.pojo.Article;
import net.sunofbeach.blog.response.Result;

public interface ArticleService {

    Result addArticle(Article article, String articleId);

    Result deleteArticle(String articleId,String state);

    Result getArticle(String articleId);

    Result articleList(int page, int size, String userName, String categoryId, String state);

    Result updateDraft(String articleId,Article article);

    Result articleState1UserId(String userId, int page, int size, String state);

    Result getArticle();

    Result addCommentCount(String articleId);

    Result addViewCount(String articleId);

    Result articleUserList(int page, int size, String categoryId,String state);

    Result newArticleList();

    Result hotArticleList();
}

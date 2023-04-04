package net.sunofbeach.blog.services;

import net.sunofbeach.blog.pojo.Comment;
import net.sunofbeach.blog.response.Result;

public interface CommentService {
    Result addComment(Comment comment);

    Result reviseComment(String commentId, String state);

    Result articleIdComment(String articleId,String state);

    Result commentList(int page, int size, String articleId, String userName, String state);
}

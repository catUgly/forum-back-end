package net.sunofbeach.blog.dao;

import net.sunofbeach.blog.pojo.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommentDao extends JpaRepository<Comment,String>, JpaSpecificationExecutor<Comment> {
    Comment findOneById(String commentId);
}

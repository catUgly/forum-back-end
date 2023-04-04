package net.sunofbeach.blog.controller.admin;

import net.sunofbeach.blog.pojo.Comment;
import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/comment")
public class CommentAdminApi {
    @Autowired
    private CommentService commentService;
    /**
     * 添加评论
     * */
    @PostMapping("/addComment")
    public Result addComment(@RequestBody Comment comment) {

        return commentService.addComment(comment);
    }
    /**
    * 修改评论状态 (0删除 1正常 2置顶)
     * 只有管理员.作者.发贴人可以操作 作者只可删除 发帖人和管理员都可操作
    * */
    @PutMapping("/reviseComment/{commentId}")
    public Result reviseComment(@PathVariable("commentId") String commentId,@RequestParam("state") String state) {

        return commentService.reviseComment(commentId,state);
    }
    /**
     * 根据文章Id获取 (置顶2 正常1) 评论
     * */
    @GetMapping("/articleIdComment/{articleId}/{state}")
    public Result articleIdComment(@PathVariable("articleId") String articleId,@PathVariable("state") String state) {

        return commentService.articleIdComment(articleId,state);
    }
    /**
     * 获取评论列表
     * 需要管理员权限
     * */
    @GetMapping("/commentList/{page}/{size}")
    public Result commentList(@PathVariable("page") int page,@PathVariable("size") int size,
                              @RequestParam(value = "articleId",required = false) String articleId,
                              @RequestParam(value = "userName",required = false) String userName,
                              @RequestParam(value = "state",required = false) String state) {

        return commentService.commentList(page,size,articleId,userName,state);
    }
}

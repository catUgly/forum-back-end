package net.sunofbeach.blog.controller.admin;

import net.sunofbeach.blog.pojo.Article;
import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/article")
public class ArticleAdminApi {
    @Autowired
    private ArticleService articleService;
    /**
     * 添加帖子
     * */
    @PostMapping("/addArticle")
    public Result addArticle(@RequestBody Article article) {
        return articleService.addArticle(article, null);
    }

    /**
     * 删除文章 (修改状态 0删除 1正常 2草稿) 只能修改成0和1 将0修改成1只有管理员权限才能操作
     * 需要管理员权限或者作者本人
     * */
    @PutMapping("/deleteArticle/{articleId}/{state}")
    public Result deleteArticle(@PathVariable("articleId") String articleId,@PathVariable("state") String state) {
        return articleService.deleteArticle(articleId,state);
    }
    /**
     * 获取文章详情
     * */
    @GetMapping("/getArticle/{articleId}")
    public Result getArticle(@PathVariable("articleId") String articleId) {
        return articleService.getArticle(articleId);
    }
    /**
     * 获取文章列表
     * 管理员可以获取全部文章 普通用户只能获取正常(1)文章
     * */
    @GetMapping("/articleList/{page}/{size}")
    public Result articleList(@PathVariable("page") int page,@PathVariable("size") int size,
                              @RequestParam(value = "userName",required = false) String userName,
                              @RequestParam(value = "categoryId",required = false) String categoryId,
                              @RequestParam(value = "state",required = false) String state) {

        return articleService.articleList(page,size,userName,categoryId,state);
    }
}

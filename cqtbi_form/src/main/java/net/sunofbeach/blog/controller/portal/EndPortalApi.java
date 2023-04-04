package net.sunofbeach.blog.controller.portal;

import net.sunofbeach.blog.pojo.Article;
import net.sunofbeach.blog.pojo.Comment;
import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/portal")
public class EndPortalApi {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private LooperService looperService;
    @Autowired
    private FriendLinkService friendLinkService;
    /**
     * 获取分类列表
     * 普通用户或未登录只能获取状态为1的分类 管理员账号可以获取全部分类
     * */
    @GetMapping("/categoryList")
    public Result categoryList() {

        return categoryService.listCategory();
    }
    /**
     * 普通用户获取分类列表
     * */
    @GetMapping("/useCategoryList")
    public Result useCategoryList() {

        return categoryService.useCategoryList();
    }
    /**
     * 获取分类详情
     * */
    @PutMapping("/getCategory/{categoryId}")
    public Result getCategory(@PathVariable("categoryId") String categoryId) {

        return categoryService.getCategory(categoryId);
    }
    /**
     * 修改分类文章个数
     * */
    @PutMapping("/updateNumber/{categoryId}")
    public Result updateNumber(@PathVariable("categoryId") String categoryId){

        return categoryService.updateNumber(categoryId);
    }
    /**
     * 发布文章时生成文章Id
     * */
    @GetMapping("/getArticle")
    public Result getArticle() {
        return articleService.getArticle();
    }
    /**
     * 添加文章
     * */
    @PostMapping("/addArticle/{articleId}")
    public Result addArticle(@RequestBody Article article,@PathVariable("articleId") String articleId) {
        return articleService.addArticle(article,articleId);
    }
    /**
     * 获取文章详情
     * */
    @GetMapping("/getArticle/{articleId}")
    public Result getArticle(@PathVariable("articleId") String articleId) {
        return articleService.getArticle(articleId);
    }
    /**
     * 更改评论个数
     * */
    @PutMapping("/addCommentCount/{articleId}")
    public Result addCommentCount(@PathVariable("articleId") String articleId) {
        return articleService.addCommentCount(articleId);
    }
    /**
     * 更改浏览量个数
     * */
    @PutMapping("/addViewCount/{articleId}")
    public Result addViewCount(@PathVariable("articleId") String articleId) {
        return articleService.addViewCount(articleId);
    }
    /**
     * 修改文章 只能修改草稿
     * */
    @PutMapping("/updateDraft/{articleId}")
    public Result updateDraft(@PathVariable("articleId") String articleId,@RequestBody Article article) {

        return articleService.updateDraft(articleId,article);
    }
    /**
     * 根据用户Id获取(state为1或2)文章
     * */
    @GetMapping("/{userId}/{page}/{size}")
    public Result articleState1UserId(@PathVariable("userId") String userId,
                                      @PathVariable("page") int page,
                                      @PathVariable("size") int size) {

        return articleService.articleState1UserId(userId,page,size,null);
    }
    /**
     * 删除文章 (修改状态 0删除 1正常 2草稿) 只能修改成0和1 将0修改成1只有管理员权限才能操作
     * 需要管理员权限或者作者本人
     * */
    @PutMapping("/deleteArticle/{articleId}")
    public Result deleteArticle(@PathVariable("articleId") String articleId,@RequestParam("state") String state) {
        return articleService.deleteArticle(articleId,state);
    }
    /**
     * 获取文章列表 只能获取状态为1的文章
     * */
    @GetMapping("/articleLists/{page}/{size}")
    public Result articleLists(@PathVariable("page") int page,@PathVariable("size") int size) {

        return articleService.articleList(page,size,null,null,"1");
    }
    /**
     * 普通用户获取文章列表
     * */
    @GetMapping("/articleUserList/{page}/{size}")
    public Result articleUserList(@PathVariable("page") int page,@PathVariable("size") int size,
                                  @RequestParam(value = "categoryId",required = false) String categoryId) {

        return articleService.articleUserList(page,size,categoryId,null);
    }
    /**
     * 获取最新文章列表
     * */
    @GetMapping("/newArticleList")
    public Result newArticleList() {
        return articleService.newArticleList();
    }
    /**
     * 获取热门文章列表
     * */
    @GetMapping("/hotArticleList")
    public Result hotArticleList() {
        return articleService.hotArticleList();
    }
    /**
     * 根据条件获取文章列表
     * */
    @GetMapping("/articleList/{page}/{size}")
    public Result articleList(@PathVariable("page") int page,@PathVariable("size") int size,
                              @RequestParam(value = "userName",required = false) String userName,
                              @RequestParam(value = "categoryId",required = false) String categoryId,
                              @RequestParam(value = "state",required = false) String state) {

        return articleService.articleList(page,size,userName,categoryId,state);
    }
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
     * 上传图片
     * */
    @PostMapping("/uploadImage/{source}")
    public Result uploadImage(MultipartFile file, @PathVariable("source") String source, @RequestParam("articleId") String articleId) {

        return imageService.uploadImage(file,source,articleId);
    }
    /**
     * 删除图片
     * 需要作者本人或管理员权限
     * */
    @PutMapping("/deleteImage/{imageId}")
    public Result deleteImage(@PathVariable("imageId") String imageId) {

        return imageService.deleteImage(imageId);
    }
    /**
     * 获取轮播图列表
     * */
    @GetMapping("/looperList")
    public Result looperList() {

        return looperService.looperList();
    }
    /**
     * 获取轮播图详情
     * */
    @GetMapping("/getLooper/{looperId}")
    public Result getLooper(@PathVariable("looperId") String looperId) {
        return looperService.getLooper(looperId);
    }
    /**
     * 获取友情链接列表
     * */
    @GetMapping("/friendLink")
    public Result friendLink() {
        return friendLinkService.friendLink();
    }
    /**
     * 获取友情链接详情
     * */
    @GetMapping("/{friendLinkId}")
    public Result getFriendLink(@PathVariable("friendLinkId") String friendLinkId) {
        return friendLinkService.getFriendLink(friendLinkId);
    }
}

package net.sunofbeach.blog.controller.admin;

import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/admin/image")
public class ImageAdminApi {
    @Autowired
    private ImageService imageService;
    /**
     * 上传图片
     * */
    @PostMapping("/uploadImage/{source}")
    public Result uploadImage(MultipartFile file,@PathVariable("source") String source) {

        return imageService.uploadImage(file,source,null);
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
     * 获取图片列表
     * 需要管理员权限
     * */
    @GetMapping("/imageList/{page}/{size}")
    public Result imageList(@PathVariable("page") int page,@PathVariable("size") int size,
                            @RequestParam(value = "userName",required = false) String userName,
                            @RequestParam(value = "source",required = false) String source,
                            @RequestParam(value = "state",required = false) String state) {
        return imageService.imageList(page,size,userName,source,state);
    }
    /**
     *  根据文章id获取图片
     * */
    @GetMapping("/getArticleId/{articleId}")
    public Result getArticleId(@PathVariable("articleId") String articleId) {
        return imageService.getArticleId(articleId);
    }
}

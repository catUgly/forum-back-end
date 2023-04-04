package net.sunofbeach.blog.controller.admin;


import net.sunofbeach.blog.pojo.Category;
import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category")
public class CategoryAdminApi {
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类
     * 需要管理员权限
     */
    @PostMapping("/addCategory")
    public Result addCategory(@RequestBody Category category) {

        return categoryService.addCategory(category);
    }
    /**
     * 删除分类 修改状态
     * 需要管理员权限
     * */
    @PutMapping("/deleteCategory/{categoryId}")
    public Result deleteCategory(@PathVariable("categoryId") String categoryId) {
        return categoryService.deleteCategory(categoryId);
    }
    /**
     * 获取分类
     * */
    @PutMapping("/getCategory/{categoryId}")
    public Result getCategory(@PathVariable("categoryId") String categoryId) {

        return categoryService.getCategory(categoryId);
    }
    /**
     * 获取分类列表
     * 普通账号或未登录只能获取状态为0的分类，管理员账号可以获取所以分类
     * */
    @GetMapping("/listCategory")
    public Result listCategory() {

        return categoryService.listCategory();
    }
    /**
     * 修改分类
     * 需要管理员权限
     * */
    @PutMapping("/updateCategory/{categoryId}")
    public Result updateCategory(@PathVariable("categoryId") String categoryId,@RequestBody Category category) {
        return categoryService.updateCategory(categoryId,category);
    }
}

package net.sunofbeach.blog.controller.admin;

import net.sunofbeach.blog.pojo.Looper;
import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.LooperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/looper")
public class LooperAdminApi {
    @Autowired
    private LooperService looperService;
    /**
     * 添加轮播图
     * 需要管理员权限
     * */
    @PostMapping("/addLooper")
    public Result addLooper(@RequestBody Looper looper) {

        return looperService.addLooper(looper);
    }
    /**
     * 删除轮播图
     * 需要管理员权限
     * */
    @DeleteMapping("/deleteLooper/{looperId}")
    public Result deleteLooper(@PathVariable("looperId") String looperId) {

        return looperService.deleteLooper(looperId);
    }
    /**
     * 修改轮播图
     * 需要管理员权限
     * */
    @PutMapping("/updateLooper/{looperId}")
    public Result updateLooper(@PathVariable("looperId") String looperId,@RequestBody Looper looper) {
        return looperService.updateLooper(looperId,looper);
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
}

package net.sunofbeach.blog.controller.admin;

import net.sunofbeach.blog.pojo.FriendLink;
import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.FriendLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/FriendLink")
public class FriendLinkAdminApi {
    @Autowired
    private FriendLinkService friendLinkService;
    /**
     * 添加友情链接
     * 需要管理员权限
     * */
    @PostMapping("/addFriendLink")
    public Result addFriendLink(@RequestBody FriendLink friendLink) {

        return friendLinkService.addFriendLink(friendLink);
    }
    /**
     * 删除友情链接
     * 需要管理员权限
     * */
    @DeleteMapping("/{friendLinkId}")
    public Result deleteFriendLink(@PathVariable("friendLinkId") String friendLinkId) {
        return friendLinkService.deleteFriendLink(friendLinkId);
    }
    /**
     * 修改友情链接
     * 需要管理员权限
     * */
    @PutMapping("/{friendLinkId}")
    public Result updateFriendLink(@PathVariable("friendLinkId") String friendLinkId,@RequestBody FriendLink friendLink) {
        return friendLinkService.updateFriendLink(friendLinkId,friendLink);
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

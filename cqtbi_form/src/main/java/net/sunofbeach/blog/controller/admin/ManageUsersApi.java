package net.sunofbeach.blog.controller.admin;

import net.sunofbeach.blog.pojo.vio.SendEmail;
import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/manageUsers")
public class ManageUsersApi {
    @Autowired
    private UserService userService;

    /**
     * 修改用户状态 0删除，1正常
     * 需要管理员权限
     * */
    @PutMapping("/updateState/{userId}")
    public Result updateState(@PathVariable("userId") String userId) {

        return userService.updateState(userId);
    }
    /**
     * 重置密码
     * 需要管理员权限
     * 重置密码后密码默认为 123456
     * */
    @GetMapping("/resetPassword/{userId}")
    public Result resetPassword(@PathVariable("userId") String userId) {

        return userService.resetPassword(userId);
    }
    /**
     * 获取用户列表
     * 需要管理员权限
     * */
    @GetMapping("/userList")
    public Result userList(@RequestParam("page") int page,
                           @RequestParam("size") int size,
                           @RequestParam(value = "userName",required = false) String userName,
                           @RequestParam(value = "email",required = false) String email,
                           @RequestParam(value = "state",required = false) String state) {

        return userService.userList(page,size,userName,email,state);
    }
    /**
     *
     * 发送邮件
     * topic邮件主题 email接收人地址 content发送内容
     * 需要管理员权限
     * */
    @PutMapping("/sendEmail")
    public Result sendEmail(@RequestBody SendEmail sendEmail) {

        return userService.sendEmail(sendEmail);
    }
}

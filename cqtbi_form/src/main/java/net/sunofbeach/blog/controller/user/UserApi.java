package net.sunofbeach.blog.controller.user;

import net.sunofbeach.blog.pojo.SobUser;
import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserApi {
    @Autowired
    private UserService userService;
    /**
    * 获取图灵验证码
    * */
    @GetMapping("/captcha/{key}")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response,@PathVariable("key") String key) throws IOException {
        userService.getCaptcha(request,response,key);
    }
    /**
    * 登录
    * */
    @PostMapping("/login/{captcha}")
    public Result userLogin(HttpServletRequest request,@PathVariable("captcha") String captcha, @RequestBody SobUser sobUser) {

        return userService.userLogin(request,captcha,sobUser);
    }
    /**
     * 发送邮件验证码
     * */
    @GetMapping("/verifyCode")
    public Result sendVerifyCode(HttpServletRequest request,@RequestParam("email") String emailAddress) {
        return userService.sendVerifyCode(request,emailAddress);
    }

    /**
    * 注册
    * */
    @PostMapping("/register")
    public Result register(@RequestBody SobUser sobUser,@RequestParam("captchaCode") String captchaCode,
                           @RequestParam("captchaEmail") String captchaEmail,HttpServletRequest request) {

        return userService.register(sobUser,captchaCode,captchaEmail,request);
    }
    /**
     * 获取登录用户信息
     * */
    @GetMapping("/loginUserInfo")
    public Result loginUserInfo() {

        return userService.loginUserInfo();
    }
    /**
     * 根据Id获取用户信息
     * */
    @GetMapping("/getUserInfo/{userId}")
    public Result getUserInfo(@PathVariable("userId") String userId) {

        return userService.getUserInfo(userId);
    }
    /**
     * 修改用户信息
     * */
    @PutMapping("/updateUser/{userId}")
    public Result updateUser(@PathVariable("userId") String userId,@RequestBody SobUser sobUser) {
        return userService.updateUser(userId,sobUser);
    }
    /**
     * 找回密码，用户忘记密码
     * */
    @PutMapping("/findPassword/{verifyCode}")
    public Result findPassword(HttpServletRequest request,@PathVariable("verifyCode") String verifyCode, @RequestParam("newPassword") String newPassword) {
        return userService.findPassword(request,verifyCode,newPassword);
    }
    /**
     * 修改密码 用户知道密码但是需要对密码进行修改
     * */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestParam("newPassword") String newPassword,@RequestParam("oldPassword") String oldPassword) {
        return userService.updatePassword(newPassword,oldPassword);
    }
    /**
     *检查邮箱是否注册
     * */
    @GetMapping("/checkEmail")
    public Result checkEmail(@RequestParam("email") String email) {
        return userService.checkEmail(email);
    }
    /**
     *检查用户名是否注册
     * */
    @GetMapping("/checkUserName/{userName}")
    public Result checkUserName(@PathVariable("userName") String userName) {
        return userService.checkUserName(userName);
    }
    /**
     * 修改邮箱地址
     * */
    @PutMapping("/updateEmail/{verifyCode}")
    public Result updateEmail(HttpServletRequest request,@PathVariable("verifyCode") String verifyCode,@RequestParam("newEmail") String newEmail) {
        return userService.updateEmail(request,verifyCode,newEmail);
    }

    /**
     * 更改活跃度 上线加2 评论加5 发帖加10
     * */
    @PutMapping("/updateActivity/{number}")
    public Result updateActivity(@PathVariable("number") int number) {

        return userService.updateActivity(number);
    }

}
package net.sunofbeach.blog.services;

import net.sunofbeach.blog.pojo.SobUser;
import net.sunofbeach.blog.pojo.vio.SendEmail;
import net.sunofbeach.blog.response.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public interface UserService {

    void getCaptcha(HttpServletRequest request, HttpServletResponse response,String key) throws IOException;

    Result sendVerifyCode(HttpServletRequest request, String emailAddress);

    Result register(SobUser sobUser, String captchaCode, String captchaEmail,HttpServletRequest request);

    Result userLogin(HttpServletRequest request, String captcha, SobUser sobUser);

    Result getUserInfo(String userId);

    Result loginUserInfo();

    Result updateUser(String userId, SobUser sobUser);

    Result findPassword(HttpServletRequest request,String verifyCode, String newPassword);

    Result updatePassword(String newPassword, String oldPassword);

    Result checkEmail(String email);

    Result checkUserName(String userName);

    Result updateState(String userId);

    Result resetPassword(String userId);

    Result userList(int page, int size, String userName, String email, String state);

    Result sendEmail(SendEmail sendEmail);

    Result updateEmail(HttpServletRequest request, String verifyCode, String newEmail);

    Result updateActivity(int number);
}

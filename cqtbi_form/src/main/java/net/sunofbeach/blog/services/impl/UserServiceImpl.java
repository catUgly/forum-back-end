package net.sunofbeach.blog.services.impl;

import io.jsonwebtoken.Claims;
import net.sunofbeach.blog.dao.UserDao;
import net.sunofbeach.blog.pojo.SobUser;
import net.sunofbeach.blog.pojo.vio.SendEmail;
import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.UserService;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import net.sunofbeach.blog.utils.Constants;
import net.sunofbeach.blog.utils.EmailUtils;
import net.sunofbeach.blog.utils.IdWorker;
import net.sunofbeach.blog.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    /**
     * 检查邮箱格式是否正确
     * */
    public static final String regEx = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    public static boolean isEmailAddressOk(String emailAddress) {
        final Pattern p = Pattern.compile(regEx);
        final Matcher m = p.matcher(emailAddress);
        return m.matches();
    }
    /**
     * 获取登录用户信息，如果登录返回用户信息，如果没有登录返回null
     * */
    public SobUser getLoginUserInfo() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String token = JwtUtil.getToken(request);
        if (token == null) {
            return null;
        }
        Claims claims = JwtUtil.parseJWT(Constants.User.JWT_SEC, token);
        if (claims == null) {
            return null;
        }
        String id = claims.getSubject();
        SobUser user = userDao.findOneById(id);
        return user;
    }
    /**
     * 判断是否是管理员
     * */
    public boolean admin() {
        SobUser loginUserInfo = getLoginUserInfo();
        if (loginUserInfo == null) {
            return false;
        }
        String roles = loginUserInfo.getRoles();
        if (!roles.equals(Constants.User.ROLE_ADMIN)) {
            return false;
        }
        return true;
    }
    /**
    * 获取图灵验证码
    * */
    @Override
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response,String key) throws IOException {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(120, 60, 4);
        // 设置字体
        specCaptcha.setFont(new Font("Verdana", Captcha.FONT_3, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);

        // 验证码存入session
        request.getSession().setAttribute("captcha", specCaptcha.text().toLowerCase());
        System.out.println(request.getSession().getAttribute("captcha"));
        // 输出图片流
        specCaptcha.out(response.getOutputStream());
    }
    /**
     * 发送邮箱验证码
     * */
    @Override
    public Result sendVerifyCode(HttpServletRequest request, String emailAddress) {
        //检查邮箱是否正确
        if (!isEmailAddressOk(emailAddress)) {
            return new Result(300,"邮箱地址格式错误！",null);
        }
        //随机产生一个6位数的整数
        Random random = new Random();
        int code = random.nextInt(999999);
        if (code < 100000) {
            code += 100000;
        }
        System.out.println(code);
        try {
            EmailUtils.sendRegisterVerifyCode(code,emailAddress);
        } catch (Exception e) {
            return new Result(300,"请求失败！",null);
        }
        request.getSession().setAttribute("code",code);
        return new Result(200,"邮箱验证码发送成功！",null);
    }
    /**
     * 注册新用户
     * */
    @Override
    public Result register(SobUser sobUser, String captchaCode, String captchaEmail,HttpServletRequest request) {
        if (sobUser.getEmail() == null) {
            return new Result(300,"邮箱地址不能为空！",null);
        }
        if (sobUser.getUserName() == null) {
            return new Result(300,"用户名不能为空！",null);
        }
        SobUser user = userDao.findOneByUserName(sobUser.getUserName());
        if (user != null) {
            return new Result(300,"用户名已注册！",null);
        }
        SobUser email = userDao.findOneByEmail(sobUser.getEmail());
        if (email != null) {
            return new Result(300,"邮箱已注册!",null);
        }
        String captcha = (String) request.getSession().getAttribute("captcha");
        if (!captchaCode.equals(captcha)) {
            return new Result(300,"图灵验证码输入错误!",null);
        }
        String code = request.getSession().getAttribute("code").toString();
        if (!captchaEmail.equals(code)) {
            return new Result(300,"邮箱验证码错误!",null);
        }
        //对密码进行加密保存
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(sobUser.getPassword());
        sobUser.setPassword(encode);
        //补全数据
        sobUser.setState("1");
        sobUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        sobUser.setRoles(Constants.User.ROLE_USER);
        sobUser.setId(idWorker.nextId() + "");
        sobUser.setCreateTime(new Date());
        sobUser.setUpdateTime(new Date());
        SobUser save = userDao.save(sobUser);
        if (save == null) {
            return new Result(300,"注册用户失败!",null);
        }
        return new Result(200,"注册新用户成功!",null);
    }
    /**
     * 用户登录
     * */
    @Override
    public Result userLogin(HttpServletRequest request, String captcha, SobUser sobUser) {
        if (sobUser.getUserName() == null) {
            return new Result(300,"用户名或邮箱不能为空!",null);
        }
        if (sobUser.getPassword() == null) {
            return new Result(300,"密码不能为空!",null);
        }
        String attribute = (String) request.getSession().getAttribute("captcha");
        if (!captcha.equals(attribute)) {
            return new Result(300,"验证码错误!",null);
        }
        SobUser user = userDao.findOneByUserName(sobUser.getUserName());
        if (user == null) {
            user = userDao.findOneByEmail(sobUser.getUserName());
            if (user == null) {
                return new Result(300,"用户名或密码错误!",null);
            }
        }
        boolean matches = bCryptPasswordEncoder.matches(sobUser.getPassword(), user.getPassword());
        if (!matches) {
            return new Result(300,"用户名或密码错误!",null);
        }
        if (user.getState().equals("0")) {
            return new Result(301,"该账号已被禁用!",null);
        }
        String token = JwtUtil.createJWT(Constants.User.JWT_SEC, user.getId(), user.getUserName(), 1000 * 60 * 60 * 2);
        return new Result(200,"登录成功!",token);
    }
    /**
     * 根据Id获取用户信息
     * */
    @Override
    public Result getUserInfo(String userId) {
        SobUser user = userDao.findOneById(userId);
        if (user == null) {
            return new Result(300,"参数错误，请求失败!",null);
        }
        return new Result(200,"获取用户信息成功!",user);
    }
    /**
     * 获取登录用户信息
     * */
    @Override
    public Result loginUserInfo() {
        SobUser user = getLoginUserInfo();
        if (user == null) {
            return new Result(301,"您还没有登录!",null);
        }
        return new Result(200,"获取登录用户信息成功!",user);
    }
    /**
     * 修改用户信息
     * */
    @Override
    public Result updateUser(String userId, SobUser sobUser) {
        //判断用户是否登录
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null) {
            return new Result(301,"您还没有登录，请先登录!",null);
        }
        //判断登录用户和修改用户Id是否一致
        if (!userInfo.getId().equals(userId)) {
            return new Result(302,"无权访问!",null);
        }
        if (sobUser.getUserName() != null) {
            userInfo.setUserName(sobUser.getUserName());
        }
        if (sobUser.getAvatar() != null) {
            userInfo.setAvatar(sobUser.getAvatar());
        }
        if (sobUser.getSex() != null) {
            userInfo.setSex(sobUser.getSex());
        }
        if (sobUser.getConstellation() != null) {
            userInfo.setConstellation(sobUser.getConstellation());
        }
        if (sobUser.getInterest() != null) {
            userInfo.setInterest(sobUser.getInterest());
        }
        userInfo.setSign(sobUser.getSign());
        userInfo.setUpdateTime(new Date());
        userDao.save(userInfo);
        return new Result(200,"用户信息修改成功!",userInfo);
    }
    /**
     * 找回密码
     * */
    @Override
    public Result findPassword(HttpServletRequest request,String verifyCode, String newPassword) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null) {
            return new Result(301,"您还没有登录，请先登录!",null);
        }
        String captcha = request.getSession().getAttribute("code").toString();
        if (!captcha.equals(verifyCode)) {
            return new Result(300,"验证码错误!",null);
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(newPassword);
        userInfo.setPassword(encode);
        userDao.save(userInfo);
        return new Result(200,"成功找回密码!",null);
    }
    /**
     * 用户修改密码
     * */
    @Override
    public Result updatePassword(String newPassword, String oldPassword) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null) {
            return new Result(301,"您还没有登录，请先登录!",null);
        }
        String password = userInfo.getPassword();
        boolean matches = bCryptPasswordEncoder.matches(oldPassword, password);
        if (!matches) {
            return new Result(303,"旧密码输入错误!",null);
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(newPassword);
        userInfo.setPassword(encode);
        userDao.save(userInfo);
        return new Result(200,"修改密码成功!",null);
    }
    /**
     * 修改邮箱地址
     * */
    @Override
    public Result updateEmail(HttpServletRequest request, String verifyCode, String newEmail) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null) {
            return new Result(301,"您还没有登录,请先登录!",null);
        }
        if (newEmail == null) {
            return new Result(302,"新的邮箱地址不能为空!",null);
        }
        String code = request.getSession().getAttribute("code").toString();
        if (!code.equals(verifyCode)) {
            return new Result(300,"邮箱验证码错误!",null);
        }
        userInfo.setEmail(newEmail);
        userDao.save(userInfo);
        return new Result(200,"修改邮箱地址成功!",null);
    }
    /**
     * 更改活跃度数
     * */
    @Override
    public Result updateActivity(int number) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null) {
            return new Result(300,"用户未登录!",null);
        }
        int activity = userInfo.getActivity();
        int sum = activity + number;
        userInfo.setActivity(sum);
        return new Result(200,"修改活跃度成功！",null);
    }

    /**
     * 检查邮箱是否存在
     * */
    @Override
    public Result checkEmail(String email) {
        //检查邮箱是否正确
        if (!isEmailAddressOk(email)) {
            return new Result(300,"邮箱地址格式错误！",null);
        }
        SobUser user = userDao.findOneByEmail(email);
        if (user != null) {
            return new Result(304,"该邮箱已被注册!",null);
        }
        return new Result(200,"该邮箱未注册!",null);
    }
    /**
     * 检查用户名是否存在
     * */
    @Override
    public Result checkUserName(String userName) {
        SobUser user = userDao.findOneByUserName(userName);
        if (user != null) {
            return new Result(304,"该用户名已被注册!",null);
        }
        return new Result(200,"该用户名未注册!",null);
    }
    /**
     * 修改用户状态 0删除，1正常
     * 需要管理员权限
     * */
    @Override
    public Result updateState(String userId) {
        boolean admin = admin();
        if (!admin) {
            return new Result(302,"无权访问!",null);
        }
        SobUser user = userDao.findOneById(userId);
        if (user == null) {
            return new Result(305,"参数错误,请求失败!",null);
        }
        String state = user.getState();
        if (state.equals("0")) {
            user.setState("1");
        } else {
            user.setState("0");
        }
        userDao.save(user);
        return new Result(200,"用户状态修改成功!",null);
    }
    /**
     * 重置密码
     * 123456
     * 需要管理员权限
     * */
    @Override
    public Result resetPassword(String userId) {
        boolean admin = admin();
        if (!admin) {
            return new Result(302,"无权访问!",null);
        }
        SobUser user = userDao.findOneById(userId);
        if (user == null) {
            return new Result(305,"参数错误,请求失败!",null);
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");
        user.setPassword(encode);
        userDao.save(user);
        return new Result(200,"密码重置成功!",null);
    }
    /**
     * 获取用户列表
     * 需要管理员权限
     * */
    @Override
    public Result userList(int page, int size, final String userName, final String email, final String state) {
        boolean admin = admin();
        if (!admin) {
            return new Result(302,"无权访问!",null);
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);
        Page<SobUser> all = userDao.findAll(new Specification<SobUser>() {
            @Override
            public Predicate toPredicate(Root<SobUser> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (userName != null) {
                    Predicate likeUserName = cb.like(root.get("userName").as(String.class), "%" + userName + "%");
                    predicates.add(likeUserName);
                }
                if (email != null) {
                    Predicate equalEmail = cb.equal(root.get("email").as(String.class), email);
                    predicates.add(equalEmail);
                }
                if (state != null) {
                    Predicate equalState = cb.equal(root.get("state").as(String.class), state);
                    predicates.add(equalState);
                }
                Predicate predicate[] = new Predicate[predicates.size()];
                predicates.toArray(predicate);
                return cb.and(predicate);
            }
        }, pageRequest);
        return new Result(200,"获取用户列表成功!",all);
    }
    /**
     * 发送邮件
     * 需要管理员权限
     * */
    @Override
    public Result sendEmail(SendEmail sendEmail) {
        boolean admin = admin();
        if (!admin) {
            return new Result(301,"无权访问!",null);
        }
        boolean emailAddressOk = isEmailAddressOk(sendEmail.getEmail());
        if (!emailAddressOk) {
            return new Result(303,"邮箱地址格式错误!",null);
        }
        SobUser byEmail = userDao.findOneByEmail(sendEmail.getEmail());
        if (byEmail == null) {
            return new Result(404,"该邮箱地址还未注册!",null);
        }
        try {
            EmailUtils.sendRegisterVerifyCode(sendEmail.getTopic(),sendEmail.getContent(),sendEmail.getEmail());
        } catch (Exception e) {
            return new Result(305,"发送邮件失败!",null);
        }
        return new Result(200,"向" + byEmail.getUserName() + "发送邮件成功!",null);
    }

}

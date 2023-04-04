package net.sunofbeach.blog.services.impl;

import io.jsonwebtoken.Claims;
import net.sunofbeach.blog.dao.LooperDao;
import net.sunofbeach.blog.dao.UserDao;
import net.sunofbeach.blog.pojo.Looper;
import net.sunofbeach.blog.pojo.SobUser;
import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.LooperService;
import net.sunofbeach.blog.utils.Constants;
import net.sunofbeach.blog.utils.IdWorker;
import net.sunofbeach.blog.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class LooperServiceImpl implements LooperService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private LooperDao looperDao;
    /**
     * 判断是否登录
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
     * 添加轮播图
     * 需要管理员权限
     * */
    @Override
    public Result addLooper(Looper looper) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null || !userInfo.getRoles().equals(Constants.User.ROLE_ADMIN)) {
            return new Result(300,"权限不足!",null);
        }
        if (looper.getTitle() == null) {
            return new Result(301,"轮播图标题不能为空!",null);
        }
        if (looper.getImageUrl() == null) {
            return new Result(301,"轮播图图片不能为空!",null);
        }
        if (looper.getTargetUrl() == null) {
            return new Result(301,"目标URl不能为空!",null);
        }
        looper.setId(idWorker.nextId() + "");
        looper.setCreateTime(new Date());
        looper.setUpdateTime(new Date());
        Looper save = looperDao.save(looper);
        if (save == null) {
            return new Result(300, "添加轮播图失败!", null);
        }
        return new Result(200,"添加轮播图成功!",null);
    }
    /**
     * 删除轮播图
     * 需要管理员权限
     * */
    @Override
    public Result deleteLooper(String looperId) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null || !userInfo.getRoles().equals(Constants.User.ROLE_ADMIN)) {
            return new Result(300,"权限不足!",null);
        }
        looperDao.deleteById(looperId);
        return new Result(200,"删除轮播图成功!",null);
    }
    /**
     * 修改轮播图
     * 需要管理员权限
     * */
    @Override
    public Result updateLooper(String looperId, Looper looper) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null || !userInfo.getRoles().equals(Constants.User.ROLE_ADMIN)) {
            return new Result(300,"权限不足!",null);
        }
        Looper oneById = looperDao.findOneById(looperId);
        if (oneById == null) {
            return new Result(300,"参数错误，请求失败!",null);
        }
        if (looper.getTitle() != null) {
            oneById.setTitle(looper.getTitle());
        }
        if (looper.getImageUrl() != null) {
            oneById.setImageUrl(looper.getImageUrl());
        }
        if (looper.getTargetUrl() != null) {
            oneById.setTargetUrl(looper.getTargetUrl());
        }
        oneById.setState(looper.getState());
        oneById.setOrder(looper.getOrder());
        oneById.setUpdateTime(new Date());
        looperDao.save(oneById);
        return new Result(200,"更新轮播图成功!",null);
    }
    /**
     * 获取轮播图列表
     * 需要管理员权限
     * */
    @Override
    public Result looperList() {
        List<Looper> all = looperDao.findAll();
        return new Result(200,"获取轮播图列表成功!",all);
    }
    /**
     * 获取轮播图详情
     */
    @Override
    public Result getLooper(String looperId) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null) {
            return new Result(300,"请先登录!",null);
        }
        Looper looper = looperDao.findOneById(looperId);
        if (looper == null) {
            return new Result(300,"参数错误请求失败!",null);
        }
        return new Result(200,"获取轮播图详情成功!",looper);
    }
}

package net.sunofbeach.blog.services.impl;

import io.jsonwebtoken.Claims;
import net.sunofbeach.blog.dao.FriendLinkDao;
import net.sunofbeach.blog.dao.UserDao;
import net.sunofbeach.blog.pojo.FriendLink;
import net.sunofbeach.blog.pojo.SobUser;
import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.FriendLinkService;
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
public class FriendLinkServiceImpl implements FriendLinkService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private FriendLinkDao friendLinkDao;

    /**
     * 获取登录用户信息
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
     * 添加友情链接
     * 需要管理员权限
     * */
    @Override
    public Result addFriendLink(FriendLink friendLink) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null || !userInfo.getRoles().equals(Constants.User.ROLE_ADMIN)) {
            return new Result(300,"无权访问!",null);
        }
        if (friendLink.getName() == null) {
            return new Result(301,"友情链接名不能为空!",null);
        }
        if (friendLink.getLogo() == null) {
            return new Result(301,"友情链接logo不能为空!",null);
        }
        if (friendLink.getUrl() == null) {
            return new Result(301,"友情链接目标地址不能为空!",null);
        }
        friendLink.setId(idWorker.nextId() + "");
        friendLink.setCreateTime(new Date());
        friendLink.setUpdateTime(new Date());
        FriendLink save = friendLinkDao.save(friendLink);
        if (save == null) {
            return new Result(300,"友情链接添加失败!",null);
        }
        return new Result(200,"友情链接添加成功!",null);
    }
    /**
     * 删除友情链接
     * 需要管理员权限
     * */
    @Override
    public Result deleteFriendLink(String friendLinkId) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null || !userInfo.getRoles().equals(Constants.User.ROLE_ADMIN)) {
            return new Result(300,"无权访问!",null);
        }
        friendLinkDao.deleteById(friendLinkId);
        return new Result(200,"删除友情链接成功!",null);
    }
    /**
     * 更新友情链接
     * 需要管理员权限
     * */
    @Override
    public Result updateFriendLink(String friendLinkId, FriendLink friendLink) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null || !userInfo.getRoles().equals(Constants.User.ROLE_ADMIN)) {
            return new Result(300,"无权访问!",null);
        }
        FriendLink oneById = friendLinkDao.findOneById(friendLinkId);
        if (oneById == null) {
            return new Result(300,"参数错误，请求失败!",null);
        }
        if (friendLink.getUrl() != null) {
            oneById.setUrl(friendLink.getUrl());
        }
        if (friendLink.getLogo() != null) {
            oneById.setLogo(friendLink.getLogo());
        }
        if (friendLink.getName() != null) {
            oneById.setName(friendLink.getName());
        }
        oneById.setOrder(friendLink.getOrder());
        oneById.setState(friendLink.getState());
        oneById.setUpdateTime(new Date());
        friendLinkDao.save(oneById);
        return new Result(200,"友情链接更新成功!",null);
    }
    /**
     * 获取友情链接列表
     * */
    @Override
    public Result friendLink() {
        List<FriendLink> all = friendLinkDao.findAll();
        return new Result(200,"获取友情链接列表成功!",all);
    }
    /**
     * 获取友情链接详情
     * */
    @Override
    public Result getFriendLink(String friendLinkId) {
        FriendLink friendLink = friendLinkDao.findOneById(friendLinkId);
        if (friendLink == null) {
            return new Result(300,"参数错误，请求失败!",null);
        }
        return new Result(200,"获取友情连接详情成功!",friendLink);
    }
}

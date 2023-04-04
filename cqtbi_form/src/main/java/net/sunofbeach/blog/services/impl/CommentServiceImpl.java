package net.sunofbeach.blog.services.impl;

import io.jsonwebtoken.Claims;
import net.sunofbeach.blog.dao.ArticleDao;
import net.sunofbeach.blog.dao.CommentDao;
import net.sunofbeach.blog.dao.UserDao;
import net.sunofbeach.blog.pojo.Article;
import net.sunofbeach.blog.pojo.Comment;
import net.sunofbeach.blog.pojo.SobUser;
import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.CommentService;
import net.sunofbeach.blog.utils.Constants;
import net.sunofbeach.blog.utils.IdWorker;
import net.sunofbeach.blog.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private ArticleDao articleDao;
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
     * 判断是否是管理员账号登录
     * */
    public boolean admin() {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null) {
            return false;
        }
        if (!userInfo.getRoles().equals(Constants.User.ROLE_ADMIN)) {
            return false;
        }
        return true;
    }
    /**
     * 添加评论
     * */
    @Override
    public Result addComment(Comment comment) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null) {
            return new Result(301,"请先登录!",null);
        }
        if (comment.getContent() == null) {
            return new Result(300,"评论内容不能为空!",null);
        }
        if (comment.getArticleId() == null) {
            return new Result(300,"评论文章Id不能为空!",null);
        }
        comment.setId(idWorker.nextId() + "");
        comment.setUserId(userInfo.getId());
        comment.setUseName(userInfo.getUserName());
        comment.setState("1");
        comment.setUserAvatar(userInfo.getAvatar());
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        Comment save = commentDao.save(comment);
        if (save == null) {
            return new Result(301,"评论失败!",null);
        }
        return new Result(200,"评论成功!",null);
    }
    /**
     * 修改状态 (0删除 1正常 2置顶)
     * 只有管理员和作者本人可以操作
     * */
    @Override
    public Result reviseComment(String commentId, String state) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null) {
            return new Result(301,"请先登录!",null);
        }
        Comment comment = commentDao.findOneById(commentId);
        if (comment == null) {
            return new Result(302,"参数错误,请求失败!",null);
        }
        String userId = comment.getUserId();
        String id = userInfo.getId();
        String articleId = comment.getArticleId();
        Article oneById = articleDao.findOneById(articleId);
        String one = oneById.getUserId();
        boolean admin = admin();
        if (!userId.equals(id) && !admin && !one.equals(id)) {
            return new Result(303,"权限不足!",null);
        }
        if (!userId.equals(id) && !state.equals("0") && !admin) {
            return new Result(301,"操作失败!",null);
        }
        comment.setState(state);
        commentDao.save(comment);
        return new Result(200,"操作成功!",null);
    }
    /**
     * 根据文章Id获取 (state为1) 的评论
     * */
    @Override
    public Result articleIdComment(final String articleId, final String state) {
        List<Comment> all = commentDao.findAll(new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                Predicate equalArticle = cb.equal(root.get("articleId").as(String.class), articleId);
                Predicate equalState = cb.equal(root.get("state").as(String.class), state);
                predicates.add(equalArticle);
                predicates.add(equalState);
                Predicate predicate[] = new Predicate[predicates.size()];
                predicates.toArray(predicate);
                return cb.and(predicate);
            }
        });
        return new Result(200,"获取评论成功!",all);
    }
    /**
     * 获取评论列表
     * 需要管路员权限
     * */
    @Override
    public Result commentList(int page, int size, final String articleId, final String userName, final String state) {
        boolean admin = admin();
        if (!admin) {
            return new Result(301,"无权访问!",null);
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);
        Page<Comment> all = commentDao.findAll(new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (articleId != null) {
                    Predicate equalArticleId = cb.equal(root.get("articleId").as(String.class), articleId);
                    predicates.add(equalArticleId);
                }
                if (userName != null) {
                    Predicate likeUserName = cb.like(root.get("useName").as(String.class), "%" + userName + "%");
                    predicates.add(likeUserName);
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
        return new Result(200,"获取评论列表成功!",all);
    }
}

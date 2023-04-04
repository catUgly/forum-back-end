package net.sunofbeach.blog.services.impl;

import io.jsonwebtoken.Claims;
import net.sunofbeach.blog.dao.ArticleDao;
import net.sunofbeach.blog.dao.UserDao;
import net.sunofbeach.blog.pojo.Article;
import net.sunofbeach.blog.pojo.SobUser;
import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.ArticleService;
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
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ArticleDao articleDao;

    /**
     * 获取登录用户信息
     */
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
     */
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
     * 发布文章前生成文章Id
     */
    @Override
    public Result getArticle() {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null) {
            return new Result(300, "请先登录！", null);
        }
        String id = idWorker.nextId() + "";
        return new Result(200, "生成文章Id成功!", id);
    }

    /**
     * 更改评论数
     *
     * @param articleId
     */
    @Override
    public Result addCommentCount(String articleId) {
        Article article = articleDao.findOneById(articleId);
        if (article == null) {
            return new Result(300, "参数错误,请求失败!", null);
        }
        int commentCount = article.getCommentCount() + 1;
        article.setCommentCount(commentCount);
        return new Result(200, "成功!", null);
    }

    /**
     * 更改浏览量数
     */
    @Override
    public Result addViewCount(String articleId) {
        Article article = articleDao.findOneById(articleId);
        if (article == null) {
            return new Result(300, "参数错误，请求失败!", null);
        }
        int viewCount = article.getViewCount() + 1;
        article.setViewCount(viewCount);
        return new Result(200, "成功!", null);
    }


    /**
     * 添加文章
     */
    @Override
    public Result addArticle(Article article, String articleId) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null) {
            return new Result(301, "您还没有登录,请先登录!", null);
        }
        if (article.getTitle() == null) {
            return new Result(300, "文章标题不能为空!", null);
        }
        if (article.getContent() == null) {
            return new Result(300, "文章内容不能为空!", null);
        }
        if (article.getCategoryId() == null) {
            return new Result(300, "文章分类不能为空!", null);
        }
        if (article.getState() == null) {
            article.setState("1");
        }
        article.setId(articleId);
        article.setUserId(userInfo.getId());
        article.setUserName(userInfo.getUserName());
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());
        Article save = articleDao.save(article);
        if (save == null) {
            return new Result(302, "添加文章失败!", null);
        }
        return new Result(200, "添加文章成功!", null);
    }

    /**
     * 删除文章 (修改状态 0删除 1正常)
     * 需要管理员权限或作者本人
     */
    @Override
    public Result deleteArticle(String articleId, String state) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null) {
            return new Result(302, "你还没有登录，请先登录!", null);
        }
        String id = userInfo.getId();
        String roles = userInfo.getRoles();
        Article article = articleDao.findOneById(articleId);
        if (article == null) {
            return new Result(303, "参数错误，请求失败!", null);
        }
        if (!article.getUserId().equals(id) && !roles.equals(Constants.User.ROLE_ADMIN)) {
            return new Result(301, "无权访问!", null);
        }
        article.setState(state);
        articleDao.save(article);
        return new Result(200, "删除文章成功!", null);
    }

    /**
     * 获取文章详情
     */
    @Override
    public Result getArticle(String articleId) {
        Article oneById = articleDao.findOneById(articleId);
        if (oneById == null) {
            return new Result(301, "参数错误，请求失败!", null);
        }
        return new Result(200, "获取文章详情成功!", oneById);
    }
    /**
     * 普通用户获取文章列表
     * */
    @Override
    public Result articleUserList(int page, int size, final String categoryId, final String state) {
        Sort sort = Sort.by(Sort.Direction.DESC, "state","createTime", "viewCount");
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);
        Page<Article> all = articleDao.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (categoryId != null) {
                    Predicate equalCategoryId = cb.equal(root.get("categoryId").as(String.class), categoryId);
                    predicates.add(equalCategoryId);
                }
                Predicate equalState = cb.between(root.<Comparable>get("state").as(String.class), "1", "2");
                predicates.add(equalState);
                Predicate predicate[] = new Predicate[predicates.size()];
                predicates.toArray(predicate);
                return cb.and(predicate);
            }
        }, pageRequest);
        return new Result(200, "获取文章列表成功!", all);
    }
    /**
     * 获取最新文章列表
     * */
    @Override
    public Result newArticleList() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(0, 10, sort);
        Page<Article> all = articleDao.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                return cb.between(root.<Comparable>get("state").as(String.class), "1", "2");
            }
        }, pageRequest);
        return new Result(200,"获取最新文章列表成功!",all);
    }
    /**
     * 获取热门文章列表
     * */
    @Override
    public Result hotArticleList() {
        Sort sort = Sort.by(Sort.Direction.DESC, "viewCount");
        PageRequest pageRequest = PageRequest.of(0, 15, sort);
        Page<Article> all = articleDao.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                return cb.between(root.<Comparable>get("state").as(String.class), "1", "2");
            }
        }, pageRequest);
        return new Result(200,"获取最新文章列表成功!",all);
    }

    /**
     * 管理员获取文章列表
     */
    @Override
    public Result articleList(int page, int size, final String userName, final String categoryId, final String state) {
        boolean admin = admin();
        if (!admin) {
            return new Result(300,"权限不足,无法访问!",null);
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "state","createTime", "viewCount");
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);
        Page<Article> all = articleDao.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (userName != null) {
                    Predicate likeUserName = cb.like(root.get("userName").as(String.class), "%" + userName + "%");
                    predicates.add(likeUserName);
                }
                if (categoryId != null) {
                    Predicate equalCategoryId = cb.equal(root.get("categoryId").as(String.class), categoryId);
                    predicates.add(equalCategoryId);
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
        return new Result(200, "获取文章列表成功!", all);
    }

    /**
     * 修改草稿
     */
    @Override
    public Result updateDraft(String articleId, Article article) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null) {
            return new Result(301, "请先登录!", null);
        }
        Article byId = articleDao.findOneById(articleId);
        if (byId == null) {
            return new Result(302, "参数错误，请求失败!", null);
        }
        if (!userInfo.getId().equals(byId.getUserId())) {
            return new Result(303, "权限不足!", null);
        }
        if (!byId.getState().equals("3")) {
            return new Result(304, "只能修改草稿文章!", null);
        }
        if (article.getContent() != null) {
            byId.setContent(article.getContent());
        }
        if (article.getTitle() != null) {
            byId.setTitle(article.getTitle());
        }
        if (article.getCategoryId() != null) {
            byId.setCategoryId(article.getCategoryId());
        }
        byId.setType(article.getType());
        byId.setLabels(article.getLabels());
        byId.setUpdateTime(new Date());
        return new Result(200, "修改草稿文章成功!", null);
    }

    /**
     * 根据用户Id获取文章
     */
    @Override
    public Result articleState1UserId(final String userId, int page, int size, final String state) {
        if (userId == null) {
            return new Result(302, "查询用户不能为空!", null);
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);
        Page<Article> all = articleDao.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                Predicate equalUserId = cb.equal(root.get("userId").as(String.class), userId);
                Predicate equalState = cb.between(root.<Comparable>get("state").as(String.class), "1", "2");
                predicates.add(equalState);
                predicates.add(equalUserId);
                Predicate predicate[] = new Predicate[predicates.size()];
                predicates.toArray(predicate);
                return cb.and(predicate);
            }
        }, pageRequest);
        return new Result(200, "获取文章成功!", all);
    }
}

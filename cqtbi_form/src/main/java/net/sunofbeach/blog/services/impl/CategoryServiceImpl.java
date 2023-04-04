package net.sunofbeach.blog.services.impl;

import io.jsonwebtoken.Claims;
import net.sunofbeach.blog.dao.CategoryDao;
import net.sunofbeach.blog.dao.UserDao;
import net.sunofbeach.blog.pojo.Category;
import net.sunofbeach.blog.pojo.SobUser;
import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.CategoryService;
import net.sunofbeach.blog.utils.Constants;
import net.sunofbeach.blog.utils.IdWorker;
import net.sunofbeach.blog.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private CategoryDao categoryDao;

    /**
     * 判断是否是管理员账号
     */
    public boolean admin() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String token = JwtUtil.getToken(request);
        if (token == null) {
            return false;
        }
        Claims claims = JwtUtil.parseJWT(Constants.User.JWT_SEC, token);
        if (claims == null) {
            return false;
        }
        String id = claims.getSubject();
        SobUser user = userDao.findOneById(id);
        if (!user.getRoles().equals(Constants.User.ROLE_ADMIN)) {
            return false;
        }
        return true;
    }

    /**
     * 添加分类
     * 需要管理员权限
     */
    @Override
    public Result addCategory(Category category) {
        boolean admin = admin();
        if (!admin) {
            return new Result(301, "无权访问!", null);
        }
        if (category.getName() == null) {
            return new Result(302, "分类名称不能为空!", null);
        }
        Category byName = categoryDao.findOneByName(category.getName());
        if (byName != null) {
            return new Result(303, "分类称已存在!", null);
        }
        if (category.getDescription() == null) {
            return new Result(302, "分类描述不能为空!", null);
        }
        if (category.getPinyin() == null) {
            return new Result(302, "分类拼音不能为空!", null);
        }
        if (category.getIcon() == null) {
            return new Result(302, "图标不能为空!", null);
        }
        category.setNumber(0);
        category.setId(idWorker.nextId() + "");
        category.setStatus(Constants.User.DEFAULT_STATE);
        category.setOrder(1);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        Category save = categoryDao.save(category);
        if (save == null) {
            return new Result(300, "添加分类失败!", null);
        }
        return new Result(200, "添加分类成功!", null);
    }

    /**
     * 删除分类 修改状态
     * 需要管理员权限
     */
    @Override
    public Result deleteCategory(String categoryId) {
        boolean admin = admin();
        if (!admin) {
            return new Result(301, "无权访问!", null);
        }
        Category byId = categoryDao.findOneById(categoryId);
        if (byId == null) {
            return new Result(302, "参数错误,请求失败!", null);
        }
        byId.setStatus("0");
        categoryDao.save(byId);
        return new Result(200, "删除分类成功!", null);
    }

    /**
     * 获取分类详情
     */
    @Override
    public Result getCategory(String categoryId) {
        Category byId = categoryDao.findOneById(categoryId);
        if (byId == null) {
            return new Result(302, "参数错误,请求失败!", null);
        }
        return new Result(200, "获取分类列表成功!", byId);
    }

    /**
     * 管理员获取分类列表
     */
    @Override
    public Result listCategory() {
        Sort sort = Sort.by(Sort.Direction.DESC, "order","createTime");
        boolean admin = admin();
        List<Category> categoryList;
        if (!admin) {
            return new Result(300,"权限不足,无法访问!",null);
        }
        categoryList = categoryDao.findAll(sort);
        return new Result(200, "获取分类列表成功!", categoryList);
    }

    /**
     * 普通用户获取分类列表
     */
    @Override
    public Result useCategoryList() {
        Sort sort = Sort.by(Sort.Direction.DESC, "order","createTime");
        List<Category> categoryList = categoryDao.findAll(new Specification<Category>() {
            @Override
            public Predicate toPredicate(Root<Category> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Predicate status = cb.equal(root.get("status").as(String.class), "1");
                return cb.and(status);
            }
        },sort);
        return new Result(200,"获取分类列表成功!",categoryList);
    }

    /**
     * 更新分类
     * 需要管理员权限
     */
    @Override
    public Result updateCategory(String categoryId, Category category) {
        boolean admin = admin();
        if (!admin) {
            return new Result(301, "无权访问!", null);
        }
        Category byId = categoryDao.findOneById(categoryId);
        if (byId == null) {
            return new Result(302, "参数错误，请求失败!", null);
        }
        if (category.getName() != null) {
            Category byName = categoryDao.findOneByName(category.getName());
            if (byName != null) {
                return new Result(303, "该分类名称已存在!", null);
            }
            byId.setName(category.getName());
        }
        if (category.getDescription() != null) {
            byId.setDescription(category.getDescription());
        }
        if (category.getPinyin() != null) {
            byId.setPinyin(category.getPinyin());
        }
        byId.setOrder(category.getOrder());
        byId.setStatus(category.getStatus());
        byId.setUpdateTime(new Date());
        return new Result(200, "更新分类成功!", null);
    }

    /**
     * 修改分类数量
     */
    @Override
    public Result updateNumber(String categoryId) {
        Category category = categoryDao.findOneById(categoryId);
        if (category == null) {
            return new Result(300, "参数错误请求失败!", null);
        }
        int number = category.getNumber() + 1;
        category.setNumber(number);
        categoryDao.save(category);
        return new Result(200, "修改分类个数成功!", number);
    }

}

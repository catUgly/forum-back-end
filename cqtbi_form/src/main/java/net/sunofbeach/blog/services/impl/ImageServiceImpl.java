package net.sunofbeach.blog.services.impl;

import io.jsonwebtoken.Claims;
import net.sunofbeach.blog.dao.ImageDao;
import net.sunofbeach.blog.dao.UserDao;
import net.sunofbeach.blog.pojo.Images;
import net.sunofbeach.blog.pojo.SobUser;
import net.sunofbeach.blog.response.Result;
import net.sunofbeach.blog.services.ImageService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ImageDao imageDao;

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd");

    public static final String imagePath = "src/main/resources/static/uploadFile/";
    /**
     * 判断用户是否登录
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
     * 上传图片
     * */
    @Override
    public Result uploadImage(MultipartFile file, String source,String articleId) {
        System.out.println(file);
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null) {
            return new Result(300,"请先登录!",null);
        }
        if (file == null) {
            return new Result(300,"图片不能为空!",null);
        }
        //判断文件类型 只支持，png jpg gif类型的图片格式
        String contentType = file.getContentType(); //获取图片类型
        String type = null;
        if ("image/png".equals(contentType)) {
            type = "png";
        } else if ("image/gif".equals(contentType)) {
            type = "gif";
        }else if ("image/jpeg".equals(contentType)) {
            type = "jpg";
        }
        if (type == null) {
            return new Result(300,"不支持此图片类型!",null);
        }
        long size = file.getSize();
        if (size > 1024 * 1024 * 2) {
            return new Result(300,"图片最大为2MB!",null);
        }
        String fileName = String.valueOf(idWorker.nextId());
        String format = simpleDateFormat.format(new Date());
        String dayPath = imagePath + File.separator + format;
        String pathDb = "static/uploadFile/" + format + "/" + type + "/" + fileName +"." + type;
        File dayPathFile = new File(dayPath);
        //判断是否有当前日期的文件夹 如果没有就创建
        if (!dayPathFile.exists()) {
            dayPathFile.mkdirs();
        }
        String targetPath = dayPath + File.separator + type;
        File targetFile = new File(targetPath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        File dest = new File(targetFile.getAbsolutePath() + File.separator + fileName + "." + type);
        try {
            file.transferTo(dest);
            //将图片信息保存在数据库中
            Images images = new Images();
            images.setId(idWorker.nextId() + "");
            images.setUrl(pathDb);
            images.setState("1");
            images.setArticleId(articleId);
            images.setSource(source);
            images.setCreateTime(new Date());
            images.setUpdateTime(new Date());
            images.setUserId(userInfo.getId());
            Images save = imageDao.save(images);
            if (save == null) {
                return new Result(300,"图片上传失败!",null);
            }
            System.out.println("dest------->" + dest);
            System.out.println("pathDb----->" + pathDb);
            return new Result(200,"图片上传成功!",images);
        } catch (IOException e) {
            return new Result(300,"图片上传失败!",null);
        }
    }
    /**
     * 删除图片
     * 需要作者本人或管理员权限
     * */
    @Override
    public Result deleteImage(String imageId) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null) {
            return new Result(300,"请先登录!",null);
        }
        Images image = imageDao.findOneById(imageId);
        if (image == null) {
            return new Result(301,"参数错误请求失败!",null);
        }
        String id = userInfo.getId();
        String roles = userInfo.getRoles();
        String userId = image.getUserId();
        if (!id.equals(userId) && !roles.equals(Constants.User.ROLE_ADMIN)) {
            return new Result(302,"权限不足!",null);
        }
        image.setState("0");
        imageDao.save(image);
        return new Result(200,"删除图片成功!",null);
    }
    /**
     * 获取图片列表
     * 需要管理员权限
     * */
    @Override
    public Result imageList(int page, int size, final String userName, final String source, final String state) {
        SobUser userInfo = getLoginUserInfo();
        if (userInfo == null || !userInfo.getRoles().equals(Constants.User.ROLE_ADMIN)) {
            return new Result(300,"权限不足!",null);
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);
        Page<Images> all = imageDao.findAll(new Specification<Images>() {
            @Override
            public Predicate toPredicate(Root<Images> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (userName != null && userName != "") {
                    Predicate equalUserId = cb.equal(root.get("userId").as(String.class), userName);
                    predicates.add(equalUserId);
                }
                if (source != null) {
                    Predicate equalSource = cb.equal(root.get("source").as(String.class), source);
                    predicates.add(equalSource);
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
        return new Result(200,"获取图片列表成功!",all);
    }
    /**
     * 根据文章Id查询图片
     * */
    @Override
    public Result getArticleId(final String  articleId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        List<Images> all = imageDao.findAll(new Specification<Images>() {
            @Override
            public Predicate toPredicate(Root<Images> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                return cb.equal(root.get("articleId").as(String.class), articleId);
            }
        }, sort);
        return new Result(200,"获取文章图片成功!",all);
    }
}

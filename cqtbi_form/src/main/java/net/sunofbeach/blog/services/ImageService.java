package net.sunofbeach.blog.services;

import net.sunofbeach.blog.response.Result;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    Result uploadImage(MultipartFile file, String source,String articleId);

    Result deleteImage(String imageId);

    Result imageList(int page, int size, String userName, String source, String state);

    Result getArticleId(String articleId);
}

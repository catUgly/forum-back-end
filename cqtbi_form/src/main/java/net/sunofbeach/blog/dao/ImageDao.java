package net.sunofbeach.blog.dao;

import net.sunofbeach.blog.pojo.Images;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImageDao extends JpaRepository<Images,String>, JpaSpecificationExecutor<Images> {

    Images findOneById(String imagesId);
}

package net.sunofbeach.blog.dao;

import net.sunofbeach.blog.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryDao extends JpaRepository<Category,String>, JpaSpecificationExecutor<Category> {

    Category findOneById(String categoryId);
    Category findOneByName(String name);
}

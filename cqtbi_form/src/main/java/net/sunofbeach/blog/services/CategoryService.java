package net.sunofbeach.blog.services;

import net.sunofbeach.blog.pojo.Category;
import net.sunofbeach.blog.response.Result;

public interface CategoryService {

    Result addCategory(Category category);

    Result deleteCategory(String categoryId);

    Result getCategory(String categoryId);

    Result listCategory();

    Result updateCategory(String categoryId, Category category);

    Result updateNumber(String categoryId);

    Result useCategoryList();
}

package com.recipe.controller;

import com.recipe.entity.Category;
import com.recipe.mapper.CategoryMapper;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

import java.util.List;

@Controller
@Mapping("/api")
public class CategoryController {

    @Inject
    private CategoryMapper categoryMapper;

    @Mapping("/categories")
    public List<Category> list() {
        return categoryMapper.findAllWithCount();
    }
}

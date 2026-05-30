package com.recipe.mapper;

import com.recipe.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> findAllWithCount();
}

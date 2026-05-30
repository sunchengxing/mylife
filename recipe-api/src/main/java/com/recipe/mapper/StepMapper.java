package com.recipe.mapper;

import com.recipe.entity.Step;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StepMapper {
    List<Step> findByRecipeId(@Param("recipeId") long recipeId);
}

package com.recipe.mapper;

import com.recipe.entity.Ingredient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IngredientMapper {
    List<Ingredient> findByRecipeId(@Param("recipeId") long recipeId);
    List<Ingredient> findByRecipeIdAndType(@Param("recipeId") long recipeId, @Param("type") String type);
}

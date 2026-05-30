package com.recipe.mapper;

import com.recipe.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecipeMapper {
    List<Recipe> findByCategory(@Param("category") String category,
                                @Param("offset") int offset,
                                @Param("limit") int limit);

    long countByCategory(@Param("category") String category);

    List<Recipe> search(@Param("q") String query,
                        @Param("offset") int offset,
                        @Param("limit") int limit);

    long countSearch(@Param("q") String query);

    Recipe findById(@Param("id") long id);

    Recipe findRandom();

    List<Recipe> findByIngredient(@Param("ingredient") String ingredient,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);
}

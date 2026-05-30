package com.recipe.controller;

import com.recipe.entity.Ingredient;
import com.recipe.entity.Recipe;
import com.recipe.entity.Step;
import com.recipe.mapper.IngredientMapper;
import com.recipe.mapper.RecipeMapper;
import com.recipe.mapper.StepMapper;
import com.recipe.model.PageResult;
import com.recipe.model.RecipeDetail;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Mapping("/api")
public class RecipeController {

    @Inject
    private RecipeMapper recipeMapper;

    @Inject
    private IngredientMapper ingredientMapper;

    @Inject
    private StepMapper stepMapper;

    @Mapping("/recipes")
    public Object list(@Param(defaultValue = "1") int page,
                       @Param(defaultValue = "20") int size,
                       @Param(required = false) String category,
                       @Param(required = false) String q) {
        int offset = (page - 1) * size;
        List<Recipe> items;
        long total;

        if (q != null && !q.isBlank()) {
            items = recipeMapper.search(q, offset, size);
            total = recipeMapper.countSearch(q);
        } else {
            items = recipeMapper.findByCategory(category, offset, size);
            total = recipeMapper.countByCategory(category);
        }

        return new PageResult<>(items, page, size, total);
    }

    @Mapping("/recipes/{id}")
    public Object detail(long id) {
        Recipe recipe = recipeMapper.findById(id);
        if (recipe == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "not_found");
            err.put("message", "Recipe " + id + " not found");
            Context.current().status(404);
            return err;
        }

        RecipeDetail detail = new RecipeDetail();
        detail.setRecipe(recipe);
        detail.setMainIngredients(ingredientMapper.findByRecipeIdAndType(id, "main"));
        detail.setSubIngredients(ingredientMapper.findByRecipeIdAndType(id, "sub"));
        detail.setSteps(stepMapper.findByRecipeId(id));
        return detail;
    }

    @Mapping("/recipes/random")
    public Object random() {
        Recipe recipe = recipeMapper.findRandom();
        if (recipe == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "not_found");
            err.put("message", "No recipe with cover image found");
            return err;
        }

        RecipeDetail detail = new RecipeDetail();
        detail.setRecipe(recipe);
        detail.setMainIngredients(ingredientMapper.findByRecipeIdAndType(recipe.getId(), "main"));
        detail.setSubIngredients(ingredientMapper.findByRecipeIdAndType(recipe.getId(), "sub"));
        detail.setSteps(stepMapper.findByRecipeId(recipe.getId()));
        return detail;
    }

    @Mapping("/recipes/search")
    public Object search(@Param String q,
                         @Param(defaultValue = "1") int page,
                         @Param(defaultValue = "20") int size) {
        if (q == null || q.isBlank()) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "bad_request");
            err.put("message", "Query parameter 'q' is required");
            return err;
        }
        int offset = (page - 1) * size;
        List<Recipe> items = recipeMapper.search(q, offset, size);
        long total = recipeMapper.countSearch(q);
        return new PageResult<>(items, page, size, total);
    }
}

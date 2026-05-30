package com.recipe.model;

import com.recipe.entity.Ingredient;
import com.recipe.entity.Recipe;
import com.recipe.entity.Step;

import java.util.List;

public class RecipeDetail {
    private Recipe recipe;
    private List<Ingredient> mainIngredients;
    private List<Ingredient> subIngredients;
    private List<Step> steps;

    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = recipe; }
    public List<Ingredient> getMainIngredients() { return mainIngredients; }
    public void setMainIngredients(List<Ingredient> mainIngredients) { this.mainIngredients = mainIngredients; }
    public List<Ingredient> getSubIngredients() { return subIngredients; }
    public void setSubIngredients(List<Ingredient> subIngredients) { this.subIngredients = subIngredients; }
    public List<Step> getSteps() { return steps; }
    public void setSteps(List<Step> steps) { this.steps = steps; }
}

package com.recipe.entity;

public class Ingredient {
    private Long id;
    private Long recipeId;
    private String name;
    private String amount;
    private String type;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRecipeId() { return recipeId; }
    public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}

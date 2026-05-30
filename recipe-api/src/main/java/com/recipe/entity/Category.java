package com.recipe.entity;

public class Category {
    private Long id;
    private String slug;
    private String name;
    private String type;
    private Integer totalPages;
    private Integer recipeCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
    public Integer getRecipeCount() { return recipeCount; }
    public void setRecipeCount(Integer recipeCount) { this.recipeCount = recipeCount; }
}

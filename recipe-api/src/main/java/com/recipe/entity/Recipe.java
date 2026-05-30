package com.recipe.entity;

public class Recipe {
    private Long id;
    private String title;
    private String author;
    private String authorUrl;
    private String coverImg;
    private Integer views;
    private Integer favorites;
    private String category;
    private String tip;
    private String createdAt;
    private String updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getAuthorUrl() { return authorUrl; }
    public void setAuthorUrl(String authorUrl) { this.authorUrl = authorUrl; }
    public String getCoverImg() { return coverImg; }
    public void setCoverImg(String coverImg) { this.coverImg = coverImg; }
    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }
    public Integer getFavorites() { return favorites; }
    public void setFavorites(Integer favorites) { this.favorites = favorites; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTip() { return tip; }
    public void setTip(String tip) { this.tip = tip; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}

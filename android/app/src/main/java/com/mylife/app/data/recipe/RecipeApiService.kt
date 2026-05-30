package com.mylife.app.data.recipe

import retrofit2.http.*

interface RecipeApiService {
    @GET("api/categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("api/recipes")
    suspend fun getRecipes(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
        @Query("category") category: String? = null,
        @Query("q") q: String? = null,
    ): PageResultDto

    @GET("api/recipes/{id}")
    suspend fun getRecipeDetail(@Path("id") id: Long): RecipeDetailDto

    @GET("api/recipes/random")
    suspend fun getRandomRecipe(): RecipeDetailDto

    @GET("api/recipes/search")
    suspend fun searchRecipes(
        @Query("q") q: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
    ): PageResultDto
}

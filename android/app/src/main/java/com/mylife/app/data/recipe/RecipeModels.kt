package com.mylife.app.data.recipe

import androidx.room.*
import com.google.gson.annotations.SerializedName

// ─── DTOs (from recipe-api) ───

data class CategoryDto(
    val id: Long,
    val slug: String,
    val name: String,
    val type: String,
    @SerializedName("totalPages") val totalPages: Int = 0,
    @SerializedName("recipeCount") val recipeCount: Int = 0,
)

data class RecipeDto(
    val id: Long,
    val title: String,
    val author: String = "",
    @SerializedName("authorUrl") val authorUrl: String = "",
    @SerializedName("coverImg") val coverImg: String = "",
    val views: Int = 0,
    val favorites: Int = 0,
    val category: String = "",
    val tip: String = "",
    @SerializedName("createdAt") val createdAt: String = "",
    @SerializedName("updatedAt") val updatedAt: String = "",
)

data class IngredientDto(
    val id: Long,
    @SerializedName("recipeId") val recipeId: Long,
    val name: String,
    val amount: String = "",
    val type: String = "main",
)

data class StepDto(
    val id: Long,
    @SerializedName("recipeId") val recipeId: Long,
    @SerializedName("stepNo") val stepNo: Int,
    val description: String,
    @SerializedName("imageUrl") val imageUrl: String = "",
)

data class RecipeDetailDto(
    val recipe: RecipeDto,
    @SerializedName("mainIngredients") val mainIngredients: List<IngredientDto> = emptyList(),
    @SerializedName("subIngredients") val subIngredients: List<IngredientDto> = emptyList(),
    val steps: List<StepDto> = emptyList(),
)

data class PageResultDto(
    val items: List<RecipeDto>,
    val page: Int,
    val size: Int,
    val total: Long,
    @SerializedName("totalPages") val totalPages: Int,
)

// ─── Room Entities ───

@Entity(tableName = "cached_recipes")
data class CachedRecipe(
    @PrimaryKey val id: Long,
    val title: String,
    val coverImg: String,
    val category: String,
    val views: Int,
    val favorites: Int,
    val cachedAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "cached_ingredients",
    foreignKeys = [ForeignKey(
        entity = CachedRecipe::class,
        parentColumns = ["id"],
        childColumns = ["recipeId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("recipeId")],
)
data class CachedIngredient(
    @PrimaryKey val id: Long,
    val recipeId: Long,
    val name: String,
    val amount: String,
    val type: String,
)

@Entity(
    tableName = "cached_steps",
    foreignKeys = [ForeignKey(
        entity = CachedRecipe::class,
        parentColumns = ["id"],
        childColumns = ["recipeId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("recipeId")],
)
data class CachedStep(
    @PrimaryKey val id: Long,
    val recipeId: Long,
    val stepNo: Int,
    val description: String,
    val imageUrl: String,
)

// ─── DAOs ───

@Dao
interface CachedRecipeDao {
    @Query("SELECT * FROM cached_recipes WHERE category = :category ORDER BY cachedAt DESC")
    suspend fun getByCategory(category: String): List<CachedRecipe>

    @Query("SELECT * FROM cached_recipes ORDER BY cachedAt DESC")
    suspend fun getAll(): List<CachedRecipe>

    @Query("SELECT * FROM cached_recipes WHERE id = :id")
    suspend fun getById(id: Long): CachedRecipe?

    @Query("SELECT COUNT(*) FROM cached_recipes")
    suspend fun count(): Int

    @Query("DELETE FROM cached_recipes WHERE cachedAt < :expireTime")
    suspend fun deleteExpired(expireTime: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(recipes: List<CachedRecipe>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(recipe: CachedRecipe)
}

@Dao
interface CachedIngredientDao {
    @Query("SELECT * FROM cached_ingredients WHERE recipeId = :recipeId AND type = :type")
    suspend fun getByRecipeAndType(recipeId: Long, type: String): List<CachedIngredient>

    @Query("SELECT * FROM cached_ingredients WHERE recipeId = :recipeId")
    suspend fun getByRecipe(recipeId: Long): List<CachedIngredient>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(ingredients: List<CachedIngredient>)
}

@Dao
interface CachedStepDao {
    @Query("SELECT * FROM cached_steps WHERE recipeId = :recipeId ORDER BY stepNo")
    suspend fun getByRecipe(recipeId: Long): List<CachedStep>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(steps: List<CachedStep>)
}

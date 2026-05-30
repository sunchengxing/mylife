package com.mylife.app.data.recipe

import com.mylife.app.util.ApiClient

class RecipeRepository(
    private val recipeDao: CachedRecipeDao,
    private val ingredientDao: CachedIngredientDao,
    private val stepDao: CachedStepDao,
) {
    private val api = ApiClient.recipeApi
    private val LIST_CACHE_TTL = 60 * 60 * 1000L   // 1 hour
    private val DETAIL_CACHE_TTL = 24 * 60 * 60 * 1000L  // 24 hours

    suspend fun getCategories(): List<CategoryDto> {
        return try {
            api.getCategories()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipes(page: Int, size: Int, category: String?): PageResultDto {
        return try {
            val result = api.getRecipes(page, size, category)
            // Cache recipes for offline browsing
            val cached = result.items.map { dto ->
                CachedRecipe(
                    id = dto.id, title = dto.title, coverImg = dto.coverImg,
                    category = dto.category, views = dto.views, favorites = dto.favorites,
                )
            }
            recipeDao.upsertAll(cached)
            result
        } catch (e: Exception) {
            // Fallback to cache on network error
            val cached = if (category != null) recipeDao.getByCategory(category) else recipeDao.getAll()
            PageResultDto(
                items = cached.map { RecipeDto(id = it.id, title = it.title, coverImg = it.coverImg, category = it.category, views = it.views, favorites = it.favorites) },
                page = page, size = size, total = cached.size.toLong(), totalPages = 1,
            )
        }
    }

    suspend fun getRecipeDetail(id: Long): RecipeDetailDto? {
        return try {
            val detail = api.getRecipeDetail(id)
            // Cache detail
            recipeDao.upsert(CachedRecipe(
                id = detail.recipe.id, title = detail.recipe.title, coverImg = detail.recipe.coverImg,
                category = detail.recipe.category, views = detail.recipe.views, favorites = detail.recipe.favorites,
            ))
            ingredientDao.upsertAll(detail.mainIngredients.map { CachedIngredient(it.id, it.recipeId, it.name, it.amount, it.type) }
                + detail.subIngredients.map { CachedIngredient(it.id, it.recipeId, it.name, it.amount, it.type) })
            stepDao.upsertAll(detail.steps.map { CachedStep(it.id, it.recipeId, it.stepNo, it.description, it.imageUrl) })
            detail
        } catch (e: Exception) {
            // Fallback to cache
            val recipe = recipeDao.getById(id) ?: return null
            val ingredients = ingredientDao.getByRecipe(id)
            val steps = stepDao.getByRecipe(id)
            RecipeDetailDto(
                recipe = RecipeDto(id = recipe.id, title = recipe.title, coverImg = recipe.coverImg, category = recipe.category, views = recipe.views, favorites = recipe.favorites),
                mainIngredients = ingredients.filter { it.type == "main" }.map { IngredientDto(it.id, it.recipeId, it.name, it.amount, it.type) },
                subIngredients = ingredients.filter { it.type == "sub" }.map { IngredientDto(it.id, it.recipeId, it.name, it.amount, it.type) },
                steps = steps.map { StepDto(it.id, it.recipeId, it.stepNo, it.description, it.imageUrl) },
            )
        }
    }

    suspend fun searchRecipes(q: String, page: Int, size: Int): PageResultDto {
        return try {
            api.searchRecipes(q, page, size)
        } catch (_: Exception) {
            PageResultDto(items = emptyList(), page = page, size = size, total = 0, totalPages = 0)
        }
    }

    suspend fun getRandomRecipe(): RecipeDetailDto? {
        return try {
            api.getRandomRecipe()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun cleanExpiredCache() {
        val expireList = System.currentTimeMillis() - DETAIL_CACHE_TTL
        recipeDao.deleteExpired(expireList)
    }
}

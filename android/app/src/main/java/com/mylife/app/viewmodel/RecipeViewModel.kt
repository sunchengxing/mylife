package com.mylife.app.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.mylife.app.MyLifeApp
import com.mylife.app.data.recipe.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeListState(
    val items: List<RecipeDto> = emptyList(),
    val categories: List<CategoryDto> = emptyList(),
    val selectedCategory: String? = null,
    val query: String = "",
    val page: Int = 1,
    val totalPages: Int = 1,
    val total: Long = 0,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
)

data class RecipeDetailState(
    val detail: RecipeDetailDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private var repo: RecipeRepository? = null

    private val _listState = MutableStateFlow(RecipeListState())
    val listState: StateFlow<RecipeListState> = _listState

    private val _detailState = MutableStateFlow(RecipeDetailState())
    val detailState: StateFlow<RecipeDetailState> = _detailState

    init {
        try {
            val db = (application as MyLifeApp).database
            repo = RecipeRepository(db.cachedRecipeDao(), db.cachedIngredientDao(), db.cachedStepDao())
            loadCategories()
            loadRecipes()
        } catch (e: Exception) {
            _listState.update { it.copy(error = "数据库初始化失败: ${e.message}") }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val cats = repo?.getCategories() ?: emptyList()
                _listState.update { it.copy(categories = cats) }
            } catch (e: Exception) {
                _listState.update { it.copy(error = e.message) }
            }
        }
    }

    fun loadRecipes(page: Int = 1, append: Boolean = false) {
        viewModelScope.launch {
            val r = repo
            if (r == null) { _listState.update { it.copy(isLoading = false, error = "未初始化") }; return@launch }
            val state = _listState.value
            _listState.update { it.copy(isLoading = !append && page == 1) }

            try {
                val result = if (state.query.isNotBlank()) {
                    r.searchRecipes(state.query, page, 20)
                } else {
                    r.getRecipes(page, 20, state.selectedCategory)
                }
                val items = if (append) state.items + result.items else result.items
                _listState.update { it.copy(
                    items = items, page = result.page, totalPages = result.totalPages,
                    total = result.total, isLoading = false, error = null,
                ) }
            } catch (e: Exception) {
                _listState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadMore() {
        val state = _listState.value
        if (state.page < state.totalPages && !state.isLoading) {
            loadRecipes(state.page + 1, append = true)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val r = repo ?: return@launch
            _listState.update { it.copy(isRefreshing = true) }
            try {
                val state = _listState.value
                val result = if (state.query.isNotBlank()) {
                    r.searchRecipes(state.query, 1, 20)
                } else {
                    r.getRecipes(1, 20, state.selectedCategory)
                }
                _listState.update { it.copy(
                    items = result.items, page = result.page, totalPages = result.totalPages,
                    total = result.total, isRefreshing = false, error = null,
                ) }
            } catch (e: Exception) {
                _listState.update { it.copy(isRefreshing = false, error = e.message) }
            }
        }
    }

    fun selectCategory(slug: String?) {
        _listState.update { it.copy(selectedCategory = slug, query = "") }
        loadRecipes(1)
    }

    fun search(q: String) {
        _listState.update { it.copy(query = q, selectedCategory = null) }
        loadRecipes(1)
    }

    fun clearSearch() {
        _listState.update { it.copy(query = "") }
        loadRecipes(1)
    }

    fun loadDetail(id: Long) {
        viewModelScope.launch {
            val r = repo
            if (r == null) { _detailState.update { RecipeDetailState(error = "未初始化") }; return@launch }
            _detailState.update { RecipeDetailState(isLoading = true) }
            try {
                val detail = r.getRecipeDetail(id)
                _detailState.update { RecipeDetailState(detail = detail, isLoading = false) }
            } catch (e: Exception) {
                _detailState.update { RecipeDetailState(isLoading = false, error = e.message) }
            }
        }
    }
}

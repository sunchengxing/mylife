package com.mylife.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mylife.app.data.recipe.CategoryDto
import com.mylife.app.data.recipe.RecipeDto
import com.mylife.app.viewmodel.RecipeListState
import com.mylife.app.viewmodel.RecipeViewModel

@Composable
fun FoodScreen(
    vm: RecipeViewModel = viewModel(),
    onRecipeClick: (Long) -> Unit = {},
) {
    val state by vm.listState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = state.query,
            onValueChange = { vm.search(it) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            placeholder = { Text("搜索菜谱...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                if (state.query.isNotBlank()) {
                    IconButton(onClick = { vm.clearSearch() }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
        )

        // Category chips
        CategoryBar(
            categories = state.categories,
            selected = state.selectedCategory,
            onSelect = { vm.selectCategory(it) },
        )

        // Recipe grid
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.isLoading && state.items.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            } else if (state.items.isEmpty() && !state.isLoading) {
                Text(
                    "暂无菜谱",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                RecipeGrid(
                    recipes = state.items,
                    onClick = onRecipeClick,
                    onLoadMore = { vm.loadMore() },
                    canLoadMore = state.page < state.totalPages,
                )
            }
        }
    }
}

@Composable
private fun CategoryBar(
    categories: List<CategoryDto>,
    selected: String?,
    onSelect: (String?) -> Unit,
) {
    ScrollableTabRow(
        selectedTabIndex = categories.indexOfFirst { it.slug == selected }.coerceAtLeast(0),
        edgePadding = 12.dp,
        divider = {},
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxWidth(),
    ) {
        // "All" tab
        Tab(
            selected = selected == null,
            onClick = { onSelect(null) },
            text = { Text("全部", fontWeight = if (selected == null) FontWeight.Bold else FontWeight.Normal) },
        )
        categories.forEach { cat ->
            Tab(
                selected = selected == cat.slug,
                onClick = { onSelect(cat.slug) },
                text = { Text(cat.name, fontWeight = if (selected == cat.slug) FontWeight.Bold else FontWeight.Normal) },
            )
        }
    }
}

@Composable
private fun RecipeGrid(
    recipes: List<RecipeDto>,
    onClick: (Long) -> Unit,
    onLoadMore: () -> Unit,
    canLoadMore: Boolean,
) {
    val gridState = rememberLazyStaggeredGridState()

    // Infinite scroll detection
    LaunchedEffect(gridState, recipes.size) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisible ->
                if (lastVisible != null && lastVisible >= recipes.size - 4 && canLoadMore) {
                    onLoadMore()
                }
            }
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        modifier = Modifier.fillMaxSize(),
    ) {
        items(recipes, key = { it.id }) { recipe ->
            RecipeCard(recipe = recipe, onClick = { onClick(recipe.id) })
        }
        if (canLoadMore) {
            item(span = StaggeredGridCells.Full) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                }
            }
        }
    }
}

@Composable
private fun RecipeCard(recipe: RecipeDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            // Cover image with gradient overlay
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
            ) {
                if (recipe.coverImg.isNotBlank()) {
                    AsyncImage(
                        model = recipe.coverImg,
                        contentDescription = recipe.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp, max = 240.dp),
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                    }
                }
                // Bottom gradient for text readability
                Box(
                    modifier = Modifier.fillMaxWidth().height(48.dp).align(Alignment.BottomCenter)
                        .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)))),
                )
            }

            // Text content
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    recipe.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (recipe.category.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                        ) {
                            Text(
                                categoryName(recipe.category),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                    if (recipe.favorites > 0) {
                        Text(
                            formatCount(recipe.favorites),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        )
                    }
                }
            }
        }
    }
}

private val CATEGORY_MAP = mapOf(
    "c-zaocan" to "早餐", "c-wancan" to "晚餐", "c-wucan" to "午餐",
    "c-yexiao" to "夜宵", "c-xiawucha" to "下午茶", "c-jiachangcai" to "家常菜",
    "c-kuaisu" to "快手菜", "c-chuangyi" to "创意菜", "c-suoshi" to "素食",
    "c-liangcai" to "凉菜", "c-recai" to "热菜", "c-tanggeng" to "汤羹",
    "c-zhushi" to "主食", "c-xiaochi" to "小吃", "c-hongpei" to "烘焙",
    "c-yinliao" to "饮料", "c-tese" to "特色", "c-jieri" to "节日",
)

private fun categoryName(slug: String): String = CATEGORY_MAP[slug] ?: slug.removePrefix("c-")

private fun formatCount(n: Int): String = when {
    n >= 10000 -> "%.1fw".format(n / 10000.0)
    n >= 1000 -> "%.1fk".format(n / 1000.0)
    else -> n.toString()
}

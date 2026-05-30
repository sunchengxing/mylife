package com.mylife.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mylife.app.data.recipe.RecipeDetailDto
import com.mylife.app.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    vm: RecipeViewModel = viewModel(),
    onBack: () -> Unit,
) {
    val state by vm.detailState.collectAsState()

    LaunchedEffect(recipeId) { vm.loadDetail(recipeId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.detail?.recipe?.title ?: "菜谱详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.error != null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error)
            }
            state.detail != null -> DetailContent(state.detail!!, Modifier.padding(padding))
        }
    }
}

@Composable
private fun DetailContent(detail: RecipeDetailDto, modifier: Modifier = Modifier) {
    val recipe = detail.recipe
    Column(modifier.verticalScroll(rememberScrollState())) {
        // Cover image
        if (recipe.coverImg.isNotBlank()) {
            AsyncImage(
                model = recipe.coverImg,
                contentDescription = recipe.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(280.dp),
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Title
            Text(recipe.title, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(8.dp))

            // Stats row
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (recipe.favorites > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(4.dp))
                        Text(formatCount(recipe.favorites), style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (recipe.views > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.RemoveRedEye, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(4.dp))
                        Text("${recipe.views}浏览", style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (recipe.author.isNotBlank()) {
                    Text("by ${recipe.author}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Tip
            if (recipe.tip.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                ) {
                    Text(
                        recipe.tip,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
            }

            // Main ingredients
            if (detail.mainIngredients.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                SectionHeader("主料")
                IngredientChips(detail.mainIngredients.map { "${it.name} ${it.amount}".trim() })
            }

            // Sub ingredients
            if (detail.subIngredients.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                SectionHeader("辅料")
                IngredientChips(detail.subIngredients.map { "${it.name} ${it.amount}".trim() })
            }

            // Steps
            if (detail.steps.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                SectionHeader("做法步骤")
                detail.steps.forEach { step ->
                    StepCard(step.stepNo, step.description, step.imageUrl)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun IngredientChips(names: List<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        names.forEach { name ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Text(
                    name,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}

@Composable
private fun StepCard(num: Int, description: String, imageUrl: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        // Step number
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("$num", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            if (imageUrl.isNotBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "步骤$num",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)),
                )
                Spacer(Modifier.height(6.dp))
            }
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun formatCount(n: Int): String = when {
    n >= 10000 -> "%.1fw".format(n / 10000.0)
    n >= 1000 -> "%.1fk".format(n / 1000.0)
    else -> n.toString()
}

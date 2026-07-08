package com.hritik.recipevault.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hritik.recipevault.R
import com.hritik.recipevault.domain.model.Recipe
import com.hritik.recipevault.ui.components.BottomNavigationBar
import com.hritik.recipevault.ui.components.ConfirmationDialog
import com.hritik.recipevault.ui.components.EmptyState
import com.hritik.recipevault.ui.components.SearchBar
import com.hritik.recipevault.ui.screen.home.components.RecipeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddRecipe: () -> Unit,
    onNavigateToEditRecipe: (Long) -> Unit,
    onNavigateToRecipeDetail: (Long) -> Unit,
    onNavigateToCollections: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var recipeToDelete by remember { mutableStateOf<Recipe?>(null) }
    val orangeColor = Color(0xFFE67E22)
    val backgroundColor = Color(0xFFFDF5F0)
    val brownColor = Color(0xFF5D4037)

    Scaffold(
        containerColor = backgroundColor,
        contentWindowInsets = WindowInsets(0), // Prevent double padding
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(id = R.string.home_title),
                            fontWeight = FontWeight.Bold,
                            color = brownColor
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(id = R.string.menu_desc),
                            tint = brownColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search_desc),
                            tint = brownColor
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0) // Remove internal status bar padding
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddRecipe,
                containerColor = orangeColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_recipe_desc)
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "home",
                onCollectionsClick = onNavigateToCollections,
                onProfileClick = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.categories) { category ->
                    val isSelected = category == state.selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onCategorySelect(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = orangeColor,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF6D6D6D).copy(alpha = 0.8f),
                            labelColor = Color.White
                        ),
                        border = null,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Text(
                text = stringResource(id = R.string.your_culinary_haven),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                color = Color.Black
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = orangeColor)
                }
            } else if (state.recipes.isEmpty()) {
                EmptyState(
                    message = if (state.searchQuery.isEmpty()) stringResource(id = R.string.empty_recipes)
                    else stringResource(id = R.string.search_empty)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(state.recipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onNavigateToRecipeDetail(recipe.id) },
                            onEdit = { onNavigateToEditRecipe(recipe.id) },
                            onDelete = { recipeToDelete = recipe },
                            onFavoriteToggle = { viewModel.toggleFavorite(recipe) }
                        )
                    }
                }
            }
        }
    }

    recipeToDelete?.let { recipe ->
        ConfirmationDialog(
            title = stringResource(id = R.string.delete_recipe_title),
            message = stringResource(id = R.string.delete_recipe_msg, recipe.recipeName),
            onConfirm = {
                viewModel.deleteRecipe(recipe)
                recipeToDelete = null
            },
            onDismiss = { recipeToDelete = null }
        )
    }
}

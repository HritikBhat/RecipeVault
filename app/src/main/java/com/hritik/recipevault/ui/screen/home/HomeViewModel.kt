package com.hritik.recipevault.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritik.recipevault.data.repository.CollectionRepository
import com.hritik.recipevault.data.repository.RecipeRepository
import com.hritik.recipevault.domain.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _allRecipes = MutableStateFlow<List<Recipe>>(emptyList())

    init {
        getRecipes()
        getCollections()
    }

    private fun getRecipes() {
        _state.update { it.copy(isLoading = true) }
        repository.getAllRecipes()
            .onEach { recipes ->
                _allRecipes.value = recipes
                applyFilters()
            }
            .catch { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }

    private fun getCollections() {
        collectionRepository.getAllCollections()
            .onEach { collections ->
                val categoryNames = listOf("All") + collections.map { it.name }
                _state.update { it.copy(categories = categoryNames) }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onCategorySelect(category: String) {
        _state.update { it.copy(selectedCategory = category) }
        applyFilters()
    }

    private fun applyFilters() {
        val query = _state.value.searchQuery
        val category = _state.value.selectedCategory
        
        val filtered = _allRecipes.value.filter { recipe ->
            val matchesQuery = recipe.recipeName.contains(query, ignoreCase = true) ||
                               recipe.ingredients.any { it.ingredientName.contains(query, ignoreCase = true) }
            val matchesCategory = category == "All" || recipe.category == category
            
            matchesQuery && matchesCategory
        }
        
        _state.update { it.copy(recipes = filtered, isLoading = false) }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
        }
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            repository.insertRecipe(recipe.copy(isFavorite = !recipe.isFavorite))
        }
    }
}

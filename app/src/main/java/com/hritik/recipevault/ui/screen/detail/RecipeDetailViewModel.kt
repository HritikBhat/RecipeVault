package com.hritik.recipevault.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritik.recipevault.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeDetailUiState())
    val state: StateFlow<RecipeDetailUiState> = _state.asStateFlow()

    init {
        savedStateHandle.get<String>("recipeId")?.let { id ->
            loadRecipe(id.toLong())
        }
    }

    private fun loadRecipe(id: Long) {
        _state.update { it.copy(isLoading = true) }
        repository.getRecipeById(id)
            .onEach { recipe ->
                _state.update { it.copy(recipe = recipe, isLoading = false) }
            }
            .catch { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun deleteRecipe(onSuccess: () -> Unit) {
        state.value.recipe?.let { recipe ->
            viewModelScope.launch {
                repository.deleteRecipe(recipe)
                onSuccess()
            }
        }
    }
}

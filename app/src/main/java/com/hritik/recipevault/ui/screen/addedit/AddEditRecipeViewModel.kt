package com.hritik.recipevault.ui.screen.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritik.recipevault.R
import com.hritik.recipevault.data.repository.CollectionRepository
import com.hritik.recipevault.data.repository.RecipeRepository
import com.hritik.recipevault.domain.model.Collection
import com.hritik.recipevault.domain.model.Ingredient
import com.hritik.recipevault.domain.model.Recipe
import com.hritik.recipevault.domain.model.Step
import com.hritik.recipevault.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val collectionRepository: CollectionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditRecipeUiState())
    val state: StateFlow<AddEditRecipeUiState> = _state.asStateFlow()

    private var currentRecipeId: Long = 0

    init {
        savedStateHandle.get<String>("recipeId")?.let { id ->
            if (id != "-1" && id.toLongOrNull() != null) {
                currentRecipeId = id.toLong()
                loadRecipe(currentRecipeId)
            }
        }
        observeCollections()
    }

    private fun observeCollections() {
        collectionRepository.getAllCollections()
            .onEach { collections ->
                _state.update { it.copy(collections = collections.map { it.name }) }
                // Set default category if empty and current category is empty
                if (_state.value.category.isEmpty() && collections.isNotEmpty()) {
                    _state.update { it.copy(category = collections.first().name) }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadRecipe(id: Long) {
        viewModelScope.launch {
            recipeRepository.getRecipeById(id).firstOrNull()?.let { recipe ->
                _state.update { state ->
                    state.copy(
                        recipeName = recipe.recipeName,
                        imageUri = recipe.imageUri,
                        cookingTime = recipe.cookingTime,
                        difficulty = recipe.difficulty,
                        category = recipe.category,
                        isFavorite = recipe.isFavorite,
                        ingredients = recipe.ingredients,
                        steps = recipe.steps,
                        createdAt = recipe.createdAt
                    )
                }
            }
        }
    }

    fun onRecipeNameChange(name: String) {
        _state.update { it.copy(recipeName = name) }
    }

    fun onImageUriChange(uri: String) {
        _state.update { it.copy(imageUri = uri) }
    }

    fun onCookingTimeChange(time: String) {
        _state.update { it.copy(cookingTime = time) }
    }

    fun onDifficultyChange(difficulty: String) {
        _state.update { it.copy(difficulty = difficulty) }
    }

    fun onCategoryChange(category: String) {
        _state.update { it.copy(category = category) }
    }

    fun addCollection(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            collectionRepository.insertCollection(Collection(name = name))
            _state.update { it.copy(category = name) }
        }
    }

    fun addIngredient(name: String, quantity: String, unit: String) {
        val newIngredient = Ingredient(
            ingredientName = name,
            quantity = quantity,
            unit = unit,
            position = _state.value.ingredients.size
        )
        _state.update { it.copy(ingredients = it.ingredients + newIngredient) }
    }

    fun updateIngredient(index: Int, name: String, quantity: String, unit: String) {
        val updatedList = _state.value.ingredients.toMutableList()
        updatedList[index] = updatedList[index].copy(
            ingredientName = name,
            quantity = quantity,
            unit = unit
        )
        _state.update { it.copy(ingredients = updatedList) }
    }

    fun deleteIngredient(index: Int) {
        val updatedList = _state.value.ingredients.toMutableList()
        updatedList.removeAt(index)
        val reorderedList = updatedList.mapIndexed { i, ingredient ->
            ingredient.copy(position = i)
        }
        _state.update { it.copy(ingredients = reorderedList) }
    }

    fun addStep(description: String) {
        val newStep = Step(
            description = description,
            position = _state.value.steps.size
        )
        _state.update { it.copy(steps = it.steps + newStep) }
    }

    fun updateStep(index: Int, description: String) {
        val updatedList = _state.value.steps.toMutableList()
        updatedList[index] = updatedList[index].copy(description = description)
        _state.update { it.copy(steps = updatedList) }
    }

    fun deleteStep(index: Int) {
        val updatedList = _state.value.steps.toMutableList()
        updatedList.removeAt(index)
        val reorderedList = updatedList.mapIndexed { i, step ->
            step.copy(position = i)
        }
        _state.update { it.copy(steps = reorderedList) }
    }

    fun saveRecipe() {
        val currentState = _state.value
        
        // Validation using UiText
        if (currentState.recipeName.isBlank()) {
            _state.update { it.copy(error = UiText.StringResource(R.string.err_empty_name)) }
            return
        }
        if (currentState.ingredients.isEmpty()) {
            _state.update { it.copy(error = UiText.StringResource(R.string.err_no_ingredients)) }
            return
        }
        if (currentState.steps.isEmpty()) {
            _state.update { it.copy(error = UiText.StringResource(R.string.err_no_steps)) }
            return
        }

        viewModelScope.launch {
            val recipe = Recipe(
                id = currentRecipeId,
                recipeName = currentState.recipeName,
                imageUri = currentState.imageUri,
                cookingTime = currentState.cookingTime,
                difficulty = currentState.difficulty,
                category = currentState.category,
                isFavorite = currentState.isFavorite,
                createdAt = if (currentRecipeId == 0L) System.currentTimeMillis() else currentState.createdAt,
                updatedAt = System.currentTimeMillis(),
                ingredients = currentState.ingredients,
                steps = currentState.steps
            )
            recipeRepository.insertRecipe(recipe)
            _state.update { it.copy(isRecipeSaved = true) }
        }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

package com.hritik.recipevault.ui.screen.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritik.recipevault.data.repository.CollectionRepository
import com.hritik.recipevault.domain.model.Collection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val repository: CollectionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionUiState())
    val state: StateFlow<CollectionUiState> = _state.asStateFlow()

    private val _allCollections = MutableStateFlow<List<Collection>>(emptyList())

    init {
        getCollections()
    }

    private fun getCollections() {
        _state.update { it.copy(isLoading = true) }
        repository.getAllCollections()
            .onEach { collections ->
                _allCollections.value = collections
                applyFilters()
            }
            .catch { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    private fun applyFilters() {
        val query = _state.value.searchQuery
        val filtered = if (query.isBlank()) {
            _allCollections.value
        } else {
            _allCollections.value.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        _state.update { it.copy(collections = filtered, isLoading = false) }
    }

    fun addCollection(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertCollection(Collection(name = name))
        }
    }

    fun updateCollection(collection: Collection, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            repository.updateCollection(collection.copy(name = newName))
        }
    }

    fun deleteCollection(collection: Collection) {
        viewModelScope.launch {
            repository.deleteCollection(collection)
        }
    }
}

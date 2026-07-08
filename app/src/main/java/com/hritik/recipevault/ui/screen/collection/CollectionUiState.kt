package com.hritik.recipevault.ui.screen.collection

import com.hritik.recipevault.domain.model.Collection

data class CollectionUiState(
    val collections: List<Collection> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

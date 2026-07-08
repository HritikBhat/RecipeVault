package com.hritik.recipevault.ui.screen.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hritik.recipevault.R
import com.hritik.recipevault.domain.model.Collection
import com.hritik.recipevault.ui.components.BottomNavigationBar
import com.hritik.recipevault.ui.components.ConfirmationDialog
import com.hritik.recipevault.ui.components.EmptyState
import com.hritik.recipevault.ui.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    viewModel: CollectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    var showAddEditDialog by remember { mutableStateOf(false) }
    var collectionToEdit by remember { mutableStateOf<Collection?>(null) }
    var collectionToDelete by remember { mutableStateOf<Collection?>(null) }
    
    val orangeColor = Color(0xFFE67E22)
    val backgroundColor = Color(0xFFFDF5F0) // Matching HomeScreen
    val brownColor = Color(0xFF5D4037) // Matching HomeScreen
    val lightBrown = Color(0xFF7D6A61)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(id = R.string.nav_collections),
                            fontWeight = FontWeight.Bold,
                            color = brownColor
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0) // Remove internal status bar padding
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    collectionToEdit = null
                    showAddEditDialog = true
                },
                containerColor = orangeColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Collection")
            }
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "collections",
                onHomeClick = onNavigateToHome,
                onProfileClick = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            SearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholder = stringResource(id = R.string.search_collections_placeholder)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.existing_collections_label),
                color = lightBrown,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.collections.isEmpty()) {
                EmptyState(
                    message = if (state.searchQuery.isEmpty()) stringResource(id = R.string.empty_collections)
                    else stringResource(id = R.string.search_empty)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.collections, key = { it.id }) { collection ->
                        CollectionItem(
                            collection = collection,
                            onEdit = {
                                collectionToEdit = collection
                                showAddEditDialog = true
                            },
                            onDelete = { collectionToDelete = collection }
                        )
                    }
                }
            }
        }
    }

    if (showAddEditDialog) {
        CollectionDialog(
            initialName = collectionToEdit?.name ?: "",
            onConfirm = { name ->
                if (name.isNotBlank()) {
                    collectionToEdit?.let {
                        viewModel.updateCollection(it, name)
                    } ?: viewModel.addCollection(name)
                    showAddEditDialog = false
                    collectionToEdit = null
                }
            },
            onDismiss = {
                showAddEditDialog = false
                collectionToEdit = null
            }
        )
    }

    collectionToDelete?.let { collection ->
        ConfirmationDialog(
            title = stringResource(id = R.string.delete_collection_title),
            message = stringResource(id = R.string.delete_collection_msg),
            onConfirm = {
                viewModel.deleteCollection(collection)
                collectionToDelete = null
            },
            onDismiss = { collectionToDelete = null }
        )
    }
}

@Composable
fun CollectionItem(
    collection: Collection,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFEFE6DD), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    tint = Color(0xFF5D4037).copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = collection.name,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = Color.Gray.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun CollectionDialog(
    initialName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialName.isEmpty()) "Add Collection" else "Edit Collection",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037)
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Collection Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE67E22),
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513))
            ) {
                Text(stringResource(id = R.string.confirm_btn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel_btn), color = Color.Gray)
            }
        }
    )
}

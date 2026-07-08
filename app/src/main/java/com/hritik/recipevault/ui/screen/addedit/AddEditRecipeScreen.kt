package com.hritik.recipevault.ui.screen.addedit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hritik.recipevault.R
import com.hritik.recipevault.domain.model.Ingredient
import com.hritik.recipevault.ui.components.CollectionDialog
import com.hritik.recipevault.ui.components.IngredientDialog
import com.hritik.recipevault.ui.components.StepDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecipeScreen(
    onPopBackStack: () -> Unit,
    viewModel: AddEditRecipeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var currentStep by remember { mutableIntStateOf(0) }

    var showIngredientDialog by remember { mutableStateOf(false) }
    var editingIngredientIndex by remember { mutableStateOf<Int?>(null) }

    var showStepDialog by remember { mutableStateOf(false) }
    var editingStepIndex by remember { mutableStateOf<Int?>(null) }

    var collectionExpanded by remember { mutableStateOf(false) }
    var showCollectionDialog by remember { mutableStateOf(false) }

    val backgroundColor = Color(0xFFFDF8F5)
    val brownColor = Color(0xFF8B4513)
    val labelColor = Color(0xFF7D6A61)
    val primaryButtonColor = Color(0xFF8B4513)
    val addStepBtnColor = Color(0xFFEDE4DB)

    LaunchedEffect(state.isRecipeSaved) {
        if (state.isRecipeSaved) {
            onPopBackStack()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it.asString(context))
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (currentStep == 0) {
                                if (state.createdAt == 0L) stringResource(id = R.string.add_recipe_title)
                                else stringResource(id = R.string.edit_recipe_title)
                            } else {
                                stringResource(id = R.string.manage_steps_title)
                            },
                            fontWeight = FontWeight.Bold,
                            color = brownColor,
                            fontSize = 20.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 0) currentStep-- else onPopBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_desc),
                            tint = brownColor
                        )
                    }
                },
                actions = {
                    Box(modifier = Modifier.size(48.dp))
                },
                windowInsets = WindowInsets(0, 0, 0, 0) // Remove internal status bar padding
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = backgroundColor,
                tonalElevation = 0.dp
            ) {
                Button(
                    onClick = {
                        if (currentStep == 0) {
                            if (state.recipeName.isBlank()) {
                                viewModel.saveRecipe()
                            } else {
                                currentStep = 1
                            }
                        } else {
                            viewModel.saveRecipe()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryButtonColor)
                ) {
                    Text(
                        text = if (currentStep == 0) stringResource(id = R.string.next_step_btn)
                        else stringResource(id = R.string.save_recipe_btn),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (currentStep == 0) {
                Spacer(modifier = Modifier.height(16.dp))

                // Recipe Name
                Text(
                    text = stringResource(id = R.string.recipe_name_label),
                    color = labelColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.recipeName,
                    onValueChange = viewModel::onRecipeNameChange,
                    placeholder = { Text(stringResource(id = R.string.recipe_name_placeholder), color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Collection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Collection",
                        color = labelColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    TextButton(
                        onClick = { showCollectionDialog = true },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp), tint = brownColor)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Collection", color = brownColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (state.collections.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = collectionExpanded,
                        onExpandedChange = { collectionExpanded = !collectionExpanded }
                    ) {
                        OutlinedTextField(
                            value = state.category,
                            onValueChange = { },
                            readOnly = true,
                            placeholder = { Text("Select a collection", color = Color.LightGray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = collectionExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color.LightGray,
                                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = collectionExpanded,
                            onDismissRequest = { collectionExpanded = false }
                        ) {
                            state.collections.forEach { collection ->
                                DropdownMenuItem(
                                    text = { Text(collection) },
                                    onClick = {
                                        viewModel.onCategoryChange(collection)
                                        collectionExpanded = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp), tint = brownColor)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Add New Collection", color = brownColor, fontWeight = FontWeight.Bold)
                                    }
                                },
                                onClick = {
                                    showCollectionDialog = true
                                    collectionExpanded = false
                                }
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCollectionDialog = true }
                    ) {
                        OutlinedTextField(
                            value = state.category,
                            onValueChange = { },
                            readOnly = true,
                            enabled = false,
                            placeholder = { Text("Select a collection", color = Color.LightGray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledContainerColor = Color.White,
                                disabledBorderColor = Color.LightGray.copy(alpha = 0.5f),
                                disabledPlaceholderColor = Color.LightGray,
                                disabledTextColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Cover Image
                Text(
                    text = stringResource(id = R.string.cover_image_label),
                    color = labelColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                DashedUploadBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    onClick = { viewModel.onImageUriChange("https://images.unsplash.com/photo-1509440159596-0249088772ff") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Ingredients
                Text(
                    text = stringResource(id = R.string.ingredients_label),
                    color = labelColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                state.ingredients.forEachIndexed { index, ingredient ->
                    IngredientItem(
                        ingredient = ingredient,
                        onEdit = {
                            editingIngredientIndex = index
                            showIngredientDialog = true
                        },
                        onDelete = { viewModel.deleteIngredient(index) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedButton(
                    onClick = {
                        editingIngredientIndex = null
                        showIngredientDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, brownColor),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = brownColor)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = R.string.add_ingredient_btn), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            } else {
                // Step 2: Manage Steps
                Spacer(modifier = Modifier.height(16.dp))

                state.steps.forEachIndexed { index, step ->
                    StepCard(
                        index = index + 1,
                        description = step.description,
                        brownColor = brownColor,
                        onEdit = {
                            editingStepIndex = index
                            showStepDialog = true
                        },
                        onDelete = { viewModel.deleteStep(index) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clickable {
                            editingStepIndex = null
                            showStepDialog = true
                        }
                        .background(addStepBtnColor.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                        .drawBehind {
                            drawRoundRect(
                                color = Color.Gray.copy(alpha = 0.3f),
                                style = Stroke(
                                    width = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                ),
                                cornerRadius = CornerRadius(16.dp.toPx())
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = labelColor, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.add_step_btn), color = labelColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showIngredientDialog) {
        val ingredient = editingIngredientIndex?.let { state.ingredients[it] }
        IngredientDialog(
            initialName = ingredient?.ingredientName ?: "",
            initialQuantity = ingredient?.quantity ?: "",
            initialUnit = ingredient?.unit ?: "qty",
            onConfirm = { name, quantity, unit ->
                editingIngredientIndex?.let {
                    viewModel.updateIngredient(it, name, quantity, unit)
                } ?: viewModel.addIngredient(name, quantity, unit)
            },
            onDismiss = { showIngredientDialog = false }
        )
    }

    if (showStepDialog) {
        StepDialog(
            initialDescription = editingStepIndex?.let { state.steps[it].description } ?: "",
            onConfirm = { description ->
                editingStepIndex?.let {
                    viewModel.updateStep(it, description)
                } ?: viewModel.addStep(description)
            },
            onDismiss = { showStepDialog = false }
        )
    }
    
    if (showCollectionDialog) {
        CollectionDialog(
            onConfirm = { name ->
                viewModel.addCollection(name)
                showCollectionDialog = false
            },
            onDismiss = { showCollectionDialog = false }
        )
    }
}

@Composable
fun StepCard(
    index: Int,
    description: String,
    brownColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Step $index",
                    color = brownColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = stringResource(id = R.string.edit_label), tint = Color.Black.copy(alpha = 0.6f), modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Outlined.Delete, contentDescription = stringResource(id = R.string.delete_label), tint = Color.Black.copy(alpha = 0.6f), modifier = Modifier.size(22.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
                    .drawBehind {
                        val handleSize = 8.dp.toPx()
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.8f),
                            start = Offset(size.width - handleSize, size.height),
                            end = Offset(size.width, size.height - handleSize),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.8f),
                            start = Offset(size.width - handleSize * 0.6f, size.height),
                            end = Offset(size.width, size.height - handleSize * 0.6f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            ) {
                Text(
                    text = description,
                    color = Color.Black.copy(alpha = 0.8f),
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun DashedUploadBox(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(Color(0xFFFFF1EE), RoundedCornerShape(12.dp))
            .drawBehind {
                drawRoundRect(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    ),
                    cornerRadius = CornerRadius(12.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(id = R.string.tap_to_upload), color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun IngredientItem(
    ingredient: Ingredient,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = ingredient.ingredientName,
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            if (ingredient.quantity.isNotBlank()) {
                Text(
                    text = "${ingredient.quantity} ${ingredient.unit}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Outlined.Edit, contentDescription = stringResource(id = R.string.edit_label), tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Outlined.Delete, contentDescription = stringResource(id = R.string.delete_label), tint = Color(0xFFD32F2F).copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
            }
        }
    }
}

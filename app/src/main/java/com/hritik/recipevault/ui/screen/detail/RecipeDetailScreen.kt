package com.hritik.recipevault.ui.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.hritik.recipevault.R
import com.hritik.recipevault.domain.model.Ingredient

@Composable
fun RecipeDetailScreen(
    onPopBackStack: () -> Unit,
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFFDF0E5))
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.recipe != null) {
                val recipe = state.recipe!!
                val pageCount = 1 + recipe.steps.size
                val pagerState = rememberPagerState(pageCount = { pageCount })

                Column(modifier = Modifier.fillMaxSize()) {
                    // Custom Header with Segmented Progress Bar
                    Column {
                        StepProgressBar(
                            pageCount = pageCount,
                            currentPage = pagerState.currentPage
                        )
                        RecipeDetailHeader(
                            recipeName = recipe.recipeName,
                            onClose = onPopBackStack,
                            onMore = { /* Options menu */ }
                        )
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.Top
                    ) { page ->
                        if (page == 0) {
                            IngredientsPage(
                                recipeName = recipe.recipeName,
                                imageUri = recipe.imageUri,
                                ingredients = recipe.ingredients
                            )
                        } else {
                            val step = recipe.steps[page - 1]
                            StepPage(
                                stepIndex = page,
                                description = step.description,
                                isLastStep = page == pageCount - 1,
                                onFinish = onPopBackStack
                            )
                        }
                    }
                }
            } else if (state.error != null) {
                Text(
                    text = stringResource(id = R.string.error_msg, state.error!!),
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun StepProgressBar(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(pageCount) { i ->
            val color = if (i <= currentPage) Color(0xFFE67E22) else Color(0xFFE67E22).copy(alpha = 0.2f)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun RecipeDetailHeader(
    recipeName: String,
    onClose: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.05f), CircleShape)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
        }

        Surface(
            color = Color.Black.copy(alpha = 0.05f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = recipeName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20)
                )
            }
        }

        IconButton(
            onClick = onMore,
            modifier = Modifier
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.05f), CircleShape)
        ) {
            Icon(Icons.Default.MoreVert, contentDescription = "More")
        }
    }
}

@Composable
fun IngredientsPage(
    recipeName: String,
    imageUri: String?,
    ingredients: List<Ingredient>
) {
    val checkedStates = remember { mutableStateMapOf<Long, Boolean>() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = recipeName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(bottom = 24.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
        item {
            Text(
                text = stringResource(id = R.string.ingredients_label),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        items(ingredients) { ingredient ->
            val isChecked = checkedStates[ingredient.id] ?: false
            Surface(
                onClick = { checkedStates[ingredient.id] = !isChecked },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF2EBE1),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = Color(0xFFE67E22),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    val displayText = buildString {
                        if (ingredient.quantity.isNotEmpty()) append("${ingredient.quantity} ")
                        if (ingredient.unit.isNotEmpty()) append("${ingredient.unit} ")
                        append(ingredient.ingredientName)
                    }.trim()
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF1D1B20)
                    )
                }
            }
        }
    }
}

@Composable
fun StepPage(
    stepIndex: Int,
    description: String,
    isLastStep: Boolean,
    onFinish: () -> Unit
) {
    // Attempt to split description into title and body
    val parts = if (description.contains(": ")) {
        description.split(": ", limit = 2)
    } else if (description.contains("\n")) {
        description.split("\n", limit = 2)
    } else {
        listOf(description)
    }

    val title = parts.getOrNull(0) ?: ""
    val body = parts.getOrNull(1) ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.step_indicator_label, stepIndex),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE67E22)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1D1B20),
            textAlign = TextAlign.Center
        )
        if (body.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, lineHeight = 26.sp),
                textAlign = TextAlign.Center,
                color = Color(0xFF49454F),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        if (isLastStep) {
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = onFinish,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(0.7f)
            ) {
                Icon(Icons.Default.Restaurant, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Finish Cooking", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

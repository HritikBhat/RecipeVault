package com.hritik.recipevault.ui.screen.premium

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hritik.recipevault.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    onNavigateBack: () -> Unit,
    viewModel: PremiumViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val showConfirmationDialog by viewModel.showConfirmationDialog.collectAsStateWithLifecycle()
    
    val backgroundColor = Color(0xFFFDF5F0)
    val brownColor = Color(0xFF5D4037)
    val diamondColor = Color(0xFF00B4D8)

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is PremiumUiState.Success -> {
                Toast.makeText(context, context.getString(R.string.premium_success), Toast.LENGTH_SHORT).show()
                viewModel.dismissState()
            }
            is PremiumUiState.Error -> {
                Toast.makeText(context, state.uiText.asString(context), Toast.LENGTH_LONG).show()
                viewModel.dismissState()
            }
            else -> Unit
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissConfirmationDialog() },
            title = { Text(text = stringResource(R.string.confirm_account_title)) },
            text = { 
                Text(
                    text = stringResource(
                        R.string.confirm_account_msg, 
                        viewModel.currentUserEmail ?: stringResource(R.string.unknown_account)
                    )
                ) 
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmPurchase(context as Activity) }) {
                    Text(stringResource(R.string.confirm_btn))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissConfirmationDialog() }) {
                    Text(stringResource(R.string.cancel_btn))
                }
            }
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        contentWindowInsets = WindowInsets(0), // Prevent double padding
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                title = { Text(stringResource(R.string.premium_title), fontWeight = FontWeight.Bold, color = brownColor) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_desc),
                            tint = brownColor
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0) // Remove internal status bar padding
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(diamondColor.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = diamondColor
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = if (isPremium) stringResource(R.string.premium_active) else stringResource(R.string.premium_title),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = brownColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.premium_desc),
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(48.dp))

                if (!isPremium) {
                    Button(
                        onClick = { viewModel.onPurchaseClick() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = brownColor),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(stringResource(R.string.buy_premium), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = { viewModel.restorePurchase() }) {
                        Text(stringResource(R.string.restore_purchase), color = brownColor)
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = null,
                        tint = diamondColor,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = stringResource(R.string.pro_member_status),
                        fontWeight = FontWeight.Medium,
                        color = brownColor
                    )
                }
            }

            if (uiState is PremiumUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = brownColor
                )
            }
        }
    }
}

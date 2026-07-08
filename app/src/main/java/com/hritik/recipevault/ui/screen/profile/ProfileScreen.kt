package com.hritik.recipevault.ui.screen.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.hritik.recipevault.R
import com.hritik.recipevault.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToCollections: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToPremium: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    val backgroundColor = Color(0xFFFDF5F0)
    val brownColor = Color(0xFF5D4037)
    val primaryAppColor = Color(0xFF5D4037)

    var showImportDialog by remember { mutableStateOf(false) }
    var selectedImportUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri ->
            uri?.let {
                viewModel.exportData(context.contentResolver, it)
            }
        }
    )

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                selectedImportUri = it
                showImportDialog = true
            }
        }
    )

    // Handle UI State changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is BackupUiState.Success -> {
                Toast.makeText(context, (uiState as BackupUiState.Success).message, Toast.LENGTH_SHORT).show()
                viewModel.resetUiState()
            }
            is BackupUiState.Error -> {
                Toast.makeText(context, (uiState as BackupUiState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetUiState()
            }
            else -> {}
        }
    }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Data") },
            text = { Text("Importing data may overwrite your existing data. Do you want to continue?") },
            confirmButton = {
                TextButton(onClick = {
                    showImportDialog = false
                    selectedImportUri?.let {
                        viewModel.importData(context.contentResolver, it)
                    }
                }) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (uiState is BackupUiState.Loading) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryAppColor)
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(id = R.string.nav_profile),
                            fontWeight = FontWeight.Bold,
                            color = brownColor
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "profile",
                onHomeClick = onNavigateToHome,
                onCollectionsClick = onNavigateToCollections
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Profile Image / Avatar
            if (user?.photoUrl != null) {
                AsyncImage(
                    model = user?.photoUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFF8B6B5E), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user?.displayName?.firstOrNull()?.toString()?.uppercase() ?: "U",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = user?.displayName ?: "User",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = user?.email ?: "",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Profile Options
            ProfileOptionItem(
                icon = Icons.Default.Star,
                title = "Get a Pro",
                iconColor = primaryAppColor,
                onClick = onNavigateToPremium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ProfileOptionItem(
                icon = Icons.Default.Language,
                title = "Change Language",
                trailingText = "English",
                iconColor = primaryAppColor,
                onClick = {}
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileOptionItem(
                icon = Icons.Default.FileDownload,
                title = "Import Data",
                iconColor = primaryAppColor,
                onClick = {
                    importLauncher.launch(arrayOf("application/json"))
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileOptionItem(
                icon = Icons.Default.FileUpload,
                title = "Export Data",
                iconColor = primaryAppColor,
                onClick = {
                    exportLauncher.launch("recipe_vault_backup.json")
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileOptionItem(
                icon = Icons.Default.Share,
                title = "Share App",
                iconColor = primaryAppColor,
                onClick = {}
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileOptionItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                title = "Logout",
                titleColor = Color(0xFFD32F2F),
                iconColor = Color(0xFFD32F2F),
                onClick = {
                    viewModel.logout {
                        onNavigateToLogin()
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    titleColor: Color = Color.Black,
    trailingText: String? = null,
    iconColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = titleColor
            )
            
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    color = iconColor.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Color.Gray.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

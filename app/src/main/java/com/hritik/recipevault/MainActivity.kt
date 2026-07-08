package com.hritik.recipevault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.hritik.recipevault.navigation.NavGraph
import com.hritik.recipevault.navigation.Screen
import com.hritik.recipevault.ui.theme.RecipeVaultTheme
import com.hritik.recipevault.util.ad.BannerAd
import com.hritik.recipevault.util.ad.InterstitialAdHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Mobile Ads SDK
        MobileAds.initialize(this) {}
        
        setContent {
            val startDestination by viewModel.startDestination.collectAsState()
            
            RecipeVaultTheme {
                if (startDestination != null) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    
                    // Show ads only if we're not on the login screen
                    val isAdAllowed = currentRoute != Screen.Login.route
                    
                    // Handle Interstitial Ads globally
                    InterstitialAdHandler(isAdAllowed = isAdAllowed)
                    
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            if (isAdAllowed) {
                                BannerAd()
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            NavGraph(
                                navController = navController,
                                startDestination = startDestination!!
                            )
                        }
                    }
                }
            }
        }
    }
}

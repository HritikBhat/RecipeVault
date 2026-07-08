package com.hritik.recipevault.util.ad

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.hritik.recipevault.util.Global

val LocalIsPro = compositionLocalOf { false }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val isPro = LocalIsPro.current
    val isKeyboardVisible = WindowInsets.isImeVisible
    
    if (!isPro && Global.IS_AD_ENABLED && !isKeyboardVisible) {
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            factory = { ctx ->
                AdView(ctx).apply {
                    // Use adaptive banner size for full width
                    val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                        ctx,
                        (ctx.resources.displayMetrics.widthPixels / ctx.resources.displayMetrics.density).toInt()
                    )
                    setAdSize(adSize)

                    // Use Test Ad Unit ID if app is on test, otherwise use production ID
                    adUnitId = if (Global.IS_TEST_MODE) {
                        "ca-app-pub-3940256099942544/6300978111"
                    } else {
                        // Replace with your real production Ad Unit ID
                        "ca-app-pub-4549313342341988/7027527053"
                    }
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

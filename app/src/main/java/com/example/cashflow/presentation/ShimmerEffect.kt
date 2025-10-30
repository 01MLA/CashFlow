package com.example.cashflow.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun Modifier.shimmerEffect(enabled: Boolean): Modifier {
    return if (enabled) {
        val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.Window)
        this.shimmer(shimmerInstance)
    } else {
        this
    }
}

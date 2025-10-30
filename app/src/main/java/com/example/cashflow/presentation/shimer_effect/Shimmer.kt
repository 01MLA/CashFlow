package com.example.cashflow.presentation.shimer_effect

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

/**
 * Provides an infinite shimmering animation value from -1f → 2f.
 * This value is used to shift a gradient brush over a composable.
 *
 * @param durationMillis Duration of one shimmer sweep
 */
@Composable
fun rememberShimmerAnimation(durationMillis: Int = 1200): Float {
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val animatedValue by transition.animateFloat(
        initialValue = -1f, targetValue = 2f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmerValue"
    )
    return animatedValue
}

/**
 * Central shimmer configuration model.
 * Can be passed through theme or modifier directly.
 */
data class ShimmerConfig(
    val baseColor: Color = Color(0xFFDDDDDD),
    val highlightColor: Color = Color(0xFFF5F5F5),
    val cornerRadius: Dp = 8.dp,
    val durationMillis: Int = 1200
)

/**
 * Modifier extension that applies a shimmer effect background.
 *
 * Example:
 * Modifier
 *     .fillMaxWidth()
 *     .height(120.dp)
 *     .shimmer()
 */
fun Modifier.shimmer(config: ShimmerConfig = ShimmerConfig()): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val animatedValue = rememberShimmerAnimation(config.durationMillis)

    val brush = remember(size, animatedValue) {
        if (size == IntSize.Zero) {
            Brush.linearGradient(listOf(config.baseColor, config.highlightColor, config.baseColor))
        } else {
            val width = size.width.toFloat()
            val startX = animatedValue * width
            val endX = startX + width / 2f
            Brush.linearGradient(
                colors = listOf(config.baseColor, config.highlightColor, config.baseColor),
                start = Offset(startX, 0f),
                end = Offset(endX, 0f)
            )
        }
    }

    this
        .onGloballyPositioned { coordinates -> size = coordinates.size }
        .clip(RoundedCornerShape(config.cornerRadius))
        .background(brush)
}

/**
 * CompositionLocal-based shimmer theming system.
 * Lets you define shimmer config globally within a subtree.
 */

val LocalShimmerConfig = staticCompositionLocalOf { ShimmerConfig() }

@Composable
fun ShimmerTheme(
    config: ShimmerConfig = ShimmerConfig(), content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalShimmerConfig provides config) {
        content()
    }
}

/**
 * Simple wrapper composable that applies shimmer automatically.
 * Makes UI code cleaner and more readable.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit = {}
) {
    val config = LocalShimmerConfig.current
    Box(modifier = modifier.shimmer(config), content = content)
}

/**
 * Basic rectangular shimmer block — e.g., text or image placeholder.
 */
@Composable
fun ShimmerRect(modifier: Modifier = Modifier, height: Int = 16) {
    ShimmerBox(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
    )
}

/**
 * Circular shimmer — e.g., avatar placeholder.
 */
@Composable
fun ShimmerCircle(size: Int = 48) {
    ShimmerBox(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
    )
}

/**
 * Combined shimmer list item — avatar + text lines.
 */
@Composable
fun ShimmerListItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerCircle()
        Spacer(Modifier.width(12.dp))
        Column {
            ShimmerRect(Modifier.fillMaxWidth(0.6f), 14)
            Spacer(Modifier.height(6.dp))
            ShimmerRect(Modifier.fillMaxWidth(0.4f), 12)
        }
    }
}

/**
 * Vertical list of shimmer placeholders — great for loading lists.
 */
@Composable
fun ShimmerListPlaceholder(itemCount: Int = 5) {
    LazyColumn {
        items((0 until itemCount).toList()) {
            ShimmerListItem()
        }
    }
}

/**
 * Determines shimmer colors based on current theme brightness.
 */
@Composable
fun shimmerColors(): Pair<Color, Color> {
    val background = MaterialTheme.colorScheme.background
    val isDark = background.luminance() < 0.5

    val lightBase = Color(0xFFE0E0E0)
    val lightHighlight = Color(0xFFF5F5F5)

    val darkBase = Color(0xFF2A2A2A)
    val darkHighlight = Color(0xFF3A3A3A)

    return if (isDark) darkBase to darkHighlight else lightBase to lightHighlight
}

/**
 * Displays a Coil image with shimmer placeholder during loading.
 */
@Composable
fun ShimmerImage(
    imageUrl: String, modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
) {
    val (base, highlight) = shimmerColors()
    val config = ShimmerConfig(base, highlight, cornerRadius = 12.dp)

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build()
    )

    val isLoading = painter.state is AsyncImagePainter.State.Loading

    Box(modifier = modifier.clip(RoundedCornerShape(config.cornerRadius))) {
        if (isLoading) {
            ShimmerBox(
                Modifier
                    .matchParentSize()
                    .shimmer(config)
            )
        }
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Represents shimmer visibility states.
 * Useful for integrating with ViewModel states.
 */
sealed class ShimmerState {
    object Loading : ShimmerState()
    object Success : ShimmerState()
    object Error : ShimmerState()
}

/**
 * Previews shimmer components for both light and dark modes.
 */
@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun Preview_ShimmerList() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Loading Items...",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        ShimmerListPlaceholder(4)
    }
}

@Composable
fun ProductCard(imageUrl: String, title: String, price: String, state: ShimmerState) {
    ShimmerTheme {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            when (state) {
                is ShimmerState.Loading -> {
                    ShimmerImage(imageUrl = "")
                    Spacer(Modifier.height(8.dp))
                    ShimmerRect(height = 14)
                    Spacer(Modifier.height(6.dp))
                    ShimmerRect(height = 12)
                }

                is ShimmerState.Success -> {
                    ShimmerImage(imageUrl = imageUrl)
                    Spacer(Modifier.height(8.dp))
                    Text(title)
                    Text(price)
                }

                else -> Text("Error loading data.")
            }
        }
    }
}

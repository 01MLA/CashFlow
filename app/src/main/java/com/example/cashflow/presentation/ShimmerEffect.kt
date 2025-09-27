package com.example.cashflow.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cashflow.R

fun Modifier.shimmer(): Modifier = composed {
    var widthPx by remember { mutableFloatStateOf(0f) }

    val transition = rememberInfiniteTransition()
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = widthPx.takeIf { it > 0f } ?: 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing)
        ))

    val brush = Brush.linearGradient(
        colors = listOf(
            colorResource(R.color.shimmer).copy(alpha = 0.6f),
            colorResource(R.color.shimmer_highlight).copy(alpha = 0.3f),
            colorResource(R.color.shimmer).copy(alpha = 0.6f)
        ), start = Offset(translate - widthPx, 0f), end = Offset(translate, 0f)
    )
    this
        .onGloballyPositioned { widthPx = it.size.width.toFloat() }
        .background(brush)
}

@Composable
fun PostItem(
    post: Post?, // null if not loaded yet
    modifier: Modifier = Modifier
) {
    if (post == null) {
        // Loading state: show shimmer placeholders
        Column(modifier = modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .shimmer()
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .shimmer()
                    .fillMaxWidth(0.6f)
                    .height(20.dp)
            )
        }
    } else {
        // Loaded state: show actual content
        Column(modifier = modifier.padding(16.dp)) {
            Text(
                text = post.title, style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.body, style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostItem() {
    // Simulate loading
    PostItem(post = null)
    Spacer(modifier = Modifier.height(16.dp))
    // Simulate loaded post
    PostItem(post = Post(title = "Hello World", body = "This is a real post content."))
}

// Example data model
data class Post(val title: String, val body: String)

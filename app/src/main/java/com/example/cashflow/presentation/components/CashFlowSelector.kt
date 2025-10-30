package com.example.cashflow.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun <T> CashFlowSelector(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(durationMillis = 5000)) // fade-in duration
                // scale-in duration
                + scaleIn(initialScale = 0.9f, animationSpec = tween(durationMillis = 500)),
        exit = fadeOut(animationSpec = tween(durationMillis = 400)) // fade-out duration
                // scale-out duration
                + scaleOut(targetScale = 0.9f, animationSpec = tween(durationMillis = 400))
    ) {
        Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = modifier
                    .height(55.dp)
                    .border(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                        width = 1.dp
                    )
            ) {
                TextButton(modifier = Modifier.fillMaxSize(), onClick = { expanded = true }) {
                    Text(
                        selectedItem.toString(),
                        modifier = modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.rotate(if (expanded) 180f else 0f)
                    )
                }

                DropdownMenu(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 0.1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(16.dp)
                        ), expanded = expanded, onDismissRequest = { expanded = !expanded }) {
                    items.forEach { item ->
                        DropdownMenuItem(leadingIcon = {
                            if (item == selectedItem) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircleOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }, text = { Text(item.toString()) }, onClick = {
                            onItemSelected(item)
                            expanded = false
                        })
                    }
                }
            }
        }
    }
}

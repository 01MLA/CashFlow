package com.example.cashflow.presentation.ui.home.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashflow.presentation.shimmerEffect
import com.example.cashflow.util.formatMoney

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CashFlowItem(
    modifier: Modifier = Modifier,
    item: ItemModel,
    onDelete: () -> Unit,
    onClick: () -> Unit = {},
    isSelected: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = true, onClick = onClick)
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp, if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Gray.copy(alpha = 0.4f), shape = RoundedCornerShape(12.dp)
            )
            .background(
                if (isSelected) MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.04f)
            ), verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .weight(1f) // Take remaining space
                .padding(vertical = 10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.title,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold, fontSize = 15.sp
                    ),
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatMoney(item.amount),
                    modifier = Modifier.padding(start = 12.dp, end = 0.dp),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    item.details,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f),
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    item.date,
                    modifier = Modifier.padding(start = 12.dp, end = 0.dp),
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Icon stays fixed at end
        IconButton(onClick = { onDelete() }) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CashFlowItemShimmer(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp, Color.Gray.copy(alpha = 0.4f), shape = RoundedCornerShape(12.dp)
            )
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.04f))
            .padding(vertical = 10.dp)
            .shimmerEffect(true),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: text placeholders
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.weight(1f)
        ) {
            // First row (title + amount)
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray.copy(alpha = 0.4f))
                )
                Box(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(width = 60.dp, height = 14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray.copy(alpha = 0.4f))
                )
            }

            // Second row (details + date)
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                )
                Box(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(width = 50.dp, height = 12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                )
            }
        }

        // Right side: Delete icon placeholder
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = null,
            tint = Color.LightGray.copy(alpha = 0.5f),
            modifier = Modifier
                .padding(end = 8.dp, start = 4.dp)
                .size(24.dp)
        )
    }
}

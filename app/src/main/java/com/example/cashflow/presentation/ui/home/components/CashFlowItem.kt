package com.example.cashflow.presentation.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashflow.R
import com.example.cashflow.domain.model.TransactionType
import com.example.cashflow.presentation.shimmerEffect
import com.example.cashflow.util.formatMoney

@Composable
fun CashFlowItem(
    modifier: Modifier = Modifier,
    item: ItemModel,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
    onEditClicked: () -> Unit = {},
    isSelected: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .combinedClickable(enabled = true, onClick = onClick, onLongClick = onLongClick)
            .border(
                1.dp, if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Gray.copy(alpha = 0.4f), shape = RoundedCornerShape(12.dp)
            )
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
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
                    text = formatMoney(item.amount,stringResource(R.string.currency)),
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
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        0.3.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                DropdownMenuItem(text = { Text(stringResource(R.string.edit)) }, leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }, onClick = {
                    expanded = false
                    onEditClicked()
                })
                DropdownMenuItem(text = { Text(stringResource(R.string.delete)) }, leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }, onClick = {
                    expanded = false
                    onDeleteClicked()
                })
            }
        }
    }
}

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

@Composable
fun CashFlowRecentItem(
    modifier: Modifier = Modifier,
    item: ItemModel,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    isSelected: Boolean = false,
    type: TransactionType,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .combinedClickable(enabled = true, onClick = onClick, onLongClick = onLongClick)
            .border(
                1.dp, if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Gray.copy(alpha = 0.4f), shape = RoundedCornerShape(12.dp)
            )
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
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
                    text = formatMoney(item.amount,stringResource(R.string.currency)),
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

        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .size(45.dp)
                .background(Color.Transparent)
                .padding(8.dp),
            painter = if (type == TransactionType.INCOME) painterResource(R.drawable.income) else painterResource(
                R.drawable.expense
            ),
            contentDescription = null,
            tint = Color.Unspecified,
        )
    }
}

@Composable
fun Test() {
    val brush = Brush.horizontalGradient(listOf(Color.LightGray, Color.Magenta, Color.Blue))
    Canvas(Modifier.size(200.dp), onDraw = {
        drawCircle(brush)
    })
}

@Preview(showBackground = true)
@Composable
private fun TestPreview() {
    Test()
}

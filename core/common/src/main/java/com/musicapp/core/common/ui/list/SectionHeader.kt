package com.musicapp.core.common.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicapp.core.common.theme.MaterialTheme
import com.musicapp.core.common.theme.SectionHeader

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null,
    seeAllText: String = "See All",
    showSeeAll: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionHeader(text = title, modifier = Modifier.padding(bottom = 4.dp))

        if (showSeeAll && onSeeAllClick != null) {
            androidx.compose.material3.TextButton(
                onClick = onSeeAllClick,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = seeAllText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SectionHeaderWithAction(
    title: String,
    modifier: Modifier = Modifier,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    actionContentDescription: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionHeader(text = title, modifier = Modifier.padding(bottom = 4.dp))

        actionIcon?.let { icon ->
            actionContentDescription?.let { desc ->
                onActionClick?.let { click ->
                    androidx.compose.material3.IconButton(
                        onClick = click,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = icon,
                            contentDescription = desc
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeaderWithChips(
    title: String,
    modifier: Modifier = Modifier,
    chips: List<ChipItem>,
    selectedChip: Int = 0,
    onChipClick: (Int) -> Unit,
    onSeeAllClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader(text = title, modifier = Modifier.padding(bottom = 4.dp))

            onSeeAllClick?.let {
                androidx.compose.material3.TextButton(
                    onClick = it,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = "See All", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Filter chips
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chips.forEachIndexed { index, chip ->
                FilterChip(
                    text = chip.text,
                    selected = index == selectedChip,
                    onClick = { onChipClick(index) }
                )
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        label = { Text(text = text) },
        colors = androidx.compose.material3.ChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

data class ChipItem(
    val text: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null
)
package com.ebloid.store.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ebloid.store.data.AppSummary

private val Amber = Color(0xFFF2C14E)

/** Иконка приложения со скруглением и плейсхолдером. */
@Composable
fun AppIcon(url: String?, size: Int = 56, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape((size / 4).dp)
    Surface(
        modifier = modifier.size(size.dp).clip(shape),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = shape,
    ) {
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(size.dp),
            )
        } else {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Android, null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

/** Звёздный рейтинг (только отображение). */
@Composable
fun RatingStars(rating: Double, size: Int = 16) {
    Row {
        for (i in 1..5) {
            val filled = i <= rating
            Icon(
                imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = if (filled) Amber else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(size.dp),
            )
        }
    }
}

/** Строка приложения в списке каталога. */
@Composable
fun AppRow(app: AppSummary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        AppIcon(app.iconUrl)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                app.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            app.developer?.let {
                Text(it, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                RatingStars(app.rating, size = 13)
                Text(
                    if (app.reviewsCount > 0) "${app.rating} · ${app.reviewsCount}" else "Нет оценок",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }
    }
}

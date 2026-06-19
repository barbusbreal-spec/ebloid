package com.ebloid.store.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ebloid.store.data.Review
import com.ebloid.store.ui.StoreViewModel
import com.ebloid.store.ui.components.AppIcon
import com.ebloid.store.ui.components.RatingStars

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    vm: StoreViewModel,
    appId: Int,
    onBack: () -> Unit,
    onRequireAuth: () -> Unit,
) {
    val state by vm.detail.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showReviewDialog by remember { mutableStateOf(false) }

    LaunchedEffect(appId) { vm.loadDetail(appId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.app?.title ?: "Приложение") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }
            state.app == null -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text(state.error ?: "Не удалось загрузить", color = MaterialTheme.colorScheme.error)
            }
            else -> {
                val app = state.app!!
                Column(
                    Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) {
                    // Шапка
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        AppIcon(app.iconUrl, size = 72)
                        Column {
                            Text(app.title, style = MaterialTheme.typography.headlineSmall)
                            app.developer?.let {
                                Text(it, style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                RatingStars(app.rating)
                                Text("${app.rating} · ${app.reviewsCount} отзывов · ⬇ ${app.downloads}",
                                    style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            vm.onDownload(app.id)
                            app.apkUrl?.let {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = app.apkUrl != null,
                    ) { Text(if (app.apkUrl != null) "Установить" else "Файл недоступен") }

                    // Скриншоты
                    if (app.screenshots.isNotEmpty()) {
                        Spacer(Modifier.height(20.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(app.screenshots) { url ->
                                AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .height(260.dp)
                                        .width(150.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                )
                            }
                        }
                    }

                    // Описание
                    Spacer(Modifier.height(20.dp))
                    Text("Об этом приложении", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(app.description ?: app.shortDescription ?: "Без описания",
                        style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("Версия ${app.version ?: "—"} · ${app.category ?: ""}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

                    // Отзывы
                    Spacer(Modifier.height(24.dp))
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("Отзывы", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = {
                            if (vm.isLoggedIn()) showReviewDialog = true else onRequireAuth()
                        }) { Text("Оставить отзыв") }
                    }
                    Spacer(Modifier.height(8.dp))
                    if (state.reviews.isEmpty()) {
                        Text("Пока нет отзывов. Будьте первым!",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    } else {
                        state.reviews.forEach { ReviewItem(it) }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }

    if (showReviewDialog) {
        ReviewDialog(
            sending = state.sending,
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, text ->
                vm.submitReview(appId, rating, text) { error ->
                    if (error == null) showReviewDialog = false
                }
            },
        )
    }
}

@Composable
private fun ReviewItem(review: Review) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(review.author, style = MaterialTheme.typography.titleSmall)
            RatingStars(review.rating.toDouble(), size = 14)
        }
        review.text?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp))
        }
        Divider(Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant)
    }
}

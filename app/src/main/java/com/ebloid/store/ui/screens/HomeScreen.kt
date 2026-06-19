package com.ebloid.store.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebloid.store.ui.StoreViewModel
import com.ebloid.store.ui.components.AppRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: StoreViewModel,
    onAppClick: (Int) -> Unit,
    onAccountClick: () -> Unit,
) {
    val state by vm.catalog.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EbloidStore") },
                actions = {
                    IconButton(onClick = onAccountClick) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Аккаунт")
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            TextField(
                value = state.query,
                onValueChange = vm::onQueryChange,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Поиск приложений") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                singleLine = true,
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onSearch = { vm.onSearch() }
                ),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )

            if (state.categories.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = state.selectedCategory == null,
                        onClick = { vm.selectCategory(null) },
                        label = { Text("Все") },
                    )
                    state.categories.forEach { cat ->
                        FilterChip(
                            selected = state.selectedCategory == cat,
                            onClick = { vm.selectCategory(cat) },
                            label = { Text(cat) },
                        )
                    }
                }
            }

            when {
                state.loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.error != null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
                }
                state.apps.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Ничего не найдено")
                }
                else -> LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(state.apps, key = { it.id }) { app ->
                        AppRow(app = app, onClick = { onAppClick(app.id) })
                    }
                }
            }
        }
    }
}

package com.ebloid.store.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebloid.store.ui.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(vm: StoreViewModel, onBack: () -> Unit) {
    val user by vm.user.collectAsStateWithLifecycle()
    val loggedIn = user != null || vm.isLoggedIn()

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text(if (loggedIn) "Профиль" else "Аккаунт") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            if (loggedIn) {
                ProfileContent(vm, user?.displayName ?: user?.username)
            } else {
                AuthForm(vm, onSuccess = onBack)
            }
        }
    }
}

@Composable
private fun ColumnScope.ProfileContent(vm: StoreViewModel, name: String?) {
    Text("Вы вошли как", style = MaterialTheme.typography.bodyMedium)
    Text(name ?: "пользователь", style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(8.dp))
    Text(
        "Чтобы публиковать собственные приложения, войдите в кабинет разработчика " +
            "на сайте EbloidStore.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    )
    Spacer(Modifier.height(24.dp))
    Button(onClick = { vm.logout() }, modifier = Modifier.fillMaxWidth()) {
        Text("Выйти")
    }
}

@Composable
private fun ColumnScope.AuthForm(vm: StoreViewModel, onSuccess: () -> Unit) {
    var isRegister by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Text(
        if (isRegister) "Регистрация" else "Вход",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 16.dp),
    )

    OutlinedTextField(
        value = username,
        onValueChange = { username = it },
        label = { Text(if (isRegister) "Логин" else "Логин или email") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    if (isRegister) {
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Пароль") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
    )

    error?.let {
        Spacer(Modifier.height(10.dp))
        Text(it, color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall)
    }

    Spacer(Modifier.height(20.dp))
    Button(
        onClick = {
            error = null
            loading = true
            val cb: (String?) -> Unit = { err ->
                loading = false
                if (err == null) onSuccess() else error = err
            }
            if (isRegister) vm.register(username.trim(), email.trim(), password, cb)
            else vm.login(username.trim(), password, cb)
        },
        enabled = !loading && username.isNotBlank() && password.isNotBlank() &&
            (!isRegister || email.isNotBlank()),
        modifier = Modifier.fillMaxWidth(),
    ) { Text(if (loading) "Подождите..." else if (isRegister) "Создать аккаунт" else "Войти") }

    TextButton(
        onClick = { isRegister = !isRegister; error = null },
        modifier = Modifier.align(Alignment.CenterHorizontally),
    ) {
        Text(if (isRegister) "Уже есть аккаунт? Войти" else "Нет аккаунта? Зарегистрироваться")
    }
}

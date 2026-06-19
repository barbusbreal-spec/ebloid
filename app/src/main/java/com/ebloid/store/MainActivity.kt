package com.ebloid.store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.ebloid.store.ui.StoreViewModel
import com.ebloid.store.ui.screens.AppDetailScreen
import com.ebloid.store.ui.screens.AuthScreen
import com.ebloid.store.ui.screens.HomeScreen
import com.ebloid.store.ui.theme.EbloidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as EbloidApp
        setContent {
            EbloidTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    EbloidNavHost(app)
                }
            }
        }
    }
}

object Routes {
    const val HOME = "home"
    const val DETAIL = "detail/{appId}"
    const val AUTH = "auth"
    fun detail(appId: Int) = "detail/$appId"
}

@Composable
fun EbloidNavHost(app: EbloidApp) {
    val navController = rememberNavController()
    val vm: StoreViewModel = viewModel(
        factory = StoreViewModel.Factory(app.repository, app.session)
    )

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                vm = vm,
                onAppClick = { navController.navigate(Routes.detail(it)) },
                onAccountClick = { navController.navigate(Routes.AUTH) },
            )
        }
        composable(
            Routes.DETAIL,
            arguments = listOf(navArgument("appId") { type = NavType.IntType }),
        ) { entry ->
            val appId = entry.arguments?.getInt("appId") ?: return@composable
            AppDetailScreen(
                vm = vm,
                appId = appId,
                onBack = { navController.popBackStack() },
                onRequireAuth = { navController.navigate(Routes.AUTH) },
            )
        }
        composable(Routes.AUTH) {
            AuthScreen(vm = vm, onBack = { navController.popBackStack() })
        }
    }
}

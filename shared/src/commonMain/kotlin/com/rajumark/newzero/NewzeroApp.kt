package com.rajumark.newzero

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rajumark.newzero.app.ArticleEffect
import com.rajumark.newzero.app.ArticleStore
import com.rajumark.newzero.ui.AppTheme
import com.rajumark.newzero.ui.ErrorDialog
import com.rajumark.newzero.ui.FeedListScreen
import com.rajumark.newzero.ui.MainScreen
import com.rajumark.newzero.ui.RssFeedAppBar
import com.rajumark.newzero.ui.Screen
import com.rajumark.newzero.ui.SettingsScreen
import com.rajumark.newzero.ui.ThemeMode
import kotlinx.coroutines.flow.filterIsInstance
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssReaderApp(navController: NavHostController = rememberNavController()) {
    var themeMode by remember { mutableStateOf(ThemeMode.AUTO) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    AppTheme(themeMode = themeMode) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentScreen = Screen.valueOf(
            backStackEntry?.destination?.route ?: Screen.Main.name
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                RssFeedAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    onSettingsClick = { navController.navigate(Screen.Settings.name) }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Main.name,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(route = Screen.Main.name) {
                        MainScreen(
                            onEditClick = { navController.navigate(Screen.FeedList.name) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    composable(route = Screen.FeedList.name) {
                        FeedListScreen()
                    }
                    composable(route = Screen.Settings.name) {
                        SettingsScreen(
                            themeMode = themeMode,
                            onThemeChange = { themeMode = it }
                        )
                    }
                }

                val store: ArticleStore = koinInject<ArticleStore>()
                val error = store.effectFlow()
                    .filterIsInstance<ArticleEffect.Error>()
                    .collectAsState(null)
                LaunchedEffect(error.value) {
                    error.value?.let {
                        errorMessage = it.error.message ?: it.error.toString()
                    }
                }
                errorMessage?.let { message ->
                    ErrorDialog(
                        message = message,
                        onDismiss = { errorMessage = null }
                    )
                }
            }
        }
    }
}

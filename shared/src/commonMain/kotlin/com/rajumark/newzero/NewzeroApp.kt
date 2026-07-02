package com.rajumark.newzero

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rajumark.newzero.app.ArticleEffect
import com.rajumark.newzero.app.ArticleStore
import com.rajumark.newzero.ui.AppTheme
import com.rajumark.newzero.ui.FeedListScreen
import com.rajumark.newzero.ui.MainScreen
import com.rajumark.newzero.ui.RssFeedAppBar
import com.rajumark.newzero.ui.Screen
import kotlinx.coroutines.flow.filterIsInstance
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssReaderApp(navController: NavHostController = rememberNavController()) {
    AppTheme {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentScreen = Screen.valueOf(
            backStackEntry?.destination?.route ?: Screen.Main.name
        )
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                RssFeedAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() }
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                }

                val store: ArticleStore = koinInject<ArticleStore>()
                val error = store.effectFlow()
                    .filterIsInstance<ArticleEffect.Error>()
                    .collectAsState(null)
                LaunchedEffect(error.value) {
                    error.value?.let {
                        snackbarHostState.showSnackbar(
                            it.error.message.toString()
                        )
                    }
                }
            }
        }
    }
}

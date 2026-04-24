package navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import game.GameScreen
import game.GameViewModel
import settings.SettingsScreen

@Composable
fun AppNavigation(
    gameViewModelFactory: () -> GameViewModel
) {
    val navController = rememberNavController()
    val gameViewModel = gameViewModelFactory()

    NavHost(navController = navController, startDestination = GameRoute) {
        composable<GameRoute> {
            GameScreen(
                viewModel = gameViewModel,
                onNavigateToSettings = { navController.navigate(SettingsRoute) }
            )
        }
        composable<SettingsRoute> {
            SettingsScreen(
                currentSettings = gameViewModel.uiState.value.settings,
                onSettingsChanged = { gameViewModel.updateSettings(it) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

package twentyfour

import androidx.compose.runtime.Composable
import navigation.AppNavigation
import theme.TwentyFourTheme

@Composable
fun App(
    gameViewModelFactory: () -> game.GameViewModel
) {
    TwentyFourTheme {
        AppNavigation(gameViewModelFactory = gameViewModelFactory)
    }
}

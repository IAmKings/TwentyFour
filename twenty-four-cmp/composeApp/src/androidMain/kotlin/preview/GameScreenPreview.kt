package preview

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import data.HistoryStorage
import game.GameScreen
import game.GameViewModel
import theme.TwentyFourTheme

/**
 * GameScreen 预览 - 默认状态
 */
@Preview
@Composable
fun GameScreenPreview() {
    val context = LocalContext.current
    val viewModel = remember {
        createPreviewViewModel(context)
    }
    TwentyFourTheme {
        GameScreen(
            viewModel = viewModel,
            onNavigateToSettings = {}
        )
    }
}

private fun createPreviewViewModel(context: Context): GameViewModel {
    return GameViewModel(HistoryStorage(context))
}

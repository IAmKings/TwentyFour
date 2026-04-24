package game

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent

/**
 * Android 平台的 GameScreen - 添加键盘快捷键支持
 */
@Composable
actual fun GameScreen(
    viewModel: GameViewModel,
    onNavigateToSettings: () -> Unit
) {
    // 键盘快捷键处理 (仅 Android)
    val keyboardHandler = Modifier.onPreviewKeyEvent { event ->
        when (event.key) {
            Key.N -> {
                viewModel.newGame()
                true
            }
            Key.H -> {
                viewModel.showHint()
                true
            }
            Key.S -> {
                viewModel.skipRound()
                true
            }
            else -> false
        }
    }

    GameScreenContent(
        viewModel = viewModel,
        onNavigateToSettings = onNavigateToSettings,
        modifier = keyboardHandler
    )
}

package game

import androidx.compose.runtime.Composable

/**
 * Desktop 平台的 GameScreen
 * 键盘快捷键暂不支持 (Desktop 使用不同的输入处理方式)
 */
@Composable
actual fun GameScreen(
    viewModel: GameViewModel,
    onNavigateToSettings: () -> Unit
) {
    GameScreenContent(viewModel = viewModel, onNavigateToSettings = onNavigateToSettings)
}

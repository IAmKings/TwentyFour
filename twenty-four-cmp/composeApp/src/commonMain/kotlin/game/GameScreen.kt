package game

import androidx.compose.runtime.Composable

/**
 * GameScreen 公共签名 (commonMain)
 * 平台特定实现见 androidMain/desktopMain
 */
@Composable
expect fun GameScreen(
    viewModel: GameViewModel,
    onNavigateToSettings: () -> Unit
)

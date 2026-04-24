package game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.fitInside
import androidx.compose.foundation.layout.WindowInsetsRulers
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.CardFace
import components.ExpressionInput
import components.GameButtons
import components.HistoryList
import components.MessageArea
import components.PlayingCard
import components.ScoreBoard
import components.TimerBar
import theme.Accent
import theme.Background
import theme.LocalAppSpacing
import theme.TextMuted

/**
 * 游戏主界面内容 (CMP 通用)
 * 键盘快捷键由平台特定层添加
 */
@Composable
fun GameScreenContent(
    viewModel: GameViewModel,
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val spacing = LocalAppSpacing.current

    // 游戏开始
    LaunchedEffect(Unit) {
        if (uiState.numbers.isEmpty()) {
            viewModel.newGame()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .safeDrawingPadding()
    ) {
        // 背景光效
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Accent.copy(alpha = 0.05f),
                            Background
                        ),
                        center = androidx.compose.ui.geometry.Offset(
                            0.2f,
                            0.15f
                        ),
                        radius = 800f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsetsRulers.Ime.current)
                .padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            // Logo 和设置按钮
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "24",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 48.sp,
                            color = Accent,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(top = spacing.lg)
                    )
                    Text(
                        text = "点 — 心算挑战",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 12.sp,
                            letterSpacing = 8.sp,
                            textAlign = TextAlign.Center
                        ),
                        color = TextMuted
                    )
                }
                // 设置按钮 (右上角)
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = spacing.lg, end = 0.dp)
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "设置",
                        tint = TextMuted.copy(alpha = 0.6f)
                    )
                }
            }

            // 计分板
            ScoreBoard(
                score = uiState.score,
                streak = uiState.streak,
                elapsedTime = "%.1f".format(uiState.elapsedTime) + "s"
            )

            // 计时条
            TimerBar(
                progress = uiState.timerProgress,
                isWarning = uiState.isTimerWarning
            )

            // 扑克牌
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                uiState.numbers.forEachIndexed { index, number ->
                    val cardStateValue = uiState.cardStates.getOrNull(index) ?: CardState.FaceDown
                    val face = when (cardStateValue) {
                        CardState.FaceDown -> CardFace.Back
                        CardState.Dealing -> CardFace.Back
                        CardState.FaceUp -> CardFace.Front
                    }
                    val suit = getSuit(index)
                    val isRed = isRedSuit(suit)  // 根据实际花色判断红黑
                    val isDealing = cardStateValue == CardState.Dealing
                    // flipTrigger 用于触发动画：当卡牌从 Dealing 变为 FaceUp 时值会变化
                    val flipTrigger = if (cardStateValue == CardState.FaceUp) index + 1 else 0
                    // 顺序翻转延迟：每张卡片延迟 150ms
                    val flipDelay = if (cardStateValue == CardState.FaceUp) (index * 150).toLong() else 0L

                    components.DealAnimation(
                        index = index,
                        isDealing = isDealing
                    ) {
                        PlayingCard(
                            number = number,
                            suit = suit,
                            isRed = isRed,
                            face = face,
                            cardWidth = 72.dp,
                            cardHeight = 100.dp,
                            isCorrect = false,
                            isFaded = false,
                            flipTrigger = flipTrigger,
                            flipDelay = flipDelay
                        )
                    }
                }
            }

            // 消息区域
            MessageArea(message = uiState.message)

            // 输入框
            ExpressionInput(
                value = uiState.expression,
                onValueChange = viewModel::updateExpression,
                onSubmit = viewModel::checkAnswer,
                enabled = uiState.roundActive
            )

            // 按钮组
            GameButtons(
                onNewGame = viewModel::newGame,
                onHint = viewModel::showHint,
                onSkip = viewModel::skipRound,
                hintEnabled = uiState.roundActive,
                skipEnabled = uiState.roundActive
            )

            // 历史记录
            if (uiState.history.isNotEmpty()) {
                HistoryList(history = uiState.history)
            }

            // 快捷键提示
            Text(
                text = "Enter 提交  |  N 新局  |  H 提示  |  S 跳过",
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted.copy(alpha = 0.45f),
                modifier = Modifier.padding(top = spacing.md)
            )

            Spacer(modifier = Modifier.height(spacing.xl))
        }
    }
}

private fun getSuit(index: Int): String {
    val suits = listOf("\u2660", "\u2665", "\u2666", "\u2663")
    return suits[index % 4]
}

/**
 * 根据花色判断是否为红色（红桃、方块为红色）
 */
private fun isRedSuit(suit: String): Boolean {
    return suit == "\u2665" || suit == "\u2666"  // 红桃 or 方块
}

package preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import components.CardFace
import components.ExpressionInput
import components.GameButtons
import components.HistoryList
import components.MessageArea
import components.PlayingCard
import components.ScoreBoard
import components.TimerBar
import data.HistoryEntry
import data.HistoryStatus
import game.GameMessage
import theme.TwentyFourTheme

// ==================== PlayingCard 预览 ====================

@Preview
@Composable
fun CardBackPreview() {
    TwentyFourTheme {
        PlayingCard(
            number = 1,
            suit = "\u2665",
            isRed = true,
            face = CardFace.Back,
            cardWidth = 80.dp,
            cardHeight = 112.dp
        )
    }
}

@Preview
@Composable
fun CardAceHeartsPreview() {
    TwentyFourTheme {
        PlayingCard(
            number = 1,
            suit = "\u2665",
            isRed = true,
            face = CardFace.Front,
            cardWidth = 80.dp,
            cardHeight = 112.dp
        )
    }
}

@Preview
@Composable
fun CardSevenSpadesPreview() {
    TwentyFourTheme {
        PlayingCard(
            number = 7,
            suit = "\u2660",
            isRed = false,
            face = CardFace.Front,
            cardWidth = 80.dp,
            cardHeight = 112.dp
        )
    }
}

@Preview
@Composable
fun CardKingClubsPreview() {
    TwentyFourTheme {
        PlayingCard(
            number = 13,
            suit = "\u2663",
            isRed = false,
            face = CardFace.Front,
            cardWidth = 80.dp,
            cardHeight = 112.dp
        )
    }
}

// ==================== TimerBar 预览 ====================

@Preview
@Composable
fun TimerNormalPreview() {
    TwentyFourTheme {
        TimerBar(progress = 0.7f, isWarning = false)
    }
}

@Preview
@Composable
fun TimerWarningPreview() {
    TwentyFourTheme {
        TimerBar(progress = 0.2f, isWarning = true)
    }
}

// ==================== ScoreBoard 预览 ====================

@Preview
@Composable
fun ScoreBoardPreview() {
    TwentyFourTheme {
        ScoreBoard(
            score = 42,
            streak = 3,
            elapsedTime = "12.5s"
        )
    }
}

// ==================== ExpressionInput 预览 ====================

@Preview
@Composable
fun ExpressionInputPreview() {
    TwentyFourTheme {
        ExpressionInput(
            value = "(A+5)*(7-4)",
            onValueChange = {},
            onSubmit = {},
            enabled = true
        )
    }
}

// ==================== GameButtons 预览 ====================

@Preview
@Composable
fun GameButtonsPreview() {
    TwentyFourTheme {
        GameButtons(
            onNewGame = {},
            onHint = {},
            onSkip = {},
            hintEnabled = true,
            skipEnabled = true
        )
    }
}

// ==================== MessageArea 预览 ====================

@Preview
@Composable
fun MessageSuccessPreview() {
    TwentyFourTheme {
        MessageArea(message = GameMessage.Success("正确！+10 分   5.2s"))
    }
}

@Preview
@Composable
fun MessageErrorPreview() {
    TwentyFourTheme {
        MessageArea(message = GameMessage.Error("结果为 18，不是 24"))
    }
}

@Preview
@Composable
fun MessageInfoPreview() {
    TwentyFourTheme {
        MessageArea(message = GameMessage.Info("提示: (A+5)*(7-4)"))
    }
}

// ==================== HistoryList 预览 ====================

@Preview
@Composable
fun HistoryListPreview() {
    TwentyFourTheme {
        HistoryList(
            history = listOf(
                HistoryEntry(
                    numbers = listOf(1, 5, 7, 4),
                    expression = "(A+5)*(7-4)",
                    status = HistoryStatus.PASS
                ),
                HistoryEntry(
                    numbers = listOf(3, 8, 2, 6),
                    expression = "跳过",
                    status = HistoryStatus.SKIP
                ),
                HistoryEntry(
                    numbers = listOf(2, 4, 6, 8),
                    expression = "2+4+6+8",
                    status = HistoryStatus.FAIL
                )
            )
        )
    }
}

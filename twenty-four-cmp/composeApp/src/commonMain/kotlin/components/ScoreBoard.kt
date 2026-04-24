package components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.Accent
import theme.Success
import theme.TextMuted
import theme.TextPrimary

@Composable
fun ScoreBoard(
    score: Int,
    streak: Int,
    elapsedTime: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top
    ) {
        ScoreItem(label = "得分", value = score.toString(), isHighlight = true)
        ScoreItem(
            label = "连续",
            value = if (streak > 0) "\uD83D\uDD25 $streak" else "0",
            isStreak = streak > 0
        )
        ScoreItem(label = "用时", value = elapsedTime)
    }
}

@Composable
private fun ScoreItem(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    isStreak: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue = if (isStreak) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "streak_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = TextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            color = when {
                isHighlight -> Success
                isStreak -> Accent
                else -> TextPrimary
            },
            modifier = Modifier.scale(scale)
        )
    }
}

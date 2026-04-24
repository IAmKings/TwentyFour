package components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import theme.Accent
import theme.Background
import theme.Surface2
import theme.TextMuted
import theme.TextPrimary

@Composable
fun GameButtons(
    onNewGame: () -> Unit,
    onHint: () -> Unit,
    onSkip: () -> Unit,
    hintEnabled: Boolean,
    skipEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SecondaryButton(
            text = "新的一局",
            onClick = onNewGame,
            enabled = true
        )
        SecondaryButton(
            text = "提示",
            onClick = onHint,
            enabled = hintEnabled
        )
        SecondaryButton(
            text = "跳过",
            onClick = onSkip,
            enabled = skipEnabled
        )
    }
}

@Composable
private fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Surface2,
            contentColor = TextMuted,
            disabledContainerColor = Surface2.copy(alpha = 0.35f),
            disabledContentColor = TextMuted.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            brush = SolidColor(
                if (enabled) Color.White.copy(alpha = 0.06f) else Color.Transparent
            )
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) TextPrimary else TextMuted.copy(alpha = 0.35f)
        )
    }
}

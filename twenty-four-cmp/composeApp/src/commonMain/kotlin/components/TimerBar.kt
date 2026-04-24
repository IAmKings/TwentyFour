package components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import theme.Surface2
import theme.TimerNormalEnd
import theme.TimerNormalStart
import theme.TimerWarningEnd
import theme.TimerWarningStart

@Composable
fun TimerBar(
    progress: Float,
    isWarning: Boolean,
    modifier: Modifier = Modifier
) {
    val startColor by animateColorAsState(
        targetValue = if (isWarning) TimerWarningStart else TimerNormalStart,
        label = "timer_start_color"
    )
    val endColor by animateColorAsState(
        targetValue = if (isWarning) TimerWarningEnd else TimerNormalEnd,
        label = "timer_end_color"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Surface2)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(startColor, endColor)
                    )
                )
                .clip(RoundedCornerShape(2.dp))
        )
    }
}

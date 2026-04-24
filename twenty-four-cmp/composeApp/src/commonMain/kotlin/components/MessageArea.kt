package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import game.GameMessage
import theme.Accent
import theme.Error
import theme.ErrorGlow
import theme.Success
import theme.SuccessGlow

@Composable
fun MessageArea(
    message: GameMessage,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = message !is GameMessage.None,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val (text, bgColor, borderColor) = when (message) {
                is GameMessage.Success -> Triple(
                    message.text,
                    Success.copy(alpha = 0.1f),
                    Success.copy(alpha = 0.18f)
                )
                is GameMessage.Error -> Triple(
                    message.text,
                    Error.copy(alpha = 0.1f),
                    Error.copy(alpha = 0.18f)
                )
                is GameMessage.Info -> Triple(
                    message.text,
                    Accent.copy(alpha = 0.08f),
                    Accent.copy(alpha = 0.18f)
                )
                is GameMessage.None -> Triple("", SuccessGlow, SuccessGlow)
            }

            if (message !is GameMessage.None) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = when (message) {
                            is GameMessage.Success -> Success
                            is GameMessage.Error -> Error
                            is GameMessage.Info -> Accent
                            is GameMessage.None -> Success
                        }
                    ),
                    modifier = Modifier
                        .background(bgColor, RoundedCornerShape(12.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                )
            }
        }
    }
}

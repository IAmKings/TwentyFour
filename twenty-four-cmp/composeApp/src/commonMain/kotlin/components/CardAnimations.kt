package components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 发牌动画包装器 - 卡片从上方掉落
 */
@Composable
fun DealAnimation(
    index: Int,
    isDealing: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val offsetY = remember { Animatable(-200f) }
    val rotation = remember { Animatable(-15f) }
    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(isDealing) {
        coroutineScope {
            if (isDealing) {
                // 初始状态
                offsetY.snapTo(-200f)
                rotation.snapTo(-15f)
                scale.snapTo(0.85f)
                alpha.snapTo(0f)

                // 延迟根据索引错开
                delay(index * 220L)

                // 掉落动画（并行）
                launch {
                    offsetY.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                }
                launch {
                    rotation.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
                launch {
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
                launch {
                    alpha.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(300)
                    )
                }
            } else {
                // 稳定状态
                offsetY.snapTo(0f)
                rotation.snapTo(0f)
                scale.snapTo(1f)
                alpha.snapTo(1f)
            }
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = offsetY.value
                rotationZ = rotation.value
                scaleX = scale.value
                scaleY = scale.value
                this.alpha = alpha.value
            }
    ) {
        content()
    }
}

/**
 * 翻转动画包装器 - 3D 翻转效果
 */
@Composable
fun FlipAnimation(
    isFlipped: Boolean,
    modifier: Modifier = Modifier,
    front: @Composable () -> Unit,
    back: @Composable () -> Unit
) {
    val rotationY = remember { Animatable(180f) }

    LaunchedEffect(isFlipped) {
        val target = if (isFlipped) 0f else 180f
        rotationY.animateTo(
            targetValue = target,
            animationSpec = tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            )
        )
    }

    val animatedRotation = rotationY.value
    val isFrontVisible = animatedRotation < 90f

    Box(
        modifier = modifier.graphicsLayer {
            this.rotationY = animatedRotation
            cameraDistance = 12f * density
        }
    ) {
        if (isFrontVisible) {
            front()
        } else {
            back()
        }
    }
}

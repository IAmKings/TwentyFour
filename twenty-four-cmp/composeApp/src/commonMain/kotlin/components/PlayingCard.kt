package components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import theme.Accent
import theme.CardBack
import theme.CardBackBorder
import theme.CardBackground
import theme.CardBorder
import theme.SuitBlack
import theme.SuitRed

/**
 * CardFace - 卡片显示正面还是背面
 */
enum class CardFace {
    Back, Front
}

/**
 * PlayingCard with optional flip animation.
 * When flipTrigger changes from 0, triggers a 3D flip animation from back to front.
 */
@Composable
fun PlayingCard(
    number: Int,
    suit: String,
    isRed: Boolean,
    face: CardFace,
    modifier: Modifier = Modifier,
    cardWidth: Dp = 80.dp,
    cardHeight: Dp = 112.dp,
    isCorrect: Boolean = false,
    isFaded: Boolean = false,
    flipTrigger: Int = 0,  // 触发翻转动画：非0值时触发动画
    flipDelay: Long = 0L  // 翻转延迟（毫秒），用于顺序翻转
) {
    val suitColor = if (isRed) SuitRed else SuitBlack

    // 当 flipTrigger > 0 且目标是正面时，使用翻转动画包装器
    if (flipTrigger > 0 && face == CardFace.Front) {
        CardFlipWrapper(
            cardWidth = cardWidth,
            cardHeight = cardHeight,
            isCorrect = isCorrect,
            number = number,
            suit = suit,
            suitColor = suitColor,
            flipDelay = flipDelay,
            modifier = modifier
        )
    } else {
        // 无动画直接显示
        PlayingCardSurface(
            number = number,
            suit = suit,
            isRed = isRed,
            suitColor = suitColor,
            face = face,
            cardWidth = cardWidth,
            cardHeight = cardHeight,
            isCorrect = isCorrect,
            isFaded = isFaded,
            modifier = modifier
        )
    }
}

/**
 * 卡片翻转动画包装器
 * 使用 Animatable 实现 3D 翻转效果
 * flipDelay: 延迟开始翻转的时间（毫秒），用于顺序翻转效果
 */
@Composable
private fun CardFlipWrapper(
    cardWidth: Dp,
    cardHeight: Dp,
    isCorrect: Boolean,
    number: Int,
    suit: String,
    suitColor: Color,
    flipDelay: Long = 0L,
    modifier: Modifier = Modifier
) {
    // 初始为背面 (180度)，翻转到正面 (0度)
    val rotationY = remember { Animatable(180f) }

    LaunchedEffect(Unit) {
        // 延迟让发牌动画完成 + 顺序翻转延迟
        kotlinx.coroutines.delay(300L + flipDelay)
        // 翻转动画: 180 -> 0
        rotationY.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(
        modifier = modifier.graphicsLayer {
            this.rotationY = rotationY.value
            cameraDistance = 12f * density
        }
    ) {
        // 旋转角度超过 90 度时显示背面
        if (rotationY.value > 90f) {
            CardBackSurface(cardWidth, cardHeight)
        } else {
            CardFrontSurface(
                number = number,
                suit = suit,
                suitColor = suitColor,
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                isCorrect = isCorrect
            )
        }
    }
}

/**
 * 统一的卡片 Surface 渲染
 */
@Composable
private fun PlayingCardSurface(
    number: Int,
    suit: String,
    isRed: Boolean,
    suitColor: Color,
    face: CardFace,
    cardWidth: Dp,
    cardHeight: Dp,
    isCorrect: Boolean,
    isFaded: Boolean,
    modifier: Modifier = Modifier
) {
    val showBack = face == CardFace.Back

    Surface(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .clip(RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = if (showBack) CardBack else CardBackground,
        shadowElevation = if (isCorrect) 12.dp else 2.dp,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (showBack) {
                CardBackContent(cardWidth, cardHeight)
            } else {
                CardFrontSurface(
                    number = number,
                    suit = suit,
                    suitColor = suitColor,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight,
                    isCorrect = isCorrect
                )
            }
        }
    }
}

/**
 * 卡片背面 Surface
 */
@Composable
private fun CardBackSurface(
    cardWidth: Dp,
    cardHeight: Dp
) {
    Surface(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clip(RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = CardBack,
        shadowElevation = 2.dp,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            CardBackContent(cardWidth, cardHeight)
        }
    }
}

/**
 * 卡片正面 Surface
 */
@Composable
private fun CardFrontSurface(
    number: Int,
    suit: String,
    suitColor: Color,
    cardWidth: Dp,
    cardHeight: Dp,
    isCorrect: Boolean
) {
    Surface(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clip(RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = CardBackground,
        shadowElevation = if (isCorrect) 12.dp else 2.dp,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            CardFrontContent(
                number = number,
                suit = suit,
                suitColor = suitColor,
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                isCorrect = isCorrect
            )
        }
    }
}

@Composable
private fun CardBackContent(cardWidth: Dp, cardHeight: Dp) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = Modifier.size(cardWidth, cardHeight)) {
        val w = size.width
        val h = size.height
        val cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())

        // 外边框
        drawRoundRect(
            color = CardBackBorder,
            topLeft = Offset.Zero,
            size = Size(w, h),
            cornerRadius = cornerRadius,
            style = Stroke(width = 2.dp.toPx())
        )

        // 内边框
        val inset = 8.dp.toPx()
        drawRoundRect(
            color = Color.White.copy(alpha = 0.1f),
            topLeft = Offset(inset, inset),
            size = Size(w - inset * 2, h - inset * 2),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = Stroke(width = 1.5f)
        )

        // 对角线图案
        val step = 6.dp.toPx()
        val lineWidth = 1.dp.toPx()

        // 45度线条
        for (i in -10..(w / step + h / step).toInt() + 10) {
            val startX = i * step
            val startY = 0f
            val endX = startX - h
            val endY = h

            if (endX < w + step && startX > -step) {
                drawLine(
                    color = Color.White.copy(alpha = 0.035f),
                    start = Offset(startX.coerceIn(0f, w + h), startY),
                    end = Offset(endX.coerceIn(-h, w), endY),
                    strokeWidth = lineWidth
                )
            }
        }

        // -45度线条
        for (i in -10..(w / step + h / step).toInt() + 10) {
            val startX = i * step
            val startY = h
            val endX = startX + h
            val endY = 0f

            if (startX < w + step && endX > -step) {
                drawLine(
                    color = Color.White.copy(alpha = 0.035f),
                    start = Offset(startX.coerceIn(-h, w), startY),
                    end = Offset(endX.coerceIn(0f, w + h), endY),
                    strokeWidth = lineWidth
                )
            }
        }

        // 中间圆形
        val centerRadius = 22.dp.toPx()
        drawCircle(
            color = Accent.copy(alpha = 0.28f),
            radius = centerRadius,
            center = Offset(w / 2, h / 2),
            style = Stroke(width = 2.dp.toPx())
        )

        // 24 文字
        val textLayout = textMeasurer.measure(
            text = "24",
            style = TextStyle(
                color = Accent.copy(alpha = 0.45f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        )
        drawText(
            textLayoutResult = textLayout,
            topLeft = Offset(
                (w - textLayout.size.width) / 2,
                (h - textLayout.size.height) / 2
            )
        )
    }
}

@Composable
private fun CardFrontContent(
    number: Int,
    suit: String,
    suitColor: Color,
    cardWidth: Dp,
    cardHeight: Dp,
    isCorrect: Boolean
) {
    val textMeasurer = rememberTextMeasurer()
    val isFace = number == 1 || number >= 11
    val label = when (number) {
        1 -> "A"
        11 -> "J"
        12 -> "Q"
        13 -> "K"
        else -> number.toString()
    }

    val borderColor = if (isCorrect) Color(0xFF4ECB71) else CardBorder
    val glowColor = if (isCorrect) Color(0x4D4ECB71) else Color.Transparent

    Canvas(modifier = Modifier.size(cardWidth, cardHeight)) {
        val w = size.width
        val h = size.height
        val cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())

        // 外发光
        if (glowColor != Color.Transparent) {
            drawRoundRect(
                color = glowColor,
                topLeft = Offset(-4f, -4f),
                size = Size(w + 8f, h + 8f),
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
            )
        }

        // 背景
        drawRoundRect(
            color = CardBackground,
            topLeft = Offset.Zero,
            size = Size(w, h),
            cornerRadius = cornerRadius
        )

        // 边框
        drawRoundRect(
            color = borderColor,
            topLeft = Offset.Zero,
            size = Size(w, h),
            cornerRadius = cornerRadius,
            style = Stroke(width = 2.dp.toPx())
        )

        // 内阴影效果（模拟）
        drawRoundRect(
            color = Color.White.copy(alpha = 0.04f),
            topLeft = Offset(1.dp.toPx(), 1.dp.toPx()),
            size = Size(w - 2.dp.toPx(), h - 2.dp.toPx()),
            cornerRadius = CornerRadius(7.dp.toPx(), 7.dp.toPx()),
            style = Stroke(width = 1.dp.toPx())
        )

        // 左上角
        val cornerValSize = textMeasurer.measure(
            text = label,
            style = TextStyle(
                color = suitColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )
        val cornerSuitSize = textMeasurer.measure(
            text = suit,
            style = TextStyle(
                color = suitColor,
                fontSize = 11.sp
            )
        )

        drawText(
            textLayoutResult = cornerValSize,
            topLeft = Offset(8.dp.toPx(), 6.dp.toPx())
        )
        drawText(
            textLayoutResult = cornerSuitSize,
            topLeft = Offset(8.dp.toPx(), 6.dp.toPx() + cornerValSize.size.height + 1.dp.toPx())
        )

        // 右下角（180度旋转，使数字和花色倒置）
        rotate(degrees = 180f, pivot = Offset(w / 2, h / 2)) {
            drawText(
                textLayoutResult = cornerValSize,
                topLeft = Offset(8.dp.toPx(), 6.dp.toPx())
            )
            drawText(
                textLayoutResult = cornerSuitSize,
                topLeft = Offset(8.dp.toPx(), 6.dp.toPx() + cornerValSize.size.height + 1.dp.toPx())
            )
        }

        // 中心图案
        if (isFace) {
            // 大字 J/Q/K/A
            val faceTextSize = textMeasurer.measure(
                text = label,
                style = TextStyle(
                    color = suitColor,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black
                )
            )
            drawText(
                textLayoutResult = faceTextSize,
                topLeft = Offset(
                    (w - faceTextSize.size.width) / 2,
                    (h - faceTextSize.size.height) / 2
                )
            )

            // 装饰边框
            val decoSize = faceTextSize.size.width * 1.4f
            drawRoundRect(
                color = suitColor.copy(alpha = 0.1f),
                topLeft = Offset(
                    (w - decoSize) / 2,
                    (h - decoSize * 1.2f) / 2
                ),
                size = Size(decoSize, decoSize * 1.2f),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                style = Stroke(width = 1.5f)
            )
        } else {
            // 中间花色
            val suitTextSize = textMeasurer.measure(
                text = suit,
                style = TextStyle(
                    color = suitColor,
                    fontSize = 36.sp
                )
            )
            drawText(
                textLayoutResult = suitTextSize,
                topLeft = Offset(
                    (w - suitTextSize.size.width) / 2,
                    (h - suitTextSize.size.height) / 2
                )
            )
        }
    }
}
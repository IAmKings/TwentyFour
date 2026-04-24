# Quality Guidelines

> Code quality standards for Compose Multiplatform development.

---

## Overview

This project follows **Kotlin/Compose best practices**:
- Strong typing with data classes
- MVVM architecture with ViewModel
- CMP-compatible code patterns

---

## Forbidden Patterns

### ❌ 硬编码颜色
```kotlin
// Bad
Box(background = Color(0xFF1A1A2E))

// Good
Box(background = Background) // from theme
```

### ❌ 不暴露 modifier
```kotlin
// Bad
@Composable
fun BadCard() {
    Surface(modifier = Modifier.fillMaxWidth()) { } // 调用者无法控制
}

// Good
@Composable
fun GoodCard(modifier: Modifier = Modifier) {
    Surface(modifier = modifier) { } // ✅
}
```

### ❌ commonMain 里的 Android 专有 API
```kotlin
// Bad - commonMain 不能用这些
import androidx.compose.ui.input.key.onPreviewKeyEvent  // ❌

// Good - 移到 androidMain
```

### ❌ 在 Effect 里直接更新状态
```kotlin
// Bad - 容易导致无限循环
LaunchedEffect(counter) {
    viewModel.updateCount(counter + 1)
}
```

---

## Required Patterns

### 1. 暴露 modifier 参数
```kotlin
@Composable
fun PlayingCard(
    number: Int,
    modifier: Modifier = Modifier  // ✅ 必须有
) {
    Surface(modifier = modifier) { ... }
}
```

### 2. 使用 Theme 颜色/字体
```kotlin
// Good
Text(
    text = "Score",
    style = MaterialTheme.typography.bodyLarge,
    color = TextPrimary
)
```

### 3. StateFlow + collectAsState
```kotlin
// Good
@Composable
fun GameScreen(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()
}
```

### 4. Sealed class 处理状态
```kotlin
sealed class GameMessage {
    data class Success(val text: String) : GameMessage()
    data class Error(val text: String) : GameMessage()
}

// when 必须处理所有情况
when (message) {
    is GameMessage.Success -> { }
    is GameMessage.Error -> { }
}
```

---

## Testing Requirements

### 单元测试 (ViewModel)
```kotlin
class GameViewModelTest {
    @Test
    fun `newGame generates solvable numbers`() {
        val viewModel = GameViewModel(MockStorage())
        viewModel.newGame()

        val state = viewModel.uiState.value
        assert(state.numbers.size == 4)
        assert(state.numbers.all { it in 1..13 })
    }
}
```

### Preview 测试
```kotlin
@Preview
@Composable
fun PlayingCardPreview() {
    TwentyFourTheme {
        PlayingCard(number = 7, suit = "♠", isRed = false, face = CardFace.Front)
    }
}
```

---

## Code Review Checklist

- [ ] 组件有 `modifier: Modifier = Modifier` 参数
- [ ] 使用 Theme 颜色/字体，不硬编码
- [ ] `commonMain` 不包含平台专有 API
- [ ] 状态用 `StateFlow` + `collectAsState`
- [ ] Sealed class 的 `when` 处理所有分支
- [ ] 没有 `Any` 或类型转换
- [ ] Preview 在 `androidMain` 使用 AndroidX 注解

---

## Kotlin Style

| 规则 | 正确 | 错误 |
|------|------|------|
| 命名 | `PascalCase` (类/函数) | `camelCase` |
| 文件名 | `PascalCase.kt` | `snake_case.kt` |
| 缩进 | 4空格 | Tab |
| import | 按字母排序 | 随机顺序 |

使用 `ktlint` 或 IDE 格式化。

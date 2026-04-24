# Hook Guidelines

> How Compose effects and side effects are used in this project.

---

## Overview

Compose 中的 "hooks" 对应 Kotlin 的 **Effect APIs**:
- `LaunchedEffect` - 协程效果
- `remember` - 记忆化
- `DisposableEffect` - 清理效果
- `SideEffect` - 副作用

**注意**: 这个项目主要用 ViewModel 管理状态，Compose effects 只用于特定场景。

---

## Common Patterns

### 1. LaunchedEffect (启动协程)
```kotlin
// Good - 初始化时执行
@Composable
fun GameScreen(viewModel: GameViewModel) {
    LaunchedEffect(Unit) {
        // 只执行一次
        if (viewModel.uiState.value.numbers.isEmpty()) {
            viewModel.newGame()
        }
    }
}

// Good - 响应状态变化
@Composable
fun MessageArea(message: GameMessage) {
    LaunchedEffect(message) {
        // 当 message 变化时执行
        if (message is GameMessage.Success) {
            // 显示成功动画
        }
    }
}
```

### 2. remember (记忆化)
```kotlin
// Good - 记忆常量
@Composable
fun Component() {
    val animationDuration = remember { 300 } // 不会改变

    // Good - 记忆派生数据
    val displayText = remember(text, maxLength) {
        text.take(maxLength) + "..."
    }
}

// Good - 带 key 的 remember
@Composable
fun AnimatedCard(key: String) {
    val scale = remember(key) { mutableFloatStateOf(1f) }
    // key 变化时重新计算
}
```

### 3. DisposableEffect (清理)
```kotlin
// Good - 资源清理
@Composable
fun KeyboardHandler(onKeyPress: (Key) -> Unit) {
    DisposableEffect(Unit) {
        val listener = { event: KeyEvent -> ... }
        addKeyListener(listener)

        onDispose {
            removeKeyListener(listener) // 清理
        }
    }
}
```

---

## When NOT to Use Effects

### ❌ 不要：Effect 里直接更新状态
```kotlin
// Bad - 容易导致无限循环
LaunchedEffect(counter) {
    viewModel.updateCount(counter + 1) // ❌
}
```

### ✅ 应该：在 Effect 里执行副作用
```kotlin
// Good - 只做副作用
LaunchedEffect(successMessage) {
    if (successMessage != null) {
        // 播放音效、震动等
        playSound()
    }
}
```

### ❌ 不要：用 Effect 替代 ViewModel
```kotlin
// Bad - 状态应该放 ViewModel
@Composable
fun BadScreen() {
    var score by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        score = loadScore() // ❌
    }
}
```

### ✅ 应该：ViewModel 管理状态
```kotlin
// Good - ViewModel 管理
class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(score = loadScore()) }
        }
    }
}
```

---

## Common Mistakes

### ❌ 忘记 key 导致陈旧值
```kotlin
// Bad - 依赖的 value 变化时不重新执行
LaunchedEffect(Unit) {
    viewModel.setValue(value) // value 是陈旧的
}
```

### ✅ 正确指定 key
```kotlin
// Good - value 变化时重新执行
LaunchedEffect(value) {
    viewModel.setValue(value)
}
```

### ❌ DisposableEffect 清理不完整
```kotlin
// Bad - 清理不完整
DisposableEffect(Unit) {
    val job = scope.launch { ... }
    onDispose {
        job.cancel()
        // ❌ 忘记取消 scope
    }
}
```

### ✅ 完整清理
```kotlin
// Good
DisposableEffect(Unit) {
    val scope = CoroutineScope(Dispatchers.Main)
    val job = scope.launch { ... }

    onDispose {
        job.cancel()
        scope.cancel()
    }
}
```

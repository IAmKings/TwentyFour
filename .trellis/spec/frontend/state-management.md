# State Management

> How state is managed in this Compose Multiplatform project.

---

## Overview

This project uses **MVVM with StateFlow**:
- **ViewModel**: Holds UI state as `StateFlow`
- **Composables**: Collect and display state
- **Platform storage**: `HistoryStorage` via `expect/actual`

---

## State Categories

| 类型 | 示例 | 管理方式 |
|------|------|----------|
| UI状态 | `numbers`, `score`, `timerProgress` | `GameUiState` + `StateFlow` |
| 用户输入 | `expression` | ViewModel 处理 |
| 持久化数据 | `history` | `HistoryStorage` (平台特定) |
| 临时状态 | 动画状态 | Composable 内部 `remember` |

---

## ViewModel Pattern

```kotlin
// 1. 定义 UI 状态
data class GameUiState(
    val numbers: List<Int> = emptyList(),
    val score: Int = 0,
    val streak: Int = 0,
    val elapsedTime: Double = 0.0,
    val timerProgress: Float = 1f,
    // ...
)

// 2. ViewModel 持有状态
class GameViewModel(
    private val historyStorage: HistoryStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    // 3. 状态更新通过 update 函数
    fun updateExpression(expr: String) {
        _uiState.update { it.copy(expression = expr) }
    }
}

// 4. Composable 收集状态
@Composable
fun GameScreen(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // 使用 uiState.numbers, uiState.score 等
}
```

---

## When to Use What

### Local State (`remember`)
```kotlin
// 组件内部状态，不需要共享
@Composable
fun TimerBar(progress: Float, isWarning: Boolean) {
    val animationDuration = remember { 300 } // 本地常量
    // ...
}
```

### ViewModel State (`StateFlow`)
```kotlin
// 需要跨组件共享，或需要持久化的状态
class GameViewModel : ViewModel() {
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
}
```

### Remember + MutableState (简单场景)
```kotlin
// 表单输入等简单场景
var text by remember { mutableStateOf("") }
TextField(value = text, onValueChange = { text = it })
```

---

## Platform-Specific Storage (expect/actual)

```kotlin
// commonMain/data/HistoryStorage.kt
expect class HistoryStorage(context: Any)

// androidMain/data/HistoryStorage.android.kt
actual class HistoryStorage(actual val context: Context) {
    actual fun load(): List<HistoryEntry> { ... }
    actual fun save(history: List<HistoryEntry>) { ... }
}

// desktopMain/data/HistoryStorage.desktop.kt
actual class HistoryStorage(actual val context: Any) {
    // Desktop 实现
}
```

---

## Common Mistakes

### ❌ 状态放错位置
```kotlin
// Bad - 应该在 ViewModel 里
@Composable
fun BadScreen() {
    var score by remember { mutableStateOf(0) }
    var numbers by remember { mutableStateOf(listOf<Int>()) }
    // 两个组件需要共享这些状态？❌
}
```

### ✅ 正确的状态提升
```kotlin
// Good - ViewModel 管理共享状态
class GameViewModel : ViewModel() {
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
}

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    // 所有组件通过 viewModel 共享状态 ✅
}
```

### ❌ 不必要的 remember
```kotlin
// Bad
@Composable
fun BadCard(numbers: List<Int>) {
    val displayNumbers by remember { numbers } // 不需要 remember
}
```

### ✅ 直接使用
```kotlin
// Good
@Composable
fun GoodCard(numbers: List<Int>) {
    // 直接使用 numbers，不需要 remember
}
```

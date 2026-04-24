# Type Safety

> Type safety patterns in this Compose Multiplatform project.

---

## Overview

This project uses **Kotlin's type system** with Compose for type safety:
- Strong typing for all data classes
- Sealed classes for state variants
- No `Any` or type casts

---

## Type Organization

| 类型 | 位置 | 示例 |
|------|------|------|
| UI状态 | `game/GameViewModel.kt` | `GameUiState`, `GameMessage` |
| 数据模型 | `data/` | `HistoryEntry`, `GameSettings` |
| 枚举/密封类 | 各自模块 | `CardState`, `HistoryStatus` |

---

## Data Classes

### UI State (不可变)
```kotlin
// Good - 使用 data class
data class GameUiState(
    val numbers: List<Int> = emptyList(),
    val score: Int = 0,
    val streak: Int = 0,
    val message: GameMessage = GameMessage.None
)

// Bad - 普通 class
class GameUiState {
    var numbers: List<Int> = emptyList()
    var score: Int = 0
}
```

### Sealed Classes (状态变体)
```kotlin
// Good - 明确的状态类型
sealed class GameMessage {
    object None : GameMessage()
    data class Success(val text: String) : GameMessage()
    data class Error(val text: String) : GameMessage()
    data class Info(val text: String) : GameMessage()
}

// 使用 when 完整处理
@Composable
fun MessageArea(message: GameMessage) {
    when (message) {
        is GameMessage.None -> { }
        is GameMessage.Success -> Text(message.text, color = Success)
        is GameMessage.Error -> Text(message.text, color = Error)
        is GameMessage.Info -> Text(message.text, color = Info)
    }
}
```

---

## Enums vs Sealed Classes

### Enum (简单选项)
```kotlin
// Good - 有限选项
enum class CardState {
    FaceDown,
    Dealing,
    FaceUp
}
```

### Sealed Class (复杂选项)
```kotlin
// Good - 带数据的选项
sealed class GameMessage {
    data class Success(val points: Int, val time: String) : GameMessage()
    data class Error(val reason: String) : GameMessage()
}
```

---

## CMP 跨平台类型

### expect/actual 模式
```kotlin
// commonMain/data/HistoryStorage.kt
expect class HistoryStorage(context: Any)

// androidMain/data/HistoryStorage.android.kt
actual class HistoryStorage(actual val context: Context) {
    actual fun load(): List<HistoryEntry> { ... }
}

// desktopMain/data/HistoryStorage.desktop.kt
actual class HistoryStorage(actual val context: Any) {
    actual fun load(): List<HistoryEntry> { ... }
}
```

---

## Common Mistakes

### ❌ 使用 Any
```kotlin
// Bad
fun process(item: Any) {
    (item as? String)?.let { ... }
}
```

### ✅ 明确类型
```kotlin
// Good
fun process(item: HistoryEntry) {
    // 直接使用 item.numbers, item.status
}
```

### ❌ 忽略类型安全
```kotlin
// Bad
val result: Any = compute()
```

### ✅ 保持类型
```kotlin
// Good
val result: GameMessage = compute()
```

---

## Forbidden Patterns

1. **`as` 类型转换** - 使用 `is` 检查
2. **`Any` 类型** - 使用具体类型
3. **可空类型滥用** - `String?` 而非 `String` 当能确定时
4. **忽略警告** - IDE 的类型警告是有意义的

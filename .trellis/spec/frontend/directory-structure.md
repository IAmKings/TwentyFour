# Directory Structure

> How Compose Multiplatform frontend code is organized.

---

## Overview

This is a **Compose Multiplatform (CMP)** project targeting Android and Desktop.
Code is organized by platform with shared code in `commonMain`.

---

## Directory Layout

```
twenty-four-cmp/
└── composeApp/
    └── src/
        ├── commonMain/kotlin/
        │   ├── App.kt              # 根组件
        │   ├── game/
        │   │   ├── GameScreen.kt    # expect 声明
        │   │   ├── GameScreenContent.kt  # 共享UI
        │   │   └── GameViewModel.kt  # 游戏ViewModel
        │   ├── components/          # UI组件
        │   │   ├── PlayingCard.kt
        │   │   ├── TimerBar.kt
        │   │   ├── ScoreBoard.kt
        │   │   └── ...
        │   ├── theme/              # 主题
        │   │   ├── Color.kt
        │   │   ├── Type.kt
        │   │   └── Theme.kt
        │   ├── navigation/         # 导航
        │   ├── settings/           # 设置页面
        │   ├── data/               # 数据模型
        │   └── solver/             # 求解器 (Kotlin)
        │
        ├── androidMain/kotlin/
        │   ├── MainActivity.kt
        │   ├── game/GameScreen.android.kt  # actual 实现
        │   └── preview/             # Preview (AndroidX)
        │
        └── desktopMain/kotlin/
            ├── main.kt
            └── game/GameScreen.desktop.kt
```

---

## Module Organization

| 目录 | 内容 |
|------|------|
| `game/` | 游戏主界面和ViewModel |
| `components/` | 可复用UI组件 (PlayingCard, TimerBar等) |
| `theme/` | 主题定义 (颜色、字体、间距) |
| `navigation/` | 导航配置 |
| `settings/` | 设置页面 |
| `data/` | 数据模型和存储 |
| `solver/` | Kotlin版求解器 |

---

## Naming Conventions

| 类型 | 规则 | 示例 |
|------|------|------|
| Composable 函数 | PascalCase | `GameScreen`, `PlayingCard` |
| 文件名 | PascalCase.kt | `GameScreen.kt`, `PlayingCard.kt` |
| ViewModel | PascalCase | `GameViewModel` |
| 状态类 | PascalCase | `GameUiState`, `CardState` |
| Sealed Class | PascalCase | `GameMessage` |
| Modifier 扩展 | `Modifier.xxx()` | `Modifier.fillMaxSize()` |

---

## Examples

### Good: Component in correct location
```
components/
├── PlayingCard.kt      # 扑克牌组件
├── TimerBar.kt         # 计时条
└── ScoreBoard.kt       # 计分板
```

### Good: Platform-specific implementation
```
commonMain/kotlin/game/GameScreen.kt    # expect
androidMain/kotlin/game/GameScreen.android.kt  # actual (Android)
desktopMain/kotlin/game/GameScreen.desktop.kt  # actual (Desktop)
```

### Bad: Mixed concerns
```
game/
├── GameScreen.kt
├── GameViewModel.kt
└── SomeUtil.kt  # ❌ Util应该放其他地方
```

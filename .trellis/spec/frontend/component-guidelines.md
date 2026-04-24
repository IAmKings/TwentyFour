# Component Guidelines

> How Compose components are built in this project.

---

## Overview

Components are organized following **Atomic Design**:
- **Atoms**: Basic UI elements (PlayingCard, TimerBar)
- **Molecules**: Simple component combinations (ScoreBoard)
- **Organisms**: Complex UI sections (GameScreen)

---

## Component Structure

```kotlin
@Composable
fun PlayingCard(
    number: Int,
    suit: String,
    isRed: Boolean,
    face: CardFace,
    modifier: Modifier = Modifier,  // 必须有
    cardWidth: Dp = 80.dp,
    cardHeight: Dp = 112.dp,
    isCorrect: Boolean = false,
    isFaded: Boolean = false
) {
    // 1. 解构参数
    val suitColor = if (isRed) SuitRed else SuitBlack

    // 2. 组合子组件
    Surface(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight),
        // ...
    ) {
        // 内容
    }
}
```

---

## Props Conventions

### 必须的参数顺序
1. **必需数据** - `number`, `suit`, `isRed`
2. **可选配置** - `isCorrect`, `isFaded`
3. **布局控制** - `modifier` (最后!)
4. **样式参数** - `cardWidth`, `cardHeight` (最后)

### 参数命名
```kotlin
// Good
@Composable
fun PlayingCard(
    number: Int,
    suit: String,
    isRed: Boolean,
    modifier: Modifier = Modifier
)

// Bad - 缩写不清晰
@Composable
fun PlayingCard(
    num: Int,
    s: String,
    red: Boolean,
    mod: Modifier = Modifier
)
```

---

## Modifier 规则

### 1. 暴露 modifier 参数
```kotlin
// Good
@Composable
fun PlayingCard(
    modifier: Modifier = Modifier
) { ... }

// Bad - 不暴露 modifier
@Composable
fun PlayingCard(
    width: Dp,
    height: Dp
) {
    Box(modifier = Modifier.size(width, height)) // 调用者无法控制
}
```

### 2. Modifier 传递给根元素
```kotlin
// Good
Surface(
    modifier = modifier
        .width(cardWidth)
        .height(cardHeight)
) { ... }

// Bad - 忽略 modifier
Surface(
    modifier = Modifier.size(cardWidth, cardHeight) // 参数被忽略
) { ... }
```

### 3. Modifier 顺序
```
size/fill → padding → background/border → clickable/pointerInput
```

---

## Theme 使用

### ✅ 必须使用 Theme 颜色
```kotlin
// Good
Surface(color = CardBackground) { ... }

// Bad - 硬编码颜色
Surface(color = Color(0xFF1A1A2E)) { ... }
```

### ✅ 必须使用 Theme 字体
```kotlin
// Good
Text(
    text = "Score",
    style = MaterialTheme.typography.bodyLarge
)

// Bad - 硬编码字体
Text(
    text = "Score",
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold
)
```

---

## Accessibility

### 1. contentDescription (必须有)
```kotlin
// Good
Icon(
    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
    contentDescription = "返回"
)

// Bad
Icon(
    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
    contentDescription = null  // ❌
)
```

### 2. 语义顺序
```kotlin
Column {
    // 按视觉顺序排列
    Text("标题")
    Text("副标题")
    Button("操作")
}
```

---

## Common Mistakes

### ❌ 不暴露 modifier
```kotlin
@Composable
fun BadCard() {
    Surface(modifier = Modifier.fillMaxWidth()) { } // 调用者无法控制
}
```

### ✅ 正确做法
```kotlin
@Composable
fun GoodCard(modifier: Modifier = Modifier) {
    Surface(modifier = modifier) { } // 调用者可以添加额外修饰
}
```

### ❌ 硬编码颜色
```kotlin
// Bad
Box(background = Color(0xFF1A1A2E))

// Good
Box(background = Background) // 从 theme 导入
```

### ❌ 魔法数字
```kotlin
// Bad
modifier.padding(16.dp)

// Good
modifier.padding(spacing.md) // 使用 theme spacing
```

# Compose Multiplatform 24点游戏

## Goal
将现有 HTML 版 24 点游戏移植为 Compose Multiplatform 跨平台应用。

## Confirmed Requirements

### 项目定位
- 作为 TwentyFour 项目的子目录 `twenty-four-cmp/`
- 目标平台：Android + Desktop（JVM）先行，后续扩展 iOS + Web

### 功能需求
- [ ] 生成可解的 4 张扑克牌（难度可选：简单 1-10 / 普通 1-13）
- [ ] 用户输入算式，用 + - * / 和括号，使结果等于 24
- [ ] 表达式验证（必须使用给出的 4 张牌、结果等于 24）
- [ ] 计时器（60 秒倒计时，进度条显示）
- [ ] 得分系统（根据用时给分，最快得 10 分）
- [ ] 连续答对记录（streak，火焰图标 + 脉冲动画）
- [ ] 提示功能（显示一个可行解法）
- [ ] 跳过功能（显示答案并进入下一局）
- [ ] 历史记录（最近 20 局，**持久化保存**）
- [ ] 键盘快捷键（Enter 提交，N 新局，H 提示，S 跳过）
- [ ] 设置页面（难度选择、其他选项）

### UI 需求
- [ ] 深色主题，与原 HTML 风格一致
- [ ] 扑克牌组件（背面图案 + 正面点数/花色，Canvas 绘制）
- [ ] 发牌动画（卡片从上方掉落 + 旋转）
- [ ] 翻转动画（背面翻到正面，3D rotateY）
- [ ] 正确/错误反馈动画（边框变色、发光）
- [ ] 响应式布局（适配手机、平板、桌面）
- [ ] 计时条颜色随时间变化（正常→警告红色）

### 导航需求
- [ ] 主游戏界面（默认）
- [ ] 设置页面（难度选择等）
- [ ] 使用 Navigation Compose 管理页面切换

### 技术需求
- [ ] Compose Multiplatform (CMP) 1.8.0+
- [ ] Kotlin 2.1.20
- [ ] 24 点求解器算法（Kotlin 重写，从 HTML JS 移植）
- [ ] 表达式求值和验证
- [ ] 状态管理：ViewModel (lifecycle-viewmodel-compose)
- [ ] 导航：Navigation Compose（类型安全路由）
- [ ] 持久化：本地文件存储历史记录（expect/actual 适配不同平台）
- [ ] 自定义主题（ColorScheme, Typography, 自定义 Spacing）

## Acceptance Criteria
- [ ] Android 和 Desktop 平台可编译运行
- [ ] 游戏逻辑与 HTML 版一致
- [ ] 动画流畅（60fps）
- [ ] 响应式布局在不同屏幕尺寸下正常显示
- [ ] 深色主题视觉效果与 HTML 版接近
- [ ] 历史记录重启应用后仍可读取

## Technical Stack
- CMP Plugin: 1.8.0
- Kotlin: 2.1.20
- Compose Compiler: 2.1.20
- Navigation Compose: 2.8.0+
- lifecycle-viewmodel-compose: 2.10.0+
- kotlinx-serialization-json: 持久化数据格式

# HJ Plan — 个人日程管理 App

iOS 风格的 Android 日程管理应用，完全本地存储，无需网络。

## 功能

- 今日日程视图（iOS 风格大标题）
- 月历视图（带事件圆点）
- 全部清单视图（支持搜索）
- 新建/编辑/删除日程
- 颜色标签（7种颜色）
- 事件提醒（系统通知）
- 全天事件
- 完成标记
- 深色模式

## 技术栈

- Kotlin + Jetpack Compose
- Room + SQLite（本地数据库）
- Hilt（依赖注入）
- MVVM 架构
- Navigation Compose

## GitHub Actions 自动打包

直接 push 到 main 分支，等 2-3 分钟后在：
**Actions → Build APK → Artifacts** 下载 APK

## 本地构建

需要：JDK 17 + Android SDK (API 34)

```bash
./gradlew assembleDebug
# APK 在 app/build/outputs/apk/debug/app-debug.apk
```

## 项目结构

```
app/src/main/java/com/hjplan/app/
├── MainActivity.kt          # 入口 Activity
├── HJPlanApp.kt             # Application (Hilt)
├── MainNavHost.kt           # 导航 + BottomBar
├── data/
│   ├── model/Event.kt       # 数据模型
│   ├── db/EventDao.kt       # 数据库操作
│   ├── db/HJPlanDatabase.kt
│   └── repository/EventRepository.kt
├── viewmodel/EventViewModel.kt
├── notification/AlarmReceiver.kt
├── ui/
│   ├── theme/Theme.kt       # iOS 配色
│   └── screens/
│       ├── HomeScreen.kt    # 今日视图
│       ├── CalendarScreen.kt
│       ├── TaskListScreen.kt
│       ├── EditEventScreen.kt
│       └── ProfileScreen.kt
└── di/DatabaseModule.kt     # Hilt 注入
```

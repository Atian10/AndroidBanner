# android-banner

> Android Java 轮播页 Demo 应用 - 实现可复用的轮播组件，后续可演化成独立库

## 仓库定位

本仓库为**闭源私有**的 Android Java 轮播页 Demo 应用，用于开发与验证可复用的轮播组件（Banner / Carousel）。组件成熟后可抽取为独立 Android Library 模块，供其他业务项目依赖。

## 已实现功能

### 核心轮播

- **基础轮播**：基于 ViewPager2 + RecyclerView.Adapter 实现横向轮播
- **真无限循环**：loop=true 时 getItemCount 返回 Integer.MAX_VALUE，启动定位 middlePosition，支持双向无限滑动
- **自动播放**：可配置轮播间隔，onPause 暂停 / onResume 恢复，避免后台耗电
- **生命周期感知**：Handler 调度与生命周期绑定，防止内存泄漏

### 可配置化（BannerConfig Builder 模式）

通过 `BannerConfig.Builder()` 链式配置以下选项：

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `interval` | long | 3000L | 轮播间隔（毫秒） |
| `loop` | boolean | true | 是否无限循环 |
| `indicatorType` | IndicatorType | DOT | 指示器类型（DOT/NUMBER） |
| `cardStyle` | CardStyle | NORMAL | 卡片样式（NORMAL/CARD） |
| `animType` | AnimType | SCALE | 切换动画类型（NONE/SCALE/FLIP/FADE/DEPTH） |

### 指示器系统

- **DOT 圆点指示器**：默认样式，选中/未选中状态切换
- **NUMBER 数字指示器**：显示 "当前位/总数"（如 1/4）
- **分发器模式**：initIndicator/updateIndicator 根据 indicatorType 分发

### 动画系统

支持 5 种页面切换动画，通过 `AnimType` 配置：

| 动画类型 | 类名 | 效果 |
|----------|------|------|
| `NONE` | — | 无动画 |
| `SCALE` | ScalePageTransformer | 非选中页缩放 0.85 + 透明度 0.5 |
| `FLIP` | FlipPageTransformer | 沿 Y 轴 3D 翻转 |
| `FADE` | FadePageTransformer | 透明度淡入淡出 |
| `DEPTH` | DepthPageTransformer | 左侧页缩小，右侧页从右侧平移进入 |

### 图片加载

- 对接 Glide，支持网络图与本地资源
- 空 URL 占位图、加载失败回退处理

### 点击事件

- `OnBannerClickListener` 回调接口
- 快速点击防护（500ms 间隔）

## 技术栈

- 语言：Java
- 最低 SDK：21
- View 绑定：ViewBinding（反射绑定基类 BaseActivity）
- 图片加载：Glide
- 容器：ViewPager2 + RecyclerView Adapter
- 日志：统一 LogUtils 工具封装

## 项目结构

```
android-banner/
├── app/                                       # Demo 应用模块
│   └── src/main/java/com/atian/banner/
│       ├── base/                              # 基类
│       │   └── BaseActivity.java              # 反射 ViewBinding 基类
│       ├── ui/                                # 界面层
│       │   └── BannerActivity.java            # 轮播演示页面
│       ├── adapter/                           # 适配器
│       │   └── BannerRvAdapter.java           # Banner 适配器（支持无限循环）
│       ├── bean/                              # 数据模型
│       │   └── BannerBean.java                # 轮播数据实体
│       ├── config/                            # 配置
│       │   └── BannerConfig.java              # Builder 模式配置类
│       ├── enums/                             # 枚举
│       │   ├── IndicatorType.java             # 指示器类型（DOT/NUMBER）
│       │   ├── CardStyle.java                 # 卡片样式（NORMAL/CARD）
│       │   └── AnimType.java                  # 动画类型（NONE/SCALE/FLIP/FADE/DEPTH）
│       ├── transformer/                       # 页面变换器
│       │   ├── ScalePageTransformer.java      # 缩放+透明度
│       │   ├── FlipPageTransformer.java       # 3D 翻转
│       │   ├── FadePageTransformer.java       # 淡入淡出
│       │   └── DepthPageTransformer.java      # 深度
│       ├── util/                              # 工具
│       │   └── LogUtils.java                  # 统一日志封装
│       └── interfaces/                        # 接口
│           └── OnBannerClickListener.java     # 点击回调
├── banner/                                    # （未来）独立 Library 模块
└── docs/                                      # 内部文档
```

## 使用示例

### 基础用法（默认配置）

```java
// 默认配置：loop=true / DOT 指示器 / SCALE 动画 / 3秒间隔
BannerConfig config = new BannerConfig.Builder().build();
adapter = new BannerRvAdapter(bannerList, config.isLoop());
```

### 自定义配置

```java
// 数字指示器 + 3D 翻转动画 + 5秒间隔 + 不循环
BannerConfig config = new BannerConfig.Builder()
        .indicatorType(IndicatorType.NUMBER)
        .animType(AnimType.FLIP)
        .interval(5000L)
        .loop(false)
        .build();
```

### 禁用动画

```java
BannerConfig config = new BannerConfig.Builder()
        .animType(AnimType.NONE)
        .build();
```

## 已修复缺陷

| 编号 | 缺陷 | 修复说明 |
|------|------|----------|
| P01 | bannerRunnable 边界溢出 | currentPosition 达到 MAX_VALUE-1 时重置到 middlePosition |
| P02 | updateNumberIndicator null 检查 | 增加 adapter != null 判断 |
| P03 | initView 调用顺序 | 先 initIndicator 再 setCurrentItem |
| P04 | postDelayed 自调度 | 移入 isAutoPlaying 判断内，停止时不自调度 |
| P07 | Adapter 构造 list null 兜底 | 转为 Collections.emptyList() |
| D01 | onBannerClick 硬编码字符串 | 迁移至 strings.xml |
| D02 | rv_item_banner.xml 命名空间位置 | 移至根元素 |
| D03 | String.format 未传 Locale | 显式传入 Locale.US |
| D04 | rv_item_banner.xml 硬编码颜色/尺寸 | 迁移至 colors.xml/dimens.xml |

## 依赖关系

本 Demo 应用为独立项目。轮播组件成熟后，将抽取到 [android-core](https://github.com/Atian10/android-core) 公共基础库的 `core-ui` 模块中，供其他业务项目复用。

## 许可证

本仓库为**闭源专有资产**，All Rights Reserved。详见 [LICENSE](./LICENSE) 文件。

未经著作权人事先书面许可，任何人不得复制、修改、分发或使用本仓库的任何部分。

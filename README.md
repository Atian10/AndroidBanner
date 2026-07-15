# AndroidBanner

> Android Java 轮播组件 - 已完成 Library 化改造，可作为独立 AAR 模块集成到任意 Android 项目

## 仓库定位

本仓库为 Android Java 轮播组件，已完成 **6 阶段 Library 化改造（P0-P5）**，提供独立 `:banner` Library 模块，支持通过源码依赖或 AAR 依赖集成到其他项目。

- **Demo 应用**：`app` 模块，演示全部功能
- **Library 模块**：`banner` 模块，对外提供轮播能力

## 已实现功能

### 核心轮播

- **基础轮播**：基于 ViewPager2 + RecyclerView.Adapter 实现横向轮播
- **真无限循环**：loop=true 时 getItemCount 返回 Integer.MAX_VALUE，启动定位 middlePosition，支持双向无限滑动
- **自动播放**：可配置轮播间隔，onPause 暂停 / onResume 恢复，避免后台耗电
- **生命周期感知**：通过 DefaultLifecycleObserver 自动感知宿主生命周期，宿主只需调用 `start(LifecycleOwner)` 即可

### 可配置化（BannerConfig Builder 模式）

通过 `BannerConfig.Builder()` 链式配置以下选项：

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `interval` | long | 3000L | 轮播间隔（毫秒） |
| `loop` | boolean | true | 是否无限循环 |
| `indicatorType` | IndicatorType | DOT | 指示器类型（DOT/NUMBER） |
| `cardStyle` | CardStyle | NORMAL | 卡片样式（NORMAL/CARD） |
| `animType` | AnimType | SCALE | 切换动画类型（NONE/SCALE/FLIP/FADE/DEPTH） |
| `titleVisible` | boolean | true | 是否显示标题（v1.1.0 新增） |
| `titleBgColor` | int | #80000000 | 标题背景色（v1.1.0 新增） |
| `titleTextColor` | int | #FFFFFF | 标题文字颜色（v1.1.0 新增） |

### 指示器系统

- **DOT 圆点指示器**：默认样式，选中/未选中状态切换
- **NUMBER 数字指示器**：显示 "当前位/总数"（如 1/4）
- **分发器模式**：initIndicator/updateIndicator 根据 indicatorType 分发

### 动画系统

支持 5 种页面切换动画，通过 `AnimType` 配置：

| 动画类型 | 类名 | 效果 |
|----------|------|------|
| `NONE` | - | 无动画 |
| `SCALE` | ScalePageTransformer | 非选中页缩放 0.85 + 透明度 0.5 |
| `FLIP` | FlipPageTransformer | 沿 Y 轴 3D 翻转 |
| `FADE` | FadePageTransformer | 透明度淡入淡出 |
| `DEPTH` | DepthPageTransformer | 左侧页缩小，右侧页从右侧平移进入 |

**CardStyle 与 AnimType 协同规则**：
- `CardStyle.CARD`：启用 ViewPager2 的 `clipToPadding=false`，让两侧卡片可见
- `CardStyle.CARD` + `AnimType.NONE`：自动 fallback 到 `SCALE`，保证卡片视觉效果生效

### 图片加载解耦

- 通过 `IImageLoader` 接口解耦，支持任意图片加载框架（Glide / Picasso / Coil）
- 默认提供 `GlideImageLoader` 实现（Glide 为 `compileOnly`，由宿主提供运行时依赖）
- 可自定义实现，注入到 `BannerView`

### 数据模型解耦

- 通过 `IBannerData` 接口统一访问图片地址、标题和跳转链接
- Adapter / Listener 泛型化，支持任意自定义数据模型
- 内置 `BannerBean` 可直接使用

### 布局可定制

- 通过 `BannerViewHolderFactory` + `BannerViewHolder` 工厂模式支持自定义 item 布局
- 默认布局展示图片 + 标题，可扩展为任意复杂布局

### 点击事件

- `OnBannerClickListener` 回调接口（泛型）

## 技术栈

- 语言：Java 11
- 最低 SDK：21
- 目标 SDK：33
- View 绑定：ViewBinding
- 图片加载：Glide（解耦，可替换）
- 容器：ViewPager2 + RecyclerView Adapter
- 生命周期：Android Lifecycle（DefaultLifecycleObserver）
- 日志：统一 LogUtils 工具封装

## 项目结构

```
AndroidBanner/
├── app/                                       # Demo 应用模块
│   └── src/main/java/com/atian/banner/
│       ├── base/                              # 基类
│       │   └── BaseActivity.java              # 反射 ViewBinding 基类
│       └── ui/                                # 界面层
│           └── BannerActivity.java            # 轮播演示页面（可切换配置）
├── banner/                                    # 独立 Library 模块
│   └── src/main/java/com/atian/banner/
│       ├── view/                              # 视图层
│       │   └── BannerView.java                # 统一入口 View
│       ├── adapter/                           # 适配器
│       │   └── BannerRvAdapter.java           # Banner 适配器（支持无限循环 + 泛型）
│       ├── bean/                              # 数据模型
│       │   └── BannerBean.java                # 默认数据模型（实现 IBannerData）
│       ├── config/                            # 配置
│       │   └── BannerConfig.java              # Builder 模式配置类
│       ├── enums/                             # 枚举
│       │   ├── IndicatorType.java             # 指示器类型（DOT/NUMBER）
│       │   ├── CardStyle.java                 # 卡片样式（NORMAL/CARD）
│       │   └── AnimType.java                  # 动画类型（NONE/SCALE/FLIP/FADE/DEPTH）
│       ├── imageloader/                       # 图片加载
│       │   └── GlideImageLoader.java          # Glide 默认实现
│       ├── interfaces/                        # 接口
│       │   ├── IBannerData.java               # 数据模型接口
│       │   ├── IImageLoader.java              # 图片加载器接口
│       │   └── OnBannerClickListener.java     # 点击回调接口（泛型）
│       ├── transformer/                       # 页面变换器
│       │   ├── ScalePageTransformer.java      # 缩放+透明度
│       │   ├── FlipPageTransformer.java       # 3D 翻转
│       │   ├── FadePageTransformer.java       # 淡入淡出
│       │   └── DepthPageTransformer.java      # 深度
│       ├── viewholder/                        # ViewHolder 系统
│       │   ├── BannerViewHolder.java          # ViewHolder 基类
│       │   ├── BannerViewHolderFactory.java   # ViewHolder 工厂接口
│       │   ├── DefaultBannerViewHolder.java   # 默认实现
│       │   └── DefaultBannerViewHolderFactory.java  # 默认工厂
│       └── util/                              # 工具
│           └── LogUtils.java                  # 统一日志封装
└── docs/                                      # 文档
    └── INTEGRATION.md                         # 对接使用指南
```

## 集成方式

### 方式一：源码模块依赖

在宿主项目 `settings.gradle` 中引入 `:banner` 模块：

```gradle
include ':app', ':banner'
project(':banner').projectDir = new File('path/to/AndroidBanner/banner')
```

在宿主 `:app` 模块的 `build.gradle` 中添加依赖：

```gradle
dependencies {
    implementation project(':banner')
}
```

### 方式二：AAR 依赖

在本仓库执行 AAR 构建：

```bash
./gradlew :banner:assembleRelease
```

产物路径：`banner/build/outputs/aar/banner-release.aar`

将 AAR 放入宿主项目 `libs/` 目录并声明依赖（AAR 不携带传递依赖，需宿主自行提供 AndroidX 等依赖）。

### 方式三：远程依赖（JitPack）

仓库已配置 `maven-publish`，支持 JitPack 构建。在 GitHub 打 Tag 后，JitPack 会自动构建并发布。

**步骤 1**：在宿主项目根目录 `settings.gradle` 的 `repositories` 中添加 JitPack：

```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }   // 新增
    }
}
```

**步骤 2**：在宿主 `:app` 模块 `build.gradle` 中添加依赖：

```gradle
dependencies {
    // :banner 模块已声明 VERSION_NAME=1.1.0，JitPack 会按 Tag 构建对应版本
    implementation 'com.github.Atian10:AndroidBanner:1.1.0'
}
```

> **已发布**：`v1.0.7` Tag 已推送，JitPack 构建已通过。`v1.1.0` 待发布（含标题样式控制 API + 触摸恢复修复）。构建状态可在 `https://jitpack.io/com/github/Atian10/AndroidBanner` 查看。

## 使用示例

### 基础用法（默认配置）

```java
BannerConfig config = new BannerConfig.Builder().build();
List<BannerBean> bannerList = buildBannerData();

binding.bannerView.setConfig(config)
        .setData(bannerList)
        .setImageLoader(new GlideImageLoader())
        .setOnBannerClickListener((position, banner) -> {
            Toast.makeText(this, "点击了第" + (position + 1) + "条", Toast.LENGTH_SHORT).show();
        })
        .start(this);  // 传入 LifecycleOwner，自动感知生命周期
```

### 自定义配置

```java
BannerConfig config = new BannerConfig.Builder()
        .interval(5000L)
        .loop(false)
        .indicatorType(IndicatorType.NUMBER)
        .cardStyle(CardStyle.CARD)
        .animType(AnimType.FLIP)
        .build();
```

### 标题样式控制（v1.1.0 新增）

```java
// 隐藏标题
BannerConfig config = new BannerConfig.Builder()
        .titleVisible(false)
        .build();

// 自定义标题颜色
BannerConfig config = new BannerConfig.Builder()
        .titleVisible(true)
        .titleBgColor(Color.parseColor("#80FF0000"))  // 半透明红底
        .titleTextColor(Color.YELLOW)                    // 黄色文字
        .build();
```

### 运行时切换配置

```java
BannerConfig newConfig = new BannerConfig.Builder()
        .animType(AnimType.DEPTH)
        .build();
binding.bannerView.setConfig(newConfig)
        .restart(this);  // 停止当前轮播并重新启动
```

> 完整对接说明请参考 [docs/INTEGRATION.md](./docs/INTEGRATION.md)

## 发布能力现状

### ✅ 已支持：源码依赖 + 本地 AAR + JitPack 远程依赖

- **源码模块依赖**：直接 `implementation project(':banner')`
- **本地 AAR**：`./gradlew :banner:assembleRelease` 生成 AAR 文件
- **JitPack 远程依赖**：已配置 `maven-publish` 插件，打 Tag 后自动发布
  - 依赖坐标：`com.github.Atian10:AndroidBanner:<tag>`
  - 版本号由 [gradle.properties](./banner/gradle.properties) 的 `VERSION_NAME` 控制（当前 1.1.0）

> ⚠️ **v1.1.0 破坏性变更**：`BannerViewHolder.bind()` 方法签名新增 `BannerConfig config` 参数。自定义 ViewHolder 的用户需适配此变更，详见 [INTEGRATION.md](./docs/INTEGRATION.md)。

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
| L01 | namespace 冲突 | :banner namespace 改为 com.atian.banner.lib，Java 包名不变 |
| L02 | R 类引用 | :app 通过 import com.atian.banner.lib.R 访问 banner 资源 |
| L03 | CardStyle 配置无效 | BannerView 根据 CardStyle 启用 clipToPadding + NONE 自动 fallback SCALE |
| P08 | 触摸滑动后自动轮播不恢复 | 新增 onPageScrollStateChanged 监听，拖拽时暂停、IDLE 时恢复（isUserDragging 标志区分） |
| P09 | Demo 颜色按钮副作用 | 移除颜色切换按钮中多余的 currentTitleVisible=true 覆盖 |
| D05 | banner_item.xml 标题硬编码背景色 | 移除 XML 中 android:background，改由 BannerConfig API 控制 |

## 依赖关系

- `:app` 依赖 `:banner`
- `:banner` 通过 `api` 传递 AndroidX 依赖
- `:banner` 中 Glide 为 `compileOnly`，宿主需自带 Glide 运行时依赖

## 许可证

本项目基于 [Apache License 2.0](./LICENSE) 开源。

- 商业使用：允许
- 修改分发：允许（需保留版权声明）
- 专利授权：包含
- 责任限制：原作者不承担任何责任

> 切换到 Apache 2.0 后，使用者可合法使用、修改、分发本仓库代码。如需二次封装为私有库，建议在 NOTICE 文件中注明原作者。

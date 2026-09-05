# AndroidBanner

> 轻量级 Android Java 轮播组件，支持近似无限循环、自动播放、多种动画与指示器，可通过源码或本地 AAR 集成；仓库已包含 JitPack 配置，但远程产物尚未验证

## 简介

- **Demo 应用**：`app` 模块，演示主要配置功能
- **Library 模块**：`banner` 模块，对外提供轮播能力

## 已实现功能

### 核心轮播

- **基础轮播**：基于 ViewPager2 + RecyclerView.Adapter 实现横向轮播
- **近似无限循环**：`loop=true` 时 `getItemCount()` 返回 `Integer.MAX_VALUE`，启动后定位到中间区间，提供足够大的双向滑动范围
- **自动播放**：可配置轮播间隔，onPause 暂停 / onResume 恢复，避免后台耗电
- **生命周期感知**：通过 DefaultLifecycleObserver 自动感知宿主生命周期；Activity 传入自身，Fragment 传入 `getViewLifecycleOwner()`

### 可配置化（BannerConfig Builder 模式）

通过 `BannerConfig.Builder()` 链式配置以下选项：

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `interval` | long | 3000L | 轮播间隔（毫秒），必须大于 0 |
| `loop` | boolean | true | 是否启用超大区间循环；关闭后手动滑动为有限列表，但自动播放到末尾仍会回到第一项 |
| `indicatorType` | IndicatorType | DOT | 指示器类型（DOT/NUMBER） |
| `indicatorVisible` | boolean | true | 是否显示指示器 |
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
| `SCALE` | ScalePageTransformer | 非选中页缩放 0.7 + 透明度 0.3 |
| `FLIP` | FlipPageTransformer | 沿 Y 轴 3D 翻转 |
| `FADE` | FadePageTransformer | 透明度淡入淡出 + 轻微缩放 |
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
│       │   └── BannerRvAdapter.java           # Banner 适配器（支持近似无限循环 + 泛型）
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
include ':banner'
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

### 方式三：远程依赖（JitPack，需先验证构建）

仓库已配置 `maven-publish` 和 `jitpack.yml`。JitPack 会在首次请求 Git Tag、提交或分支版本时按需构建；只有构建成功后，远程依赖才可使用。

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
    // 版本应对应远端 Git Tag；以下坐标仍需以 JitPack 实际构建结果为准
    implementation 'com.github.Atian10:AndroidBanner:v1.1.2'
}
```

> **当前版本**：`v1.1.2` 是发布链路修复版：在保留 `v1.1.1` 功能修复的基础上，将损坏的 POSIX `gradlew` 恢复为 Gradle 7.5 标准脚本，修复 JitPack 在 Gradle 启动前失败的问题。JitPack 坐标只有在对应 Tag 构建成功后才可使用；请以 JitPack 构建结果和宿主项目的实际依赖解析为准。

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
        .start(this);  // Activity
```

Fragment 应在 `onViewCreated` 之后把最后一行改为 `.start(getViewLifecycleOwner())`，确保 View 销毁时及时停止轮播并释放引用。

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
        .setData(bannerList)  // setData 会用新配置重建 Adapter
        .setImageLoader(new GlideImageLoader())
        // 如需点击回调，应在这里重新调用 setOnBannerClickListener(...)
        .restartKeepPosition(this);  // 数据条数不变时保持当前索引；Fragment 使用 getViewLifecycleOwner()
```

`setData(...)` 会重建 Adapter，因此图片加载器和点击监听需要重新设置。数据条数不变的配置切换可用 `restartKeepPosition(...)` 保持当前真实索引；若数据条数或顺序改变，位置或业务对象不保证保持不变。若希望从第一项重新开始，使用 `restart(...)`。

> 完整对接说明请参考 [docs/INTEGRATION.md](./docs/INTEGRATION.md)

## 发布能力现状

### 当前状态：源码与本地 AAR 已配置，JitPack 待验证

- **源码模块依赖**：直接 `implementation project(':banner')`
- **本地 AAR**：已提供 `./gradlew :banner:assembleRelease` 构建入口，实际产物仍需在可用的 Java/Android 构建环境中验证
- **JitPack 远程依赖**：已配置 `maven-publish` 和 `jitpack.yml`，但当前版本尚未取得成功构建和宿主依赖解析证据
  - 待验证坐标：`com.github.Atian10:AndroidBanner:<tag>`

> ⚠️ **v1.1.0 破坏性变更**：`BannerViewHolder.bind()` 方法签名新增 `BannerConfig config` 参数。自定义 ViewHolder 的用户需适配此变更，详见 [INTEGRATION.md](./docs/INTEGRATION.md)。

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

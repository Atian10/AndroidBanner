# Banner 轮播组件对接指南

> 本文档面向需要在本项目中集成轮播功能的开发者，介绍如何依赖 `:banner` 模块并完成全部功能对接。

---

## 一、概述

`:banner` 是一个独立的 Android Library 模块，封装了 ViewPager2 + 指示器 + 自动轮播 + 动画系统，支持：

- 真无限循环（双向滑动约 10 亿次）
- 生命周期自动感知（onResume 轮播 / onPause 暂停 / onDestroy 释放）
- 5 种切换动画（NONE / SCALE / FLIP / FADE / DEPTH）
- 2 种指示器（圆点 / 数字）
- 2 种卡片样式（普通 / 卡片）
- 可扩展的图片加载（Glide / Picasso / Coil）
- 可扩展的数据模型（实现 `IBannerData` 即可）
- 可扩展的 item 布局（ViewHolder 工厂模式）

---

## 二、环境要求

| 项目 | 要求 |
|------|------|
| compileSdk | ≥ 33 |
| minSdk | ≥ 21 |
| Java 版本 | Java 11 |
| AndroidX | 必须启用 |
| 依赖框架 | Glide 4.x（如使用默认 `GlideImageLoader`） |

`:banner` 模块会传递以下依赖（`api` 依赖，宿主无需重复声明）：

- `androidx.appcompat:appcompat:1.6.1`
- `androidx.constraintlayout:constraintlayout:2.1.4`
- `androidx.recyclerview:recyclerview:1.3.2`
- `androidx.viewpager2:viewpager2:1.0.0`
- `androidx.lifecycle:lifecycle-common:2.5.1 (transitive via appcompat)`
- `androidx.lifecycle:lifecycle-runtime:2.5.1 (transitive via appcompat)`
- `com.google.android.material:material:1.9.0`

> **注意**：Glide 在 `:banner` 中是 `compileOnly`，宿主项目必须自行引入 Glide 运行时依赖，详见 [第五节·使用默认图片加载器](#51-使用默认图片加载器glide)。

---

## 三、接入方式

### 方式一：源码模块依赖（推荐，适用于同仓库内部项目）

**步骤 1**：在宿主项目根目录 `settings.gradle` 中引入 `:banner` 模块：

```gradle
include ':app', ':banner'
project(':banner').projectDir = new File('path/to/AndroidBanner/banner')
```

**步骤 2**：在宿主 `:app` 模块的 `build.gradle` 中添加依赖：

```gradle
dependencies {
    implementation project(':banner')
}
```

### 方式二：AAR 依赖（适用于跨仓库发布）

**步骤 1**：在 `AndroidBanner` 仓库执行 AAR 构建：

```bash
cd path/to/AndroidBanner
./gradlew :banner:assembleRelease
```

产物路径：`banner/build/outputs/aar/banner-release.aar`

**步骤 2**：将 AAR 放入宿主项目的 `libs/` 目录，并在 `build.gradle` 中声明：

```gradle
dependencies {
    implementation files('libs/banner-release.aar')
    // banner 的 api 依赖需宿主自行提供（AAR 不携带传递依赖）
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.viewpager2:viewpager2:1.0.0'
    implementation 'androidx.lifecycle:lifecycle-common:2.5.1'
    implementation 'androidx.lifecycle:lifecycle-runtime:2.5.1'
    implementation 'com.google.android.material:material:1.9.0'
}
```

### 方式三：JitPack 远程依赖（推荐，跨项目最便捷）

> 仓库已配置 `maven-publish` 插件和 [jitpack.yml](../jitpack.yml)，支持 JitPack 自动构建。

**步骤 1**：在宿主项目根目录 `settings.gradle` 的 `dependencyResolutionManagement.repositories` 中添加 JitPack 仓库：

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }   // 新增 JitPack
    }
}
```

**步骤 2**：在宿主 `:app` 模块 `build.gradle` 中添加依赖：

```gradle
dependencies {
    implementation 'com.github.Atian10:AndroidBanner:1.1.0'
}
```

**步骤 3**：确认 [banner/build.gradle](../banner/build.gradle) 中的 Glide 依赖。

JitPack 发布的 AAR 会携带 `api` 依赖（AndroidX 系列），但 Glide 是 `compileOnly`，宿主仍需自行引入：

```gradle
dependencies {
    implementation 'com.github.Atian10:AndroidBanner:1.1.0'
    // 如使用 GlideImageLoader，需额外引入 Glide
    implementation 'com.github.bumptech.glide:glide:4.15.1'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.15.1'
}
```

> **版本说明**：`1.1.0` 对应 GitHub 的 `v1.1.0` Tag（待发布）。发布新版本时：
> 1. 修改 [banner/gradle.properties](../banner/gradle.properties) 中 `VERSION_NAME` 为新版本
> 2. 提交并推送代码
> 3. 在 GitHub 创建对应 Tag（如 `v1.1.0`）并推送
> 4. JitPack 会自动触发构建，状态可在 `https://jitpack.io/com/github/Atian10/AndroidBanner` 查看

> ⚠️ **v1.1.0 破坏性变更**：`BannerViewHolder.bind()` 方法签名新增 `BannerConfig config` 参数。自定义 ViewHolder 的用户需适配，详见[第 6.5 节](#65-标题样式控制v110-新增)。

**JitPack 优势**：
- 无需账号审核（Maven Central 需要）
- 无需手动发布命令
- 打 Tag 即发布，版本管理清晰
- 支持分支构建（`com.github.Atian10:AndroidBanner:main-SNAPSHOT`）

---

## 四、快速开始（5 分钟集成）

### 4.1 布局引入 BannerView

在宿主布局 XML 中添加 `BannerView`：

```xml
<com.atian.banner.view.BannerView
    android:id="@+id/banner_view"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    android:clipChildren="false" />
```

> **提示**：若使用 `CardStyle.CARD`（卡片样式），父容器也需设置 `android:clipChildren="false"`，否则两侧卡片会被裁剪。

### 4.2 准备数据

使用内置 `BannerBean`（实现 `IBannerData`）：

```java
List<BannerBean> bannerList = new ArrayList<>();
bannerList.add(new BannerBean("https://example.com/1.jpg", "标题1", "https://link1.com"));
bannerList.add(new BannerBean("https://example.com/2.jpg", "标题2", "https://link2.com"));
bannerList.add(new BannerBean("https://example.com/3.jpg", "标题3", "https://link3.com"));
```

### 4.3 配置并启动

在 Activity / Fragment 中链式调用：

```java
BannerConfig config = new BannerConfig.Builder()
        .interval(3000L)
        .loop(true)
        .indicatorType(IndicatorType.DOT)
        .cardStyle(CardStyle.NORMAL)
        .animType(AnimType.SCALE)
        .build();

binding.bannerView.setConfig(config)
        .setData(bannerList)
        .setImageLoader(new GlideImageLoader())  // 使用默认 Glide 加载器
        .setOnBannerClickListener((position, banner) -> {
            // 处理点击
            Toast.makeText(this, "点击了第" + (position + 1) + "条", Toast.LENGTH_SHORT).show();
        })
        .start(this);  // 传入 LifecycleOwner，自动感知生命周期
```

### 4.4 完成

运行后即可看到自动轮播效果。无需手动在 `onResume` / `onPause` 中启停，`BannerView` 通过 `DefaultLifecycleObserver` 自动管理。

---

## 五、核心 API

### 5.1 BannerView 方法清单

| 方法 | 说明 | 链式返回 |
|------|------|----------|
| `setConfig(BannerConfig)` | 设置配置（必须在 `setData` 前调用） | `BannerView` |
| `setData(List<? extends IBannerData>)` | 设置轮播数据 | `BannerView` |
| `setImageLoader(IImageLoader)` | 设置图片加载器 | `BannerView` |
| `setOnBannerClickListener(OnBannerClickListener<IBannerData>)` | 设置点击监听 | `BannerView` |
| `setViewHolderFactory(BannerViewHolderFactory<IBannerData>)` | 设置自定义布局工厂 | `BannerView` |
| `start(LifecycleOwner)` | 启动（首次） | `void` |
| `restart(LifecycleOwner)` | 重启（切换配置后调用） | `void` |

**链式调用顺序**：

```
setConfig → [setViewHolderFactory] → setData → setImageLoader → setOnBannerClickListener → start
```

### 5.2 BannerConfig 配置项

通过 `BannerConfig.Builder()` 构造：

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `interval(long)` | long | `3000L` | 轮播间隔（毫秒） |
| `loop(boolean)` | boolean | `true` | 是否无限循环 |
| `indicatorType(IndicatorType)` | IndicatorType | `DOT` | 指示器类型 |
| `indicatorVisible(boolean)` | boolean | `true` | 是否显示指示器 |
| `cardStyle(CardStyle)` | CardStyle | `NORMAL` | 卡片样式 |
| `animType(AnimType)` | AnimType | `SCALE` | 切换动画类型 |
| `titleVisible(boolean)` | boolean | `true` | 是否显示标题（v1.1.0 新增） |
| `titleBgColor(int)` | int | `#80000000` | 标题背景色（v1.1.0 新增） |
| `titleTextColor(int)` | int | `#FFFFFF` | 标题文字颜色（v1.1.0 新增） |

### 5.3 枚举说明

#### IndicatorType（指示器类型）

| 取值 | 效果 |
|------|------|
| `DOT` | 圆点指示器，选中/未选中状态切换 |
| `NUMBER` | 数字指示器，显示 "当前位/总数"（如 1/4） |

#### CardStyle（卡片样式）

| 取值 | 效果 |
|------|------|
| `NORMAL` | 普通样式，全屏铺满 |
| `CARD` | 卡片样式，两侧缩放 + padding 留白，两侧卡片可见 |

> **协同规则**：`CardStyle.CARD` + `AnimType.NONE` 时，自动 fallback 到 `SCALE`，保证卡片视觉效果生效。

#### AnimType（切换动画）

| 取值 | 效果 |
|------|------|
| `NONE` | 无动画 |
| `SCALE` | 缩放 + 透明度（两侧页缩小 0.7，透明度 0.3） |
| `FLIP` | 3D 翻转（沿 Y 轴旋转 135°） |
| `FADE` | 淡入淡出（透明度 + 轻微缩放 0.85） |
| `DEPTH` | 深度（左侧页缩小 0.5，右侧页从右侧平移进入） |

---

## 六、高级用法

### 6.1 使用默认图片加载器（Glide）

`:banner` 提供了基于 Glide 的默认实现 `GlideImageLoader`。因 Glide 在 `:banner` 中为 `compileOnly`，宿主必须自行引入：

```gradle
dependencies {
    implementation 'com.github.bumptech.glide:glide:4.15.1'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.15.1'
}
```

引入后即可直接使用：

```java
binding.bannerView.setImageLoader(new GlideImageLoader());
```

### 6.2 自定义图片加载器（Picasso / Coil 等）

实现 `IImageLoader` 接口：

```java
public class PicassoImageLoader implements IImageLoader {
    @Override
    public void loadImage(Context context, String url, ImageView target) {
        if (context == null || target == null || url == null || url.isEmpty()) {
            return;
        }
        Picasso.get().load(url).into(target);
    }
}
```

注入：

```java
binding.bannerView.setImageLoader(new PicassoImageLoader());
```

### 6.3 自定义数据模型

内置 `BannerBean` 已实现 `IBannerData`，可直接使用。若宿主有自有数据模型，实现接口即可，无需继承 `BannerBean`：

```java
public class HomeAdBean implements IBannerData {
    private String imgUrl;
    private String adTitle;
    private String adLink;

    @Override
    public String getImageUrl() { return imgUrl; }

    @Override
    public String getTitle() { return adTitle; }

    @Override
    public String getLinkUrl() { return adLink; }
}
```

使用：

```java
List<HomeAdBean> adList = fetchAdList();
binding.bannerView.setData(adList);  // List<HomeAdBean> 是 List<? extends IBannerData> 的子类型
```

### 6.4 自定义 item 布局

默认布局 `banner_item.xml` 仅展示图片 + 标题。如需展示更多内容（如角标、价格、按钮），按以下步骤：

**步骤 1**：创建自定义布局 `layout/my_banner_item.xml`。

**步骤 2**：实现 `BannerViewHolder`：

```java
public class MyBannerViewHolder extends BannerViewHolder<IBannerData> {
    private final ImageView ivImage;
    private final TextView tvTitle;
    private final TextView tvPrice;

    public MyBannerViewHolder(View itemView) {
        super(itemView);
        ivImage = itemView.findViewById(R.id.iv_image);
        tvTitle = itemView.findViewById(R.id.tv_title);
        tvPrice = itemView.findViewById(R.id.tv_price);
    }

    @Override
    public void bind(IBannerData data, int position, IImageLoader imageLoader) {
        tvTitle.setText(data.getTitle());
        if (imageLoader != null) {
            imageLoader.loadImage(itemView.getContext(), data.getImageUrl(), ivImage);
        }
        // 若数据模型含额外字段，可在此绑定
    }
}
```

**步骤 3**：实现 `BannerViewHolderFactory`：

```java
public class MyViewHolderFactory implements BannerViewHolderFactory<IBannerData> {
    @Override
    public BannerViewHolder<IBannerData> createViewHolder(@NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_banner_item, parent, false);
        return new MyBannerViewHolder(view);
    }
}
```

**步骤 4**：注入（须在 `setData` 前调用）：

```java
binding.bannerView.setConfig(config)
        .setViewHolderFactory(new MyViewHolderFactory())  // 自定义布局
        .setData(bannerList)
        .setImageLoader(new GlideImageLoader())
        .start(this);
```

> **v1.1.0 变更**：`BannerViewHolder.bind()` 签名已变更为 `bind(T data, int position, IImageLoader imageLoader, BannerConfig config)`。自定义 ViewHolder 需增加 `config` 参数，可通过 config 读取标题样式配置。

### 6.5 标题样式控制（v1.1.0 新增）

通过 `BannerConfig` 可控制默认布局的标题显隐、背景色、文字颜色，无需自定义布局。

**隐藏标题**：

```java
BannerConfig config = new BannerConfig.Builder()
        .titleVisible(false)
        .build();
```

**自定义标题颜色**：

```java
BannerConfig config = new BannerConfig.Builder()
        .titleVisible(true)
        .titleBgColor(Color.parseColor("#80FF0000"))  // 半透明红底
        .titleTextColor(Color.YELLOW)                    // 黄色文字
        .build();
```

**运行时切换标题样式**：

```java
BannerConfig newConfig = new BannerConfig.Builder()
        .titleVisible(false)
        .build();
binding.bannerView.setConfig(newConfig)
        .restart(this);
```

> **注意**：`titleVisible` / `titleBgColor` / `titleTextColor` 仅对默认 `DefaultBannerViewHolder` 生效。自定义 ViewHolder 需自行在 `bind()` 中读取 `config` 并应用样式。

---

## 七、生命周期说明

`BannerView` 实现了 `DefaultLifecycleObserver`，传入 `LifecycleOwner`（Activity / Fragment）后自动管理：

| 生命周期事件 | BannerView 行为 |
|-------------|------------------|
| `onResume` | 启动自动轮播 |
| `onPause` | 停止自动轮播 |
| `onDestroy` | 停止轮播 + 反注册回调 + 移除观察者 |

**宿主无需手动调用** `stop()` / `release()` 等方法。

**切换配置**：如需在运行时切换配置（如切换动画类型），调用 `restart()`：

```java
BannerConfig newConfig = new BannerConfig.Builder()
        .animType(AnimType.FLIP)
        .build();
binding.bannerView.setConfig(newConfig)
        .restart(this);  // 会停止当前轮播并重新启动
```

---

## 八、混淆规则

`:banner` 模块已通过 `consumerProguardFiles` 随 AAR 自动分发混淆规则，宿主开启混淆时无需额外配置。

如使用 AAR 方式接入且混淆规则未自动应用，手动添加：

```proguard
# Banner 库 ViewBinding 保留
-keep class com.atian.banner.lib.databinding.** { *; }
-keep class com.atian.banner.view.BannerView { *; }
-keep class com.atian.banner.viewholder.** { *; }
```

> **注意**：`consumer-proguard-rules.pro` 已包含公共 API 类的 keep 规则（config/enums/interfaces/bean/view/imageloader/viewholder），通常无需手动添加。仅当宿主自定义 `IBannerData` 实现类被混淆导致字段丢失时，才需额外添加：
> `-keep class * implements com.atian.banner.interfaces.IBannerData { *; }`

---

## 九、常见问题 FAQ

### Q1：图片不显示，只显示标题？

**原因**：未调用 `setImageLoader()`，或 Glide 依赖未引入。

**解决**：
1. 确认调用了 `setImageLoader(new GlideImageLoader())`
2. 确认宿主 `build.gradle` 已引入 Glide 运行时依赖（见 [6.1 节](#61-使用默认图片加载器glide)）

### Q2：CardStyle.CARD 没有卡片效果？

**原因**：父容器或 BannerView 未设置 `clipChildren="false"`。

**解决**：在布局 XML 中为 BannerView 及其父容器添加 `android:clipChildren="false"`。

### Q3：关闭循环（loop=false）后，滑到末尾会回跳？

**原因**：`loop=false` 时 `bannerRunnable` 使用 `(currentPosition + 1) % realCount`，到末尾会回绕到第 0 项。

**解决**：这是预期行为。如需"到末尾停止"，可监听 `onPageSelected` 并在到达末尾时调用 `stopAutoPlay`（当前版本未提供公开 `stop` 方法，可后续迭代）。

### Q4：R 类找不到（`com.atian.banner.lib.R`）？

**原因**：`:banner` 模块的 namespace 是 `com.atian.banner.lib`，R 类位于该包下。

**解决**：在宿主访问 banner 资源处添加 `import com.atian.banner.lib.R;`。注意：Java 包名（`com.atian.banner.*`）与 namespace（`com.atian.banner.lib`）不同，这是为避免 R 类冲突的设计。

### Q5：自定义数据模型的泛型类型转换错误？

**原因**：`setData(List<? extends IBannerData>)` 接受任意实现 `IBannerData` 的列表，内部会拷贝为 `List<IBannerData>`。

**解决**：在 `OnBannerClickListener` 回调中，`banner` 参数类型为 `IBannerData`。如需访问子类字段，需自行强转：

```java
.setOnBannerClickListener((position, banner) -> {
    HomeAdBean ad = (HomeAdBean) banner;  // 强转为子类
    String link = ad.getLinkUrl();
})
```

### Q6：如何监听页面切换事件？

**当前版本**：`OnPageChangeCallback` 在 `BannerView` 内部注册，未对外暴露。如需监听切换事件，可通过 `OnBannerClickListener` 间接获取（点击时返回 `position`）。

**后续迭代**：可新增 `setOnPageChangeListener` 公开方法。

---

## 十、API 速查表

| 类 / 接口 | 包路径 | 用途 |
|-----------|--------|------|
| `BannerView` | `com.atian.banner.view` | 统一入口 |
| `BannerConfig` | `com.atian.banner.config` | 配置（Builder 模式） |
| `BannerBean` | `com.atian.banner.bean` | 默认数据模型 |
| `IBannerData` | `com.atian.banner.interfaces` | 数据模型接口 |
| `IImageLoader` | `com.atian.banner.interfaces` | 图片加载器接口 |
| `OnBannerClickListener` | `com.atian.banner.interfaces` | 点击回调接口 |
| `GlideImageLoader` | `com.atian.banner.imageloader` | Glide 默认实现 |
| `BannerViewHolder` | `com.atian.banner.viewholder` | ViewHolder 基类 |
| `BannerViewHolderFactory` | `com.atian.banner.viewholder` | ViewHolder 工厂接口 |
| `IndicatorType` | `com.atian.banner.enums` | 指示器枚举 |
| `CardStyle` | `com.atian.banner.enums` | 卡片样式枚举 |
| `AnimType` | `com.atian.banner.enums` | 动画类型枚举 |

---

## 附录：完整示例

参见 `app` 模块 [BannerActivity.java](../app/src/main/java/com/atian/banner/ui/BannerActivity.java)，提供可切换配置的完整 Demo。

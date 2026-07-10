# android-banner

> Android Java 轮播页 Demo 应用 - 实现可复用的轮播组件，后续可演化成独立库

## 仓库定位

本仓库为**闭源私有**的 Android Java 轮播页 Demo 应用，用于开发与验证可复用的轮播组件（Banner / Carousel）。组件成熟后可抽取为独立 Android Library 模块，供其他业务项目依赖。

## 功能规划（拟）

- **基础轮播**：基于 ViewPager2 实现横向自动轮播
- **指示器**：支持圆点、数字、自定义指示器样式
- **自动播放**：可配置轮播间隔、是否循环、是否自动播放
- **生命周期感知**：onPause 暂停 / onResume 恢复，避免后台耗电
- **图片加载**：对接 Glide，支持网络图与本地资源
- **点击事件**：OnBannerClickListener 回调
- **多样式**：支持普通 Banner、卡片 Banner（带缩放/透明度）

## 技术栈

- 语言：Java
- View 绑定：ViewBinding
- 图片加载：Glide
- ViewPager2 + RecyclerView Adapter
- 最低 SDK：按项目 build.gradle 配置

## 项目结构（拟）

```
android-banner/
├── app/                        # Demo 应用模块
│   └── src/main/java/
│       ├── ui/                 # 演示页面
│       ├── adapter/            # Banner 适配器
│       └── bean/               # 数据模型
├── banner/                     # （未来）独立 Library 模块
└── docs/                       # 内部文档
```

## 依赖关系

本 Demo 应用为独立项目。轮播组件成熟后，将抽取到 [android-core](https://github.com/Atian10/android-core) 公共基础库的 `core-ui` 模块中，供其他业务项目复用。

## 许可证

本仓库为**闭源专有资产**，All Rights Reserved。详见 [LICENSE](./LICENSE) 文件。

未经著作权人事先书面许可，任何人不得复制、修改、分发或使用本仓库的任何部分。

# Banner Library 混淆规则（消费者规则，会自动应用到宿主项目）

# 保留公共 API 类（Builder 模式/枚举/接口/View/图片加载器/ViewHolder）
-keep public class com.atian.banner.config.** { *; }
-keep public class com.atian.banner.enums.** { *; }
-keep public class com.atian.banner.interfaces.** { *; }
-keep public class com.atian.banner.bean.** { *; }
-keep public class com.atian.banner.view.** { *; }
-keep public class com.atian.banner.imageloader.** { *; }
-keep public class com.atian.banner.viewholder.** { *; }

# 保留 Builder 模式相关的方法（避免反射创建失败）
-keepclassmembers class com.atian.banner.config.BannerConfig$Builder {
    public <methods>;
}

# 保留 OnBannerClickListener 回调（宿主项目实现）
-keep interface com.atian.banner.interfaces.OnBannerClickListener { *; }

# 保留泛型签名（Adapter 使用泛型时需要）
-keepattributes Signature, *Annotation*, SourceFile, LineNumberTable

# Glide 规则（Glide 官方推荐）
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder { *** rewind(); }

# ViewBinding（Library 模块生成的 Binding 类，namespace 为 com.atian.banner.lib）
-keep class com.atian.banner.lib.databinding.** { *; }

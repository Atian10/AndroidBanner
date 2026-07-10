# Glide 混淆规则
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
    *** rewind();
}
-keep class com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool {
    <init>(...);
}
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$ImageType {
    *** values();
}

# ViewPager2
-keep class androidx.viewpager2.widget.ViewPager2 { *; }

# 保留 Bean 类
-keep class com.atian.banner.bean.** { *; }

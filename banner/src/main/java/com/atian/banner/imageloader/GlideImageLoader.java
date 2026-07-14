package com.atian.banner.imageloader;

import android.content.Context;
import android.widget.ImageView;

import com.atian.banner.interfaces.IImageLoader;
import com.atian.banner.util.LogUtils;
import com.bumptech.glide.Glide;

/**
 * 基于 Glide 的图片加载器默认实现
 * <p>使用此实现时，宿主项目必须引入 Glide 运行时依赖：
 * <pre>
 * implementation 'com.github.bumptech.glide:glide:4.15.1'
 * annotationProcessor 'com.github.bumptech.glide:compiler:4.15.1'
 * </pre>
 * </p>
 * <p>如需使用其他图片加载框架，请实现 {@link IImageLoader} 接口</p>
 */
public class GlideImageLoader implements IImageLoader {

    private static final String TAG = "GlideImageLoader";

    @Override
    public void loadImage(Context context, String url, ImageView target) {
        if (context == null || target == null) {
            LogUtils.w(TAG, "loadImage：  context 或 target 为 null，跳过加载");
            return;
        }
        if (url == null || url.isEmpty()) {
            LogUtils.w(TAG, "loadImage：  url 为空，跳过加载");
            return;
        }
        Glide.with(context)
                .load(url)
                .centerCrop()
                .into(target);
    }
}

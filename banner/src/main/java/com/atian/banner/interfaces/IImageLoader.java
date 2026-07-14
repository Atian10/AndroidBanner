package com.atian.banner.interfaces;

import android.content.Context;
import android.widget.ImageView;

/**
 * 图片加载器接口
 * <p>解耦 Banner 库与具体图片加载框架（Glide/Picasso/Coil 等），
 * 用户需实现此接口并注入到 {@link com.atian.banner.view.BannerView}</p>
 * <p>Banner 库提供默认的 {@link com.atian.banner.imageloader.GlideImageLoader}，
 * 用户也可自定义实现</p>
 */
public interface IImageLoader {

    /**
     * 加载图片到 ImageView
     *
     * @param context  上下文
     * @param url      图片地址（网络或本地）
     * @param target   目标 ImageView
     */
    void loadImage(Context context, String url, ImageView target);
}

package com.atian.banner.viewholder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.atian.banner.interfaces.IBannerData;
import com.atian.banner.interfaces.IImageLoader;

/**
 * Banner ViewHolder 基类
 * <p>用户自定义布局时需继承此类并实现 {@link #bind(IBannerData, int, IImageLoader)} 方法</p>
 * <p>点击事件由 Adapter 统一处理，ViewHolder 仅负责数据绑定</p>
 *
 * @param <T> Banner 数据类型
 */
public abstract class BannerViewHolder<T extends IBannerData> extends RecyclerView.ViewHolder {

    public BannerViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * 绑定数据到视图
     *
     * @param data        数据项
     * @param position    真实数据位置
     * @param imageLoader 图片加载器（可能为 null）
     */
    public abstract void bind(T data, int position, IImageLoader imageLoader);
}

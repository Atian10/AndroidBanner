package com.atian.banner.viewholder;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.atian.banner.interfaces.IBannerData;

/**
 * Banner ViewHolder 工厂接口
 * <p>用户自定义布局时需实现此接口，在 {@link #createViewHolder(ViewGroup)} 中
 * 加载自定义布局并创建 {@link BannerViewHolder} 实例</p>
 * <p>示例：
 * <pre>
 * public class CustomViewHolderFactory implements BannerViewHolderFactory&lt;IBannerData&gt; {
 *     &#64;Override
 *     public BannerViewHolder&lt;IBannerData&gt; createViewHolder(ViewGroup parent) {
 *         View view = LayoutInflater.from(parent.getContext())
 *                 .inflate(R.layout.my_banner_item, parent, false);
 *         return new CustomViewHolder(view);
 *     }
 * }
 * </pre>
 * </p>
 *
 * @param <T> Banner 数据类型
 */
public interface BannerViewHolderFactory<T extends IBannerData> {

    /**
     * 创建 ViewHolder
     *
     * @param parent 父容器
     * @return BannerViewHolder 实例
     */
    @NonNull
    BannerViewHolder<T> createViewHolder(@NonNull ViewGroup parent);
}

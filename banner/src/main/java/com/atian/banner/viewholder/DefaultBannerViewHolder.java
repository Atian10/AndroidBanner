package com.atian.banner.viewholder;

import android.view.View;

import com.atian.banner.lib.databinding.BannerItemBinding;
import com.atian.banner.interfaces.IBannerData;
import com.atian.banner.interfaces.IImageLoader;

/**
 * 默认 Banner ViewHolder 实现
 * <p>使用 {@link BannerItemBinding}（banner_item.xml）布局，
 * 展示图片和标题</p>
 */
public class DefaultBannerViewHolder extends BannerViewHolder<IBannerData> {

    private final BannerItemBinding binding;

    public DefaultBannerViewHolder(BannerItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(IBannerData data, int position, IImageLoader imageLoader) {
        // 设置标题
        binding.tvBannerTitle.setText(data.getTitle());
        // 加载图片
        if (imageLoader != null) {
            imageLoader.loadImage(itemView.getContext(), data.getImageUrl(), binding.ivBannerImage);
        }
    }
}

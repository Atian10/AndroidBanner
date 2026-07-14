package com.atian.banner.viewholder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.atian.banner.lib.databinding.BannerItemBinding;
import com.atian.banner.interfaces.IBannerData;

/**
 * 默认 Banner ViewHolder 工厂
 * <p>创建 {@link DefaultBannerViewHolder}，使用标准 banner_item.xml 布局</p>
 * <p>用户未提供自定义工厂时，BannerView 默认使用此工厂</p>
 */
public class DefaultBannerViewHolderFactory implements BannerViewHolderFactory<IBannerData> {

    @NonNull
    @Override
    public BannerViewHolder<IBannerData> createViewHolder(@NonNull ViewGroup parent) {
        BannerItemBinding binding = BannerItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DefaultBannerViewHolder(binding);
    }
}

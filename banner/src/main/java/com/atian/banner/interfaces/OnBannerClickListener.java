package com.atian.banner.interfaces;

/**
 * 轮播图点击回调接口（泛型）
 * <p>泛型参数 T 需实现 {@link IBannerData}，
 * 使用时传入具体数据类型可避免类型转换</p>
 *
 * @param <T> Banner 数据类型
 */
public interface OnBannerClickListener<T extends IBannerData> {

    /**
     * 轮播图被点击
     *
     * @param position 当前位置（真实数据位置，非循环位置）
     * @param banner   对应数据
     */
    void onBannerClick(int position, T banner);
}

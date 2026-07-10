package com.atian.banner.interfaces;

import com.atian.banner.bean.BannerBean;

/**
 * 轮播图点击回调接口
 */
public interface OnBannerClickListener {

    /**
     * 轮播图被点击
     *
     * @param position 当前位置
     * @param banner   对应数据
     */
    void onBannerClick(int position, BannerBean banner);
}

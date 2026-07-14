package com.atian.banner.transformer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Banner 淡入淡出动画变换器
 * <p>页面透明度渐变，无位移与缩放</p>
 */
public class FadePageTransformer implements ViewPager2.PageTransformer {

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position < -1 || position > 1) {
            // 完全不可见区域，透明度为 0
            page.setAlpha(0f);
        } else {
            // position=0：alpha=1.0（完全显示）
            // position=±1：alpha=0.0（完全透明）
            page.setAlpha(1f - Math.abs(position));
        }
    }
}

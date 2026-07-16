package com.atian.banner.transformer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Banner 淡入淡出动画变换器
 * <p>页面透明度渐变 + 轻微缩放，增强视觉层次感</p>
 */
public class FadePageTransformer implements ViewPager2.PageTransformer {

    /** 非选中页最小缩放比例 */
    private static final float MIN_SCALE = 0.85f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position < -1 || position > 1) {
            // 完全不可见区域，透明度为 0，恢复默认缩放
            page.setAlpha(0f);
            page.setScaleX(1f);
            page.setScaleY(1f);
        } else {
            // position=0：alpha=1.0（完全显示），scale=1.0
            // position=±1：alpha=0.0（完全透明），scale=0.85
            float alpha = 1f - Math.abs(position);
            page.setAlpha(alpha);
            // 透明度越低，缩放越小，增强淡入淡出的视觉层次
            float scale = MIN_SCALE + (1 - MIN_SCALE) * alpha;
            page.setScaleX(scale);
            page.setScaleY(scale);
        }
    }
}

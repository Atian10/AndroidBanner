package com.atian.banner.transformer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Banner 卡片样式页面变换器
 * <p>非选中页缩放至 0.85，透明度降至 0.5，实现卡片堆叠效果</p>
 */
public class ScalePageTransformer implements ViewPager2.PageTransformer {

    /** 非选中页缩放比例 */
    private static final float MIN_SCALE = 0.85f;

    /** 非选中页透明度 */
    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        // position 含义：
        //   0       当前页完全显示
        //  [-1, 0)  左侧页（即将进入/已离开）
        //  (0, 1]   右侧页（即将进入/已离开）
        if (position < -1 || position > 1) {
            // 完全不可见区域，恢复默认
            page.setAlpha(1f);
            page.setScaleX(1f);
            page.setScaleY(1f);
        } else {
            float scale;
            float alpha;
            if (position <= 0) {
                // [-1, 0]：左侧页，从 0.85 渐变到 1.0
                scale = MIN_SCALE + (1 - MIN_SCALE) * (1 + position);
                alpha = MIN_ALPHA + (1 - MIN_ALPHA) * (1 + position);
            } else {
                // (0, 1]：右侧页，从 1.0 渐变到 0.85
                scale = MIN_SCALE + (1 - MIN_SCALE) * (1 - position);
                alpha = MIN_ALPHA + (1 - MIN_ALPHA) * (1 - position);
            }
            page.setScaleX(scale);
            page.setScaleY(scale);
            page.setAlpha(alpha);
        }
    }
}

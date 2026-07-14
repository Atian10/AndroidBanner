package com.atian.banner.transformer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Banner 深度动画变换器
 * <p>左侧页缩小并保持原位，右侧页从右侧缩小进入并平移至原位</p>
 * <p>参考 Google 官方 DepthPageTransformer 变体实现</p>
 */
public class DepthPageTransformer implements ViewPager2.PageTransformer {

    /** 非当前页最小缩放比例 */
    private static final float MIN_SCALE = 0.75f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position < -1 || position > 1) {
            // 完全不可见区域，隐藏并恢复默认
            page.setAlpha(0f);
            page.setScaleX(1f);
            page.setScaleY(1f);
            page.setTranslationX(0f);
        } else if (position <= 0) {
            // [-1, 0]：左侧页（已离开或即将进入）
            // 透明度从 0 渐变到 1
            page.setAlpha(1f + position);
            // 缩放从 0.75 渐变到 1.0
            float scale = MIN_SCALE + (1 - MIN_SCALE) * (1 + position);
            page.setScaleX(scale);
            page.setScaleY(scale);
            // 不位移
            page.setTranslationX(0f);
        } else {
            // (0, 1]：右侧页（即将进入）
            // 始终不透明
            page.setAlpha(1f);
            // 保持原大小
            page.setScaleX(1f);
            page.setScaleY(1f);
            // 从右侧缩小宽度处平移至原位（负值表示向左移动）
            page.setTranslationX(-page.getWidth() * position);
        }
    }
}

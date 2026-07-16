package com.atian.banner.transformer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Banner 3D 翻转动画变换器
 * <p>页面沿 Y 轴旋转，产生 3D 立体翻转效果</p>
 */
public class FlipPageTransformer implements ViewPager2.PageTransformer {

    /** 最大旋转角度（度） */
    private static final float MAX_ROTATION = 135f;

    /** Camera 距离系数（越大透视越弱） */
    private static final float CAMERA_DISTANCE_FACTOR = 20f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        // 设置旋转中心为页面水平中心
        page.setPivotX(page.getWidth() / 2f);
        page.setPivotY(page.getHeight() / 2f);
        // 增大 Camera 距离，避免过度透视变形
        page.setCameraDistance(page.getWidth() * CAMERA_DISTANCE_FACTOR);

        if (position < -1 || position > 1) {
            // 完全不可见区域，隐藏并恢复默认
            page.setAlpha(0f);
            page.setRotationY(0f);
        } else {
            // 可见区域始终显示
            page.setAlpha(1f);
            // position<0（左侧页）：rotationY 为正，向左翻转
            // position>0（右侧页）：rotationY 为负，向右翻转
            // position=0：rotationY=0，正面朝向用户
            page.setRotationY(-position * MAX_ROTATION);
        }
    }
}

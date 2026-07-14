package com.atian.banner.enums;

/**
 * Banner 切换动画类型
 */
public enum AnimType {

    /** 无动画 */
    NONE,

    /** 缩放 + 透明度（两侧卡片缩小半透明） */
    SCALE,

    /** 3D 翻转（沿 Y 轴旋转） */
    FLIP,

    /** 淡入淡出（仅透明度变化） */
    FADE,

    /** 深度（左侧页缩小，右侧页从右侧放大进入） */
    DEPTH
}

package com.atian.banner.config;

import com.atian.banner.enums.AnimType;
import com.atian.banner.enums.CardStyle;
import com.atian.banner.enums.IndicatorType;

/**
 * Banner 轮播配置类（Builder 模式）
 * <p>集中管理轮播间隔、循环模式、指示器类型、卡片样式等可配置项</p>
 */
public class BannerConfig {

    /** 轮播间隔（毫秒） */
    private final long interval;

    /** 是否无限循环 */
    private final boolean loop;

    /** 指示器类型 */
    private final IndicatorType indicatorType;

    /** 卡片样式 */
    private final CardStyle cardStyle;

    /** 切换动画类型 */
    private final AnimType animType;

    private BannerConfig(Builder builder) {
        this.interval = builder.interval;
        this.loop = builder.loop;
        this.indicatorType = builder.indicatorType;
        this.cardStyle = builder.cardStyle;
        this.animType = builder.animType;
    }

    public long getInterval() {
        return interval;
    }

    public boolean isLoop() {
        return loop;
    }

    public IndicatorType getIndicatorType() {
        return indicatorType;
    }

    public CardStyle getCardStyle() {
        return cardStyle;
    }

    public AnimType getAnimType() {
        return animType;
    }

    /**
     * BannerConfig 构造器
     */
    public static class Builder {

        /** 默认轮播间隔 3 秒 */
        private long interval = 3000L;

        /** 默认开启无限循环 */
        private boolean loop = true;

        /** 默认圆点指示器 */
        private IndicatorType indicatorType = IndicatorType.DOT;

        /** 默认普通样式 */
        private CardStyle cardStyle = CardStyle.NORMAL;

        /** 默认缩放动画 */
        private AnimType animType = AnimType.SCALE;

        public Builder interval(long interval) {
            this.interval = interval;
            return this;
        }

        public Builder loop(boolean loop) {
            this.loop = loop;
            return this;
        }

        public Builder indicatorType(IndicatorType indicatorType) {
            this.indicatorType = indicatorType;
            return this;
        }

        public Builder cardStyle(CardStyle cardStyle) {
            this.cardStyle = cardStyle;
            return this;
        }

        public Builder animType(AnimType animType) {
            this.animType = animType;
            return this;
        }

        public BannerConfig build() {
            return new BannerConfig(this);
        }
    }
}

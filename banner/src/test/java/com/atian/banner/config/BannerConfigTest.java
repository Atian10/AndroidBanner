package com.atian.banner.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.atian.banner.enums.AnimType;
import com.atian.banner.enums.CardStyle;
import com.atian.banner.enums.IndicatorType;

import org.junit.Test;

public class BannerConfigTest {

    @Test
    public void defaultConfigUsesDocumentedValues() {
        BannerConfig config = new BannerConfig.Builder().build();

        assertEquals(3000L, config.getInterval());
        assertTrue(config.isLoop());
        assertEquals(IndicatorType.DOT, config.getIndicatorType());
        assertEquals(CardStyle.NORMAL, config.getCardStyle());
        assertEquals(AnimType.SCALE, config.getAnimType());
        assertTrue(config.isTitleVisible());
        assertEquals(0x80000000, config.getTitleBgColor());
        assertEquals(0xFFFFFFFF, config.getTitleTextColor());
        assertTrue(config.isIndicatorVisible());
    }

    @Test
    public void intervalAcceptsPositiveValue() {
        BannerConfig config = new BannerConfig.Builder()
                .interval(5000L)
                .build();

        assertEquals(5000L, config.getInterval());
    }

    @Test(expected = IllegalArgumentException.class)
    public void intervalRejectsZero() {
        new BannerConfig.Builder().interval(0L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void intervalRejectsNegativeValue() {
        new BannerConfig.Builder().interval(-1L);
    }
}

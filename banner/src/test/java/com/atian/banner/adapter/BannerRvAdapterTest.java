package com.atian.banner.adapter;

import static org.junit.Assert.assertEquals;

import com.atian.banner.bean.BannerBean;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class BannerRvAdapterTest {

    @Test
    public void nullDataProducesEmptyAdapter() {
        BannerRvAdapter<BannerBean> adapter = new BannerRvAdapter<>(null, false);

        assertEquals(0, adapter.getRealCount());
        assertEquals(0, adapter.getItemCount());
        assertEquals(0, adapter.getMiddlePosition());
        assertEquals(0, adapter.getRealPosition(10));
    }

    @Test
    public void finiteModeUsesRealCountAndPosition() {
        BannerRvAdapter<BannerBean> adapter = new BannerRvAdapter<>(createData(), false);

        assertEquals(3, adapter.getRealCount());
        assertEquals(3, adapter.getItemCount());
        assertEquals(1, adapter.getRealPosition(4));
    }

    @Test
    public void loopModeUsesMaxCountAndAlignedMiddlePosition() {
        BannerRvAdapter<BannerBean> adapter = new BannerRvAdapter<>(createData(), true);
        int middlePosition = adapter.getMiddlePosition();

        assertEquals(Integer.MAX_VALUE, adapter.getItemCount());
        assertEquals(0, middlePosition % adapter.getRealCount());
        assertEquals(0, adapter.getRealPosition(middlePosition));
    }

    private List<BannerBean> createData() {
        return Arrays.asList(
                new BannerBean("1", "title1", ""),
                new BannerBean("2", "title2", ""),
                new BannerBean("3", "title3", "")
        );
    }
}

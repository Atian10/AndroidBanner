package com.atian.banner.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.atian.banner.R;
import com.atian.banner.adapter.BannerRvAdapter;
import com.atian.banner.base.BaseActivity;
import com.atian.banner.bean.BannerBean;
import com.atian.banner.databinding.ActivityBannerBinding;
import com.atian.banner.interfaces.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 轮播图演示页面
 */
public class BannerActivity extends BaseActivity<ActivityBannerBinding> implements OnBannerClickListener {

    private static final String TAG = "BannerActivity";

    /** 轮播间隔（毫秒） */
    private static final long BANNER_INTERVAL_TIME = 3000L;

    private BannerRvAdapter adapter;

    private final Handler bannerHandler = new Handler(Looper.getMainLooper());

    /** 当前轮播位置 */
    private int currentPosition = 0;

    /** 是否正在自动轮播 */
    private boolean isAutoPlaying = false;

    private final ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            currentPosition = position;
            updateIndicator(position);
            Log.d(TAG, "onPageSelected：  position=" + position);
        }
    };

    private final Runnable bannerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAutoPlaying && adapter != null && adapter.getItemCount() > 0) {
                int nextPosition = (currentPosition + 1) % adapter.getItemCount();
                binding.vpBanner.setCurrentItem(nextPosition, true);
            }
            bannerHandler.postDelayed(this, BANNER_INTERVAL_TIME);
        }
    };

    @Override
    protected void initData() {
        List<BannerBean> bannerList = buildBannerData();
        adapter = new BannerRvAdapter(bannerList);
        adapter.setOnBannerClickListener(this);
    }

    @Override
    protected void initView() {
        binding.vpBanner.setAdapter(adapter);
        binding.vpBanner.registerOnPageChangeCallback(pageChangeCallback);
        initIndicator(adapter.getItemCount());
    }

    /**
     * 构造测试轮播数据
     */
    private List<BannerBean> buildBannerData() {
        List<BannerBean> list = new ArrayList<>();
        list.add(new BannerBean("", "第一条轮播", ""));
        list.add(new BannerBean("", "第二条轮播", ""));
        list.add(new BannerBean("", "第三条轮播", ""));
        list.add(new BannerBean("", "第四条轮播", ""));
        return list;
    }

    /**
     * 初始化指示器
     */
    private void initIndicator(int count) {
        LinearLayout indicatorContainer = binding.llBannerIndicator;
        indicatorContainer.removeAllViews();
        for (int i = 0; i < count; i++) {
            android.view.View indicator = new android.view.View(this);
            int size = (int) getResources().getDimension(R.dimen.banner_indicator_size);
            int margin = (int) getResources().getDimension(R.dimen.banner_indicator_margin);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(margin, 0, margin, 0);
            indicator.setLayoutParams(params);
            indicator.setBackgroundResource(i == 0
                    ? R.drawable.shape_banner_indicator_selected
                    : R.drawable.shape_banner_indicator_normal);
            indicatorContainer.addView(indicator);
        }
    }

    /**
     * 更新指示器状态
     */
    private void updateIndicator(int position) {
        LinearLayout indicatorContainer = binding.llBannerIndicator;
        int childCount = indicatorContainer.getChildCount();
        if (childCount == 0) {
            return;
        }
        int realPosition = position % childCount;
        for (int i = 0; i < childCount; i++) {
            android.view.View indicator = indicatorContainer.getChildAt(i);
            if (i == realPosition) {
                indicator.setBackgroundResource(R.drawable.shape_banner_indicator_selected);
            } else {
                indicator.setBackgroundResource(R.drawable.shape_banner_indicator_normal);
            }
        }
    }

    /**
     * 开始自动轮播
     */
    private void startAutoPlay() {
        if (!isAutoPlaying) {
            isAutoPlaying = true;
            bannerHandler.postDelayed(bannerRunnable, BANNER_INTERVAL_TIME);
            Log.d(TAG, "startAutoPlay：  开始自动轮播");
        }
    }

    /**
     * 停止自动轮播
     */
    private void stopAutoPlay() {
        if (isAutoPlaying) {
            isAutoPlaying = false;
            bannerHandler.removeCallbacks(bannerRunnable);
            Log.d(TAG, "stopAutoPlay：  停止自动轮播");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAutoPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAutoPlay();
        binding.vpBanner.unregisterOnPageChangeCallback(pageChangeCallback);
    }

    @Override
    public void onBannerClick(int position, BannerBean banner) {
        if (isFastClick()) {
            return;
        }
        Toast.makeText(this, "点击了第" + (position + 1) + "条", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onBannerClick：  position=" + position + ", banner=" + banner);
    }
}

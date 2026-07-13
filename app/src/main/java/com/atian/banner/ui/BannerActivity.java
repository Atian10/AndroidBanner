package com.atian.banner.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.atian.banner.R;
import com.atian.banner.adapter.BannerRvAdapter;
import com.atian.banner.base.BaseActivity;
import com.atian.banner.bean.BannerBean;
import com.atian.banner.config.BannerConfig;
import com.atian.banner.databinding.ActivityBannerBinding;
import com.atian.banner.enums.AnimType;
import com.atian.banner.enums.IndicatorType;
import com.atian.banner.interfaces.OnBannerClickListener;
import com.atian.banner.transformer.DepthPageTransformer;
import com.atian.banner.transformer.FadePageTransformer;
import com.atian.banner.transformer.FlipPageTransformer;
import com.atian.banner.transformer.ScalePageTransformer;
import com.atian.banner.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 轮播图演示页面
 */
public class BannerActivity extends BaseActivity<ActivityBannerBinding> implements OnBannerClickListener {

    private static final String TAG = "BannerActivity";

    private BannerConfig config;

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
            LogUtils.d(TAG, "onPageSelected：  position=" + position);
        }
    };

    private final Runnable bannerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAutoPlaying && adapter != null && config != null && adapter.getRealCount() > 0) {
                int nextPosition;
                if (config.isLoop()) {
                    // P01 修复：接近 MAX_VALUE 时重置到 middlePosition，防止 +1 溢出
                    if (currentPosition >= Integer.MAX_VALUE - 1) {
                        int middle = adapter.getMiddlePosition();
                        binding.vpBanner.setCurrentItem(middle, false);
                        currentPosition = middle;
                    }
                    nextPosition = currentPosition + 1;
                } else {
                    nextPosition = (currentPosition + 1) % adapter.getRealCount();
                }
                binding.vpBanner.setCurrentItem(nextPosition, true);
                // P04 修复：postDelayed 移入 isAutoPlaying 判断内，停止时不自调度
                bannerHandler.postDelayed(this, config.getInterval());
            }
        }
    };

    @Override
    protected void initData() {
        config = new BannerConfig.Builder().build();
        List<BannerBean> bannerList = buildBannerData();
        adapter = new BannerRvAdapter(bannerList, config.isLoop());
        adapter.setOnBannerClickListener(this);
    }

    @Override
    protected void initView() {
        binding.vpBanner.setAdapter(adapter);
        binding.vpBanner.registerOnPageChangeCallback(pageChangeCallback);
        // 根据动画类型应用 PageTransformer
        applyAnimation();
        // 先初始化指示器（基于真实条数，避免 loop=true 时创建 10 亿圆点）
        initIndicator(adapter.getRealCount());
        // 后定位到 middlePosition，实现真无限循环（P03 调用顺序）
        if (config.isLoop() && adapter.getRealCount() > 0) {
            currentPosition = adapter.getMiddlePosition();
            binding.vpBanner.setCurrentItem(currentPosition, false);
        }
    }

    /**
     * 根据动画类型应用 PageTransformer
     */
    private void applyAnimation() {
        if (config == null) {
            return;
        }
        ViewPager2.PageTransformer transformer;
        switch (config.getAnimType()) {
            case SCALE:
                transformer = new ScalePageTransformer();
                break;
            case FLIP:
                transformer = new FlipPageTransformer();
                break;
            case FADE:
                transformer = new FadePageTransformer();
                break;
            case DEPTH:
                transformer = new DepthPageTransformer();
                break;
            case NONE:
            default:
                transformer = null;
                break;
        }
        binding.vpBanner.setPageTransformer(transformer);
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
     * 初始化指示器（分发器）
     *
     * @param count 真实数据条数
     */
    private void initIndicator(int count) {
        if (config == null) {
            return;
        }
        switch (config.getIndicatorType()) {
            case NUMBER:
                initNumberIndicator(count);
                break;
            case DOT:
            default:
                initDotIndicator(count);
                break;
        }
    }

    /**
     * 初始化圆点指示器
     */
    private void initDotIndicator(int count) {
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
     * 初始化数字指示器
     */
    private void initNumberIndicator(int count) {
        LinearLayout indicatorContainer = binding.llBannerIndicator;
        indicatorContainer.removeAllViews();
        if (count <= 0) {
            return;
        }
        TextView tvIndicator = new TextView(this);
        tvIndicator.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.text_size_indicator));
        tvIndicator.setTextColor(ContextCompat.getColor(this, R.color.banner_indicator_text));
        tvIndicator.setText(String.format(Locale.US, getString(R.string.msg_banner_indicator_format), 1, count));
        indicatorContainer.addView(tvIndicator);
    }

    /**
     * 更新指示器状态（分发器）
     *
     * @param position ViewPager2 当前位置
     */
    private void updateIndicator(int position) {
        if (config == null) {
            return;
        }
        switch (config.getIndicatorType()) {
            case NUMBER:
                updateNumberIndicator(position);
                break;
            case DOT:
            default:
                updateDotIndicator(position);
                break;
        }
    }

    /**
     * 更新圆点指示器
     */
    private void updateDotIndicator(int position) {
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
     * 更新数字指示器
     */
    private void updateNumberIndicator(int position) {
        // P02 修复：adapter null 检查
        if (adapter == null) {
            return;
        }
        LinearLayout indicatorContainer = binding.llBannerIndicator;
        int childCount = indicatorContainer.getChildCount();
        if (childCount == 0) {
            return;
        }
        int realCount = adapter.getRealCount();
        if (realCount == 0) {
            return;
        }
        int realPosition = position % realCount;
        TextView tvIndicator = (TextView) indicatorContainer.getChildAt(0);
        tvIndicator.setText(String.format(Locale.US, getString(R.string.msg_banner_indicator_format),
                realPosition + 1, realCount));
    }

    /**
     * 开始自动轮播
     */
    private void startAutoPlay() {
        if (!isAutoPlaying) {
            isAutoPlaying = true;
            bannerHandler.postDelayed(bannerRunnable, config.getInterval());
            LogUtils.d(TAG, "startAutoPlay：  开始自动轮播");
        }
    }

    /**
     * 停止自动轮播
     */
    private void stopAutoPlay() {
        if (isAutoPlaying) {
            isAutoPlaying = false;
            bannerHandler.removeCallbacks(bannerRunnable);
            LogUtils.d(TAG, "stopAutoPlay：  停止自动轮播");
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
        Toast.makeText(this,
                String.format(Locale.US, getString(R.string.msg_banner_click_format), position + 1),
                Toast.LENGTH_SHORT).show();
        LogUtils.i(TAG, "onBannerClick：  position=" + position + ", banner=" + banner);
    }
}

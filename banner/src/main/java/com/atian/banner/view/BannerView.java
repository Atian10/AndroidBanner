package com.atian.banner.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager2.widget.ViewPager2;

import com.atian.banner.adapter.BannerRvAdapter;
import com.atian.banner.config.BannerConfig;
import com.atian.banner.lib.R;
import com.atian.banner.lib.databinding.BannerViewLayoutBinding;
import com.atian.banner.enums.AnimType;
import com.atian.banner.enums.IndicatorType;
import com.atian.banner.interfaces.IBannerData;
import com.atian.banner.interfaces.IImageLoader;
import com.atian.banner.interfaces.OnBannerClickListener;
import com.atian.banner.transformer.DepthPageTransformer;
import com.atian.banner.transformer.FadePageTransformer;
import com.atian.banner.transformer.FlipPageTransformer;
import com.atian.banner.transformer.ScalePageTransformer;
import com.atian.banner.util.LogUtils;
import com.atian.banner.viewholder.BannerViewHolderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Banner 轮播统一入口 View
 * <p>封装 ViewPager2 + 指示器 + 自动轮播 + 动画系统，
 * 通过 {@link DefaultLifecycleObserver} 自动感知宿主生命周期，
 * 宿主只需在 onCreate 后调用 {@link #start(LifecycleOwner)} 即可</p>
 * <p>使用示例：
 * <pre>
 * BannerView bannerView = findViewById(R.id.banner_view);
 * bannerView.setConfig(new BannerConfig.Builder().build())
 *           .setData(list)
 *           .setOnBannerClickListener(listener)
 *           .start(this);
 * </pre>
 * </p>
 */
public class BannerView extends FrameLayout implements DefaultLifecycleObserver {

    private static final String TAG = "BannerView";

    private BannerViewLayoutBinding binding;

    private BannerConfig config;

    private BannerRvAdapter<IBannerData> adapter;

    /** ViewHolder 工厂（自定义布局时设置，须在 setData 前调用） */
    private BannerViewHolderFactory<IBannerData> viewHolderFactory;

    private final Handler bannerHandler = new Handler(Looper.getMainLooper());

    /** 当前轮播位置 */
    private int currentPosition = 0;

    /** 是否正在自动轮播 */
    private boolean isAutoPlaying = false;

    /** 是否已执行过 start（避免重复注册生命周期） */
    private boolean isStarted = false;

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
                        binding.bannerViewPager.setCurrentItem(middle, false);
                        currentPosition = middle;
                    }
                    nextPosition = currentPosition + 1;
                } else {
                    nextPosition = (currentPosition + 1) % adapter.getRealCount();
                }
                binding.bannerViewPager.setCurrentItem(nextPosition, true);
                // P04 修复：postDelayed 移入 isAutoPlaying 判断内，停止时不自调度
                bannerHandler.postDelayed(this, config.getInterval());
            }
        }
    };

    public BannerView(@NonNull Context context) {
        super(context);
        initView();
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化内部视图
     */
    private void initView() {
        binding = BannerViewLayoutBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    /**
     * 设置 Banner 配置（必须在 {@link #setData(List)} 之前调用）
     *
     * @param config Banner 配置，为 null 时使用默认配置
     * @return 当前 BannerView，支持链式调用
     */
    public BannerView setConfig(@Nullable BannerConfig config) {
        this.config = config == null ? new BannerConfig.Builder().build() : config;
        return this;
    }

    /**
     * 设置自定义 ViewHolder 工厂
     * <p>用于自定义 item 布局，须在 {@link #setData(List)} 之前调用，
     * 未调用时使用 {@link com.atian.banner.viewholder.DefaultBannerViewHolderFactory}</p>
     *
     * @param factory ViewHolder 工厂
     * @return 当前 BannerView，支持链式调用
     */
    public BannerView setViewHolderFactory(@Nullable BannerViewHolderFactory<IBannerData> factory) {
        this.viewHolderFactory = factory;
        return this;
    }

    /**
     * 设置轮播数据
     * <p>支持任何实现 {@link IBannerData} 的数据模型，
     * 内部会拷贝为 {@code List<IBannerData>} 以保证类型安全</p>
     *
     * @param list 轮播数据列表，null 会被兜底为空集合
     * @return 当前 BannerView，支持链式调用
     */
    public BannerView setData(@Nullable List<? extends IBannerData> list) {
        if (config == null) {
            config = new BannerConfig.Builder().build();
        }
        // 拷贝为 List<IBannerData>，避免泛型协变问题
        List<IBannerData> dataList = list == null ? null : new ArrayList<>(list);
        adapter = new BannerRvAdapter<>(dataList, config.isLoop(), viewHolderFactory);
        return this;
    }

    /**
     * 设置 Banner 点击监听
     *
     * @param listener 点击监听器，泛型类型为 {@link IBannerData}
     * @return 当前 BannerView，支持链式调用
     */
    public BannerView setOnBannerClickListener(@Nullable OnBannerClickListener<IBannerData> listener) {
        if (adapter != null) {
            adapter.setOnBannerClickListener(listener);
        }
        return this;
    }

    /**
     * 设置图片加载器
     * <p>不设置时图片不加载（优雅降级），
     * 推荐使用 {@link com.atian.banner.imageloader.GlideImageLoader} 或自定义实现</p>
     *
     * @param imageLoader 图片加载器
     * @return 当前 BannerView，支持链式调用
     */
    public BannerView setImageLoader(@Nullable IImageLoader imageLoader) {
        if (adapter != null) {
            adapter.setImageLoader(imageLoader);
        }
        return this;
    }

    /**
     * 启动 Banner（注册生命周期感知）
     * <p>宿主 Activity/Fragment 传入自身（实现 {@link LifecycleOwner}），
     * BannerView 会自动在 onResume 启动轮播、onPause 停止轮播、onDestroy 释放资源</p>
     *
     * @param lifecycleOwner 宿主生命周期所有者
     */
    public void start(@NonNull LifecycleOwner lifecycleOwner) {
        if (isStarted) {
            LogUtils.w(TAG, "start：  BannerView 已启动，请勿重复调用");
            return;
        }
        isStarted = true;
        // 注册生命周期观察者（宿主销毁时会自动移除）
        lifecycleOwner.getLifecycle().addObserver(this);
        // 初始化 ViewPager
        binding.bannerViewPager.setAdapter(adapter);
        binding.bannerViewPager.registerOnPageChangeCallback(pageChangeCallback);
        // 根据动画类型应用 PageTransformer
        applyAnimation();
        // 先初始化指示器（基于真实条数，避免 loop=true 时创建 10 亿圆点）
        initIndicator(adapter == null ? 0 : adapter.getRealCount());
        // 后定位到 middlePosition，实现真无限循环（P03 调用顺序）
        if (config != null && config.isLoop() && adapter != null && adapter.getRealCount() > 0) {
            currentPosition = adapter.getMiddlePosition();
            binding.bannerViewPager.setCurrentItem(currentPosition, false);
        }
        LogUtils.i(TAG, "start：  Banner 启动完成");
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
        binding.bannerViewPager.setPageTransformer(transformer);
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
        LinearLayout indicatorContainer = binding.bannerIndicatorContainer;
        indicatorContainer.removeAllViews();
        for (int i = 0; i < count; i++) {
            View indicator = new View(getContext());
            int size = (int) getResources().getDimension(R.dimen.banner_indicator_size);
            int margin = (int) getResources().getDimension(R.dimen.banner_indicator_margin);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(margin, 0, margin, 0);
            indicator.setLayoutParams(params);
            indicator.setBackgroundResource(i == 0
                    ? R.drawable.banner_indicator_selected
                    : R.drawable.banner_indicator_normal);
            indicatorContainer.addView(indicator);
        }
    }

    /**
     * 初始化数字指示器
     */
    private void initNumberIndicator(int count) {
        LinearLayout indicatorContainer = binding.bannerIndicatorContainer;
        indicatorContainer.removeAllViews();
        if (count <= 0) {
            return;
        }
        TextView tvIndicator = new TextView(getContext());
        tvIndicator.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.banner_text_size_indicator));
        tvIndicator.setTextColor(ContextCompat.getColor(getContext(), R.color.banner_indicator_text));
        tvIndicator.setText(String.format(Locale.US,
                getContext().getString(R.string.banner_msg_indicator_format), 1, count));
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
        LinearLayout indicatorContainer = binding.bannerIndicatorContainer;
        int childCount = indicatorContainer.getChildCount();
        if (childCount == 0) {
            return;
        }
        int realPosition = position % childCount;
        for (int i = 0; i < childCount; i++) {
            View indicator = indicatorContainer.getChildAt(i);
            if (i == realPosition) {
                indicator.setBackgroundResource(R.drawable.banner_indicator_selected);
            } else {
                indicator.setBackgroundResource(R.drawable.banner_indicator_normal);
            }
        }
    }

    /**
     * 更新数字指示器
     */
    private void updateNumberIndicator(int position) {
        if (adapter == null) {
            return;
        }
        LinearLayout indicatorContainer = binding.bannerIndicatorContainer;
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
        tvIndicator.setText(String.format(Locale.US,
                getContext().getString(R.string.banner_msg_indicator_format), realPosition + 1, realCount));
    }

    /**
     * 开始自动轮播
     */
    private void startAutoPlay() {
        if (!isAutoPlaying && config != null) {
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

    // ============ DefaultLifecycleObserver 实现 ============

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        startAutoPlay();
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        stopAutoPlay();
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        stopAutoPlay();
        if (binding != null) {
            binding.bannerViewPager.unregisterOnPageChangeCallback(pageChangeCallback);
        }
        // 移除生命周期观察，避免内存泄漏
        owner.getLifecycle().removeObserver(this);
        LogUtils.i(TAG, "onDestroy：  Banner 资源已释放");
    }
}

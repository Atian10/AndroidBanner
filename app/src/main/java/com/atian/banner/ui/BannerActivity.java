package com.atian.banner.ui;

import android.graphics.Color;
import android.widget.Toast;

import com.atian.banner.base.BaseActivity;
import com.atian.banner.bean.BannerBean;
import com.atian.banner.config.BannerConfig;
import com.atian.banner.databinding.ActivityBannerBinding;
import com.atian.banner.enums.AnimType;
import com.atian.banner.enums.CardStyle;
import com.atian.banner.enums.IndicatorType;
import com.atian.banner.imageloader.GlideImageLoader;
import com.atian.banner.interfaces.IBannerData;
import com.atian.banner.interfaces.OnBannerClickListener;
import com.atian.banner.lib.R;
import com.atian.banner.util.LogUtils;
import com.atian.banner.view.BannerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 轮播图演示页面
 * <p>提供可切换配置的控制面板，展示 Banner 库全部功能：
 * <ul>
 *   <li>指示器类型：DOT / NUMBER</li>
 *   <li>卡片样式：NORMAL / CARD</li>
 *   <li>切换动画：NONE / SCALE / FLIP / FADE / DEPTH</li>
 *   <li>循环模式：开 / 关</li>
 *   <li>标题设置：显示/隐藏、背景色切换</li>
 * </ul>
 * </p>
 */
public class BannerActivity extends BaseActivity<ActivityBannerBinding> implements OnBannerClickListener<IBannerData> {

    private static final String TAG = "BannerActivity";

    /** Demo 使用的真实图片 URL（picsum.photos 提供占位图） */
    private static final String[] IMAGE_URLS = {
            "https://picsum.photos/id/1015/600/400",
            "https://picsum.photos/id/1016/600/400",
            "https://picsum.photos/id/1018/600/400",
            "https://picsum.photos/id/1019/600/400"
    };

    /** Demo 使用的轮播标题 */
    private static final String[] TITLES = {
            "山谷河流",
            "山路蜿蜒",
            "山峦起伏",
            "高山湖泊"
    };

    /** 当前指示器类型 */
    private IndicatorType currentIndicatorType = IndicatorType.DOT;

    /** 当前指示器是否显示 */
    private boolean currentIndicatorVisible = true;

    /** 当前卡片样式 */
    private CardStyle currentCardStyle = CardStyle.NORMAL;

    /** 当前动画类型 */
    private AnimType currentAnimType = AnimType.SCALE;

    /** 当前循环模式 */
    private boolean currentLoop = true;

    /** 当前标题是否显示 */
    private boolean currentTitleVisible = true;

    /** 当前标题背景色 */
    private int currentTitleBgColor = Color.parseColor("#80000000");

    @Override
    protected void initData() {
        // 首次启动：加载默认配置
        reloadBanner();
    }

    @Override
    protected void initView() {
        // 指示器类型切换
        binding.btnIndicatorDot.setOnClickListener(v -> {
            currentIndicatorType = IndicatorType.DOT;
            updateConfigDisplay();
            switchBannerConfig();
        });
        binding.btnIndicatorNumber.setOnClickListener(v -> {
            currentIndicatorType = IndicatorType.NUMBER;
            updateConfigDisplay();
            switchBannerConfig();
        });
        // 指示器开关切换
        binding.btnIndicatorShow.setOnClickListener(v -> {
            currentIndicatorVisible = true;
            switchBannerConfig();
        });
        binding.btnIndicatorHide.setOnClickListener(v -> {
            currentIndicatorVisible = false;
            switchBannerConfig();
        });
        // 卡片样式切换
        binding.btnStyleNormal.setOnClickListener(v -> {
            currentCardStyle = CardStyle.NORMAL;
            updateConfigDisplay();
            switchBannerConfig();
        });
        binding.btnStyleCard.setOnClickListener(v -> {
            currentCardStyle = CardStyle.CARD;
            updateConfigDisplay();
            switchBannerConfig();
        });
        // 动画类型切换
        binding.btnAnimNone.setOnClickListener(v -> {
            currentAnimType = AnimType.NONE;
            updateConfigDisplay();
            switchBannerConfig();
        });
        binding.btnAnimScale.setOnClickListener(v -> {
            currentAnimType = AnimType.SCALE;
            updateConfigDisplay();
            switchBannerConfig();
        });
        binding.btnAnimFlip.setOnClickListener(v -> {
            currentAnimType = AnimType.FLIP;
            updateConfigDisplay();
            switchBannerConfig();
        });
        binding.btnAnimFade.setOnClickListener(v -> {
            currentAnimType = AnimType.FADE;
            updateConfigDisplay();
            switchBannerConfig();
        });
        binding.btnAnimDepth.setOnClickListener(v -> {
            currentAnimType = AnimType.DEPTH;
            updateConfigDisplay();
            switchBannerConfig();
        });
        // 循环模式切换
        binding.btnLoopOn.setOnClickListener(v -> {
            currentLoop = true;
            updateConfigDisplay();
            switchBannerConfig();
        });
        binding.btnLoopOff.setOnClickListener(v -> {
            currentLoop = false;
            updateConfigDisplay();
            switchBannerConfig();
        });
        // 标题显隐切换
        binding.btnTitleShow.setOnClickListener(v -> {
            currentTitleVisible = true;
            switchBannerConfig();
        });
        binding.btnTitleHide.setOnClickListener(v -> {
            currentTitleVisible = false;
            switchBannerConfig();
        });
        // 标题背景色切换
        binding.btnTitleBgRed.setOnClickListener(v -> {
            currentTitleBgColor = Color.parseColor("#80FF0000");
            switchBannerConfig();
        });
        binding.btnTitleBgBlue.setOnClickListener(v -> {
            currentTitleBgColor = Color.parseColor("#800000FF");
            switchBannerConfig();
        });
        binding.btnTitleBgTransparent.setOnClickListener(v -> {
            currentTitleBgColor = Color.TRANSPARENT;
            switchBannerConfig();
        });
        binding.btnTitleBgDefault.setOnClickListener(v -> {
            currentTitleBgColor = Color.parseColor("#80000000");
            switchBannerConfig();
        });
        // 重新加载
        binding.btnReload.setOnClickListener(v -> {
            updateConfigDisplay();
            reloadBanner();
            Toast.makeText(this, "已重新加载", Toast.LENGTH_SHORT).show();
        });
        // 显示当前配置
        updateConfigDisplay();
    }

    /**
     * 根据当前配置重新加载 Banner（从第一张重新开始）
     */
    private void reloadBanner() {
        BannerConfig config = new BannerConfig.Builder()
                .interval(3000L)
                .loop(currentLoop)
                .indicatorType(currentIndicatorType)
                .indicatorVisible(currentIndicatorVisible)
                .cardStyle(currentCardStyle)
                .animType(currentAnimType)
                .titleVisible(currentTitleVisible)
                .titleBgColor(currentTitleBgColor)
                .build();
        List<BannerBean> bannerList = buildBannerData();
        binding.bannerView.setConfig(config)
                .setData(bannerList)
                .setImageLoader(new GlideImageLoader())
                .setOnBannerClickListener(this)
                .restart(this);
    }

    /**
     * 根据当前配置切换 Banner（保持当前图片位置）
     * <p>用于动画/指示器/卡片样式/循环模式/标题样式等配置切换，
     * 避免切换后重置到第一张。</p>
     */
    private void switchBannerConfig() {
        BannerConfig config = new BannerConfig.Builder()
                .interval(3000L)
                .loop(currentLoop)
                .indicatorType(currentIndicatorType)
                .indicatorVisible(currentIndicatorVisible)
                .cardStyle(currentCardStyle)
                .animType(currentAnimType)
                .titleVisible(currentTitleVisible)
                .titleBgColor(currentTitleBgColor)
                .build();
        List<BannerBean> bannerList = buildBannerData();
        binding.bannerView.setConfig(config)
                .setData(bannerList)
                .setImageLoader(new GlideImageLoader())
                .setOnBannerClickListener(this)
                .restartKeepPosition(this);
    }

    /**
     * 更新当前配置显示
     */
    private void updateConfigDisplay() {
        String configText = String.format(Locale.US,
                getString(R.string.banner_msg_config_format),
                currentIndicatorType.name(),
                currentCardStyle.name(),
                currentAnimType.name(),
                currentLoop ? "开" : "关");
        binding.tvConfig.setText(configText);
        LogUtils.i(TAG, "updateConfigDisplay：  " + configText);
    }

    /**
     * 构造测试轮播数据（使用真实图片 URL）
     */
    private List<BannerBean> buildBannerData() {
        List<BannerBean> list = new ArrayList<>();
        int count = Math.min(IMAGE_URLS.length, TITLES.length);
        for (int i = 0; i < count; i++) {
            list.add(new BannerBean(IMAGE_URLS[i], TITLES[i], ""));
        }
        return list;
    }

    @Override
    public void onBannerClick(int position, IBannerData banner) {
        if (isFastClick()) {
            return;
        }
        Toast.makeText(this,
                String.format(Locale.US, getString(R.string.banner_msg_click_format), position + 1),
                Toast.LENGTH_SHORT).show();
        LogUtils.i(TAG, "onBannerClick：  position=" + position + ", banner=" + banner);
    }
}

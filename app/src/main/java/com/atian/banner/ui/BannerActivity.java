package com.atian.banner.ui;

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
 * <p>使用 {@link BannerView} 统一入口，宿主仅需配置数据并调用 start</p>
 */
public class BannerActivity extends BaseActivity<ActivityBannerBinding> implements OnBannerClickListener<IBannerData> {

    private static final String TAG = "BannerActivity";

    @Override
    protected void initData() {
        // 构建 Banner 配置（可自定义轮播间隔、循环模式、指示器类型、卡片样式、动画类型）
        BannerConfig config = new BannerConfig.Builder()
                .interval(3000L)
                .loop(true)
                .indicatorType(IndicatorType.DOT)
                .cardStyle(CardStyle.NORMAL)
                .animType(AnimType.SCALE)
                .build();
        // 构造测试轮播数据
        List<BannerBean> bannerList = buildBannerData();
        // 链式调用：配置 → 数据 → 图片加载器 → 点击监听 → 启动（传入 LifecycleOwner 自动感知生命周期）
        binding.bannerView.setConfig(config)
                .setData(bannerList)
                .setImageLoader(new GlideImageLoader())
                .setOnBannerClickListener(this)
                .start(this);
    }

    @Override
    protected void initView() {
        // BannerView 已在 initData 中完成初始化，此处无需额外操作
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

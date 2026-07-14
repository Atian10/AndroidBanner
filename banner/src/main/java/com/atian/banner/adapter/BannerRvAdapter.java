package com.atian.banner.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atian.banner.interfaces.IBannerData;
import com.atian.banner.interfaces.IImageLoader;
import com.atian.banner.interfaces.OnBannerClickListener;
import com.atian.banner.viewholder.BannerViewHolder;
import com.atian.banner.viewholder.BannerViewHolderFactory;
import com.atian.banner.viewholder.DefaultBannerViewHolderFactory;

import java.util.Collections;
import java.util.List;

/**
 * 轮播图列表适配器，用于 ViewPager2
 * <p>支持无限循环模式：loop=true 时 getItemCount 返回 Integer.MAX_VALUE，
 * 启动时定位到 middlePosition 可实现双向无限滑动</p>
 * <p>泛型参数 T 需实现 {@link IBannerData}，支持用户自定义数据模型</p>
 * <p>通过 {@link BannerViewHolderFactory} 支持自定义 item 布局</p>
 *
 * @param <T> Banner 数据类型
 */
public class BannerRvAdapter<T extends IBannerData> extends RecyclerView.Adapter<BannerViewHolder<T>> {

    private final List<T> list;

    /** 是否无限循环 */
    private final boolean loop;

    /** ViewHolder 工厂 */
    private final BannerViewHolderFactory<T> factory;

    /** 图片加载器 */
    private IImageLoader imageLoader;

    /** 点击监听器 */
    private OnBannerClickListener<T> listener;

    /**
     * 构造适配器（使用默认 ViewHolder 工厂）
     *
     * @param list 数据列表
     * @param loop 是否无限循环
     */
    public BannerRvAdapter(List<T> list, boolean loop) {
        this(list, loop, null);
    }

    /**
     * 构造适配器（指定 ViewHolder 工厂）
     *
     * @param list    数据列表
     * @param loop    是否无限循环
     * @param factory ViewHolder 工厂，为 null 时使用默认工厂
     */
    @SuppressWarnings("unchecked")
    public BannerRvAdapter(List<T> list, boolean loop, BannerViewHolderFactory<T> factory) {
        // P07 修复：list null 兜底，转为不可变空集合
        this.list = list == null ? Collections.emptyList() : list;
        this.loop = loop;
        // factory 为 null 时使用默认工厂（类型安全：DefaultBannerViewHolderFactory 实现 BannerViewHolderFactory<IBannerData>）
        this.factory = factory != null ? factory : (BannerViewHolderFactory<T>) new DefaultBannerViewHolderFactory();
    }

    /**
     * 设置图片加载器
     *
     * @param imageLoader 图片加载器，为 null 时不加载图片
     */
    public void setImageLoader(IImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    /**
     * 设置点击监听器
     *
     * @param listener 点击监听器
     */
    public void setOnBannerClickListener(OnBannerClickListener<T> listener) {
        this.listener = listener;
    }

    /**
     * 是否无限循环
     */
    public boolean isLoop() {
        return loop;
    }

    /**
     * 获取真实数据条数
     */
    public int getRealCount() {
        return list == null ? 0 : list.size();
    }

    /**
     * 将无限循环位置转换为真实数据位置
     *
     * @param position ViewPager2 当前位置
     * @return 真实数据索引
     */
    public int getRealPosition(int position) {
        int realCount = getRealCount();
        if (realCount == 0) {
            return 0;
        }
        return position % realCount;
    }

    /**
     * 获取无限循环的中间起始位置
     * <p>以 Integer.MAX_VALUE / 2 为基准，对齐到 realCount 的整数倍，
     * 保证起始位置展示第 0 项，且可双向滑动约 10 亿次</p>
     *
     * @return 中间起始位置，realCount=0 时返回 0
     */
    public int getMiddlePosition() {
        int realCount = getRealCount();
        if (realCount == 0) {
            return 0;
        }
        int half = Integer.MAX_VALUE / 2;
        return half - (half % realCount);
    }

    @NonNull
    @Override
    public BannerViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return factory.createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder<T> holder, int position) {
        int realPosition = getRealPosition(position);
        T data = list.get(realPosition);
        // 委托给 ViewHolder 进行数据绑定
        holder.bind(data, realPosition, imageLoader);
        // 点击事件由 Adapter 统一处理
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBannerClick(realPosition, data);
            }
        });
    }

    @Override
    public int getItemCount() {
        int realCount = getRealCount();
        if (realCount == 0) {
            return 0;
        }
        return loop ? Integer.MAX_VALUE : realCount;
    }
}

package com.atian.banner.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atian.banner.bean.BannerBean;
import com.atian.banner.databinding.RvItemBannerBinding;
import com.atian.banner.interfaces.OnBannerClickListener;

import java.util.Collections;
import java.util.List;

/**
 * 轮播图列表适配器，用于 ViewPager2
 * <p>支持无限循环模式：loop=true 时 getItemCount 返回 Integer.MAX_VALUE，
 * 启动时定位到 middlePosition 可实现双向无限滑动</p>
 */
public class BannerRvAdapter extends RecyclerView.Adapter<BannerRvAdapter.ViewHolder> {

    private final List<BannerBean> list;

    /** 是否无限循环 */
    private final boolean loop;

    private OnBannerClickListener listener;

    public BannerRvAdapter(List<BannerBean> list, boolean loop) {
        // P07 修复：list null 兜底，转为不可变空集合
        this.list = list == null ? Collections.emptyList() : list;
        this.loop = loop;
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RvItemBannerBinding binding = RvItemBannerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int realPosition = getRealPosition(position);
        holder.bind(list.get(realPosition), realPosition);
    }

    @Override
    public int getItemCount() {
        int realCount = getRealCount();
        if (realCount == 0) {
            return 0;
        }
        return loop ? Integer.MAX_VALUE : realCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final RvItemBannerBinding binding;

        ViewHolder(RvItemBannerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BannerBean banner, int position) {
            binding.tvBannerTitle.setText(banner.getTitle());
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBannerClick(position, banner);
                }
            });
        }
    }
}

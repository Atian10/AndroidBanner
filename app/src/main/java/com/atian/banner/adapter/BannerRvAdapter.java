package com.atian.banner.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atian.banner.bean.BannerBean;
import com.atian.banner.databinding.RvItemBannerBinding;
import com.atian.banner.interfaces.OnBannerClickListener;

import java.util.List;

/**
 * 轮播图列表适配器，用于 ViewPager2
 */
public class BannerRvAdapter extends RecyclerView.Adapter<BannerRvAdapter.ViewHolder> {

    private final List<BannerBean> list;

    private OnBannerClickListener listener;

    public BannerRvAdapter(List<BannerBean> list) {
        this.list = list;
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.listener = listener;
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
        holder.bind(list.get(position), position);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
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

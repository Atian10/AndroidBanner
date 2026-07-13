package com.atian.banner.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.atian.banner.util.LogUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Activity 基类，封装 ViewBinding 绑定与生命周期日志
 *
     * @param <VB> ViewBinding 类型
 */
public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    /** 快速点击间隔（毫秒） */
    private static final long MIN_CLICK_DELAY_TIME = 500L;

    protected VB binding;

    private long lastClickTime = 0L;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, getClass().getSimpleName() + " onCreate：  开始");
        binding = createBinding();
        setContentView(binding.getRoot());
        initData();
        initView();
        LogUtils.d(TAG, getClass().getSimpleName() + " onCreate：  结束");
    }

    /**
     * 通过反射创建 ViewBinding 实例
     */
    @SuppressWarnings("unchecked")
    private VB createBinding() {
        try {
            Type type = getClass().getGenericSuperclass();
            Class<VB> bindingClass = (Class<VB>) ((ParameterizedType) type).getActualTypeArguments()[0];
            Method inflateMethod = bindingClass.getMethod("inflate", LayoutInflater.class);
            return (VB) inflateMethod.invoke(null, getLayoutInflater());
        } catch (Exception e) {
            LogUtils.e(TAG, "createBinding 反射失败：  " + e.getMessage(), e);
            throw new RuntimeException("ViewBinding 创建失败", e);
        }
    }

    /**
     * 判断是否为快速点击
     */
    protected boolean isFastClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < MIN_CLICK_DELAY_TIME) {
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, getClass().getSimpleName() + " onDestroy：  销毁");
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化视图
     */
    protected abstract void initView();
}

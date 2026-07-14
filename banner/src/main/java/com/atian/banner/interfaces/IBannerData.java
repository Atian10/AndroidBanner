package com.atian.banner.interfaces;

/**
 * Banner 数据模型接口
 * <p>用户自定义数据模型时需实现此接口，Banner 库通过此接口统一访问图片地址、标题和跳转链接</p>
 * <p>示例：
 * <pre>
 * public class MyBannerData implements IBannerData {
 *     private String url;
 *     private String title;
 *
 *     &#64;Override
 *     public String getImageUrl() { return url; }
 *
 *     &#64;Override
 *     public String getTitle() { return title; }
 *
 *     &#64;Override
 *     public String getLinkUrl() { return null; }
 * }
 * </pre>
 * </p>
 */
public interface IBannerData {

    /**
     * 获取图片地址（网络或本地）
     *
     * @return 图片地址
     */
    String getImageUrl();

    /**
     * 获取标题
     *
     * @return 标题
     */
    String getTitle();

    /**
     * 获取点击跳转链接
     *
     * @return 跳转链接，无跳转返回 null
     */
    String getLinkUrl();
}

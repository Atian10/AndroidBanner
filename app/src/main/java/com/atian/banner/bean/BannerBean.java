package com.atian.banner.bean;

/**
 * 轮播图数据模型
 */
public class BannerBean {

    /** 图片地址（网络或本地） */
    private String imageUrl;

    /** 标题 */
    private String title;

    /** 点击跳转链接 */
    private String linkUrl;

    public BannerBean(String imageUrl, String title, String linkUrl) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.linkUrl = linkUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    @Override
    public String toString() {
        return "BannerBean{"
                + "imageUrl='" + imageUrl + '\''
                + ", title='" + title + '\''
                + ", linkUrl='" + linkUrl + '\''
                + '}';
    }
}

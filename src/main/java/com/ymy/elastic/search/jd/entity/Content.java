package com.ymy.elastic.search.jd.entity;

/**
 * 封装商品
 * 标题、图片地址、价格
 */
public class Content {
    /**
     * 商品的标题
     */
    private String title;

    /**
     * 商品图片地址
     */
    private String img;

    /**
     * 商品的价格
     */
    private String price;

    public Content() {

    }

    public Content(String title, String img, String price) {
        this.title = title;
        this.img = img;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Content{" +
                "title='" + title + '\'' +
                ", img='" + img + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}

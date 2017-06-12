package com.paymentwall.pandapptest.pojo;

/**
 * Created by Hieu.Hoa.Luong on 10/12/2014.
 */
public class Goods {
    String id;
    String name;
    Double price;
    String currency;
    int image;

    public Goods(String id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Goods(String id, int image, String name, Double price, String currency) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.price = price;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public Goods() {
    }
}

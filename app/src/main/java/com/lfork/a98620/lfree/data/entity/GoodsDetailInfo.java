package com.lfork.a98620.lfree.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by 98620 on 2018/4/13.
 */

public class GoodsDetailInfo extends Goods {

    @SerializedName("desc")
    private String description;

    private String username;


    private String[] images;

    private ArrayList<Review> reviews;

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }
}

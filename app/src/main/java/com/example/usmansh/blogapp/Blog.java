package com.example.usmansh.blogapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Usman Sh on 9/13/2017.
 */

public class Blog {

    private String tiles;
    private String images;
    private String descriptions;
    private String username;
    private String userid;

    public Blog(){

    }


    public Blog(String tiles, String descriptions, String images,String username,String userid) {
        this.tiles = tiles;
        this.descriptions = descriptions;
        this.images = images;
        this.username = username;
        this.userid = userid;
    }




    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getTiles() {
        return tiles;
    }

    public void setTiles(String tiles) {
        this.tiles = tiles;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

}

package com.graduate.seoil.sg_projdct.Model;

/**
 * Created by baejanghun on 30/03/2019.
 */
public class GroupUserList {
    private String id;
    private String imageURL;
    private String level;
    private String username;
    private String registDate;

    public GroupUserList() {
    }

    public GroupUserList(String imageURL, String username) {
        this.imageURL = imageURL;
        this.username = username;
    }

    public GroupUserList(String id, String imageURL, String level, String username, String registDate) {
        this.id = id;
        this.imageURL = imageURL;
        this.level = level;
        this.username = username;
        this.registDate = registDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRegistDate() {
        return registDate;
    }

    public void setRegistDate(String registDate) {
        this.registDate = registDate;
    }
}

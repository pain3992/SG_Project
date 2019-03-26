package com.graduate.seoil.sg_projdct.Model;

import java.util.List;

/**
 * Created by baejanghun on 25/03/2019.
 */
public class Group {
    private String key;
    private String title;
    private String content;
    private String goal;
    private String imageURL;
    private String registDate;

    private int user_min_count;
    private int uesr_max_count;

//    private List<String> goal_list;
//    private List<String> group_list;

    public Group(String title, String content, String goal, String imageURL, String registDate, int user_min_count, int uesr_max_count) {
        this.title = title;
        this.content = content;
        this.goal = goal;
        this.imageURL = imageURL;
        this.user_min_count = user_min_count;
        this.uesr_max_count = uesr_max_count;
        this.registDate = registDate;
    }

    public Group() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getUser_min_count() {
        return user_min_count;
    }

    public void setUser_min_count(int user_min_count) {
        this.user_min_count = user_min_count;
    }

    public int getUesr_max_count() {
        return uesr_max_count;
    }

    public void setUesr_max_count(int uesr_max_count) {
        this.uesr_max_count = uesr_max_count;
    }

    public String getRegistDate() {
        return registDate;
    }

    public void setRegistDate(String registDate) {
        this.registDate = registDate;
    }

    @Override
    public String toString() {
        return this.title + "," + this.content;
    }
}
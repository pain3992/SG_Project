package com.graduate.seoil.sg_projdct.Model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by baejanghun on 25/03/2019.
 */
public class Group {
    private String title;
    private String content;
    private String goal;
    private String imageURL;
    private String registDate;
    private String adminName;
    private String dayCycle;


    private int planTime;

    private int current_user;
    private int User_max_count;

    private HashMap<String, Object> userList;
    private HashMap<String, Object> notifications;


    public Group() {
    }

    public Group(String title, String content, String goal, String imageURL, String registDate, String adminName, String dayCycle, int planTime, int current_user, int User_max_count, HashMap<String, Object> userList) {
        this.title = title;
        this.content = content;
        this.goal = goal;
        this.imageURL = imageURL;
        this.registDate = registDate;
        this.adminName = adminName;
        this.dayCycle = dayCycle;
        this.planTime = planTime;
        this.current_user = current_user;
        this.User_max_count = User_max_count;
        this.userList = userList;
//        this.notifications = notifications;
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

    public int getUser_max_count() {
        return User_max_count;
    }

    public void setUser_max_count(int User_max_count) {
        this.User_max_count = User_max_count;
    }

    public String getRegistDate() {
        return registDate;
    }

    public void setRegistDate(String registDate) {
        this.registDate = registDate;
    }

    public String getDayCycle() {
        return dayCycle;
    }

    public void setDayCycle(String dayCycle) {
        this.dayCycle = dayCycle;
    }

    public int getPlanTime() {
        return planTime;
    }

    public void setPlanTime(int planTime) {
        this.planTime = planTime;
    }

    public int getcurrent_user() {
        return current_user;
    }
    public void setcurrent_user(int current_user) {
        this.current_user = current_user;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public HashMap<String, Object> getUserList() {
        return userList;
    }

    public void setUserList(HashMap<String, Object> userList) {
        this.userList = userList;
    }

    public HashMap<String, Object> getNotifications() {
        return notifications;
    }

    public void setNotifications(HashMap<String, Object> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    public String toString() {
        return title + ", " + content + ", " + goal + ", " + registDate + ", " + adminName + ", " + dayCycle + ", " + userList;
    }
}

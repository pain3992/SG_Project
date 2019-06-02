package com.graduate.seoil.sg_projdct.Model;

/**
 * Created by baejanghun on 02/06/2019.
 */
public class PushNotification {
    String uid;
    String title;
    String content;
    String category;
    String sender_name;
    String sender_url;
    long timestamp;

    public PushNotification() {
    }

    public PushNotification(String uid, String title, String contentl, String category, String sender_name, long timestamp) {
        this.uid = uid;
        this.title = title;
        this.content = contentl;
        this.category = category;
        this.sender_name = sender_name;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender_url() {
        return sender_url;
    }

    public void setSender_url(String sender_url) {
        this.sender_url = sender_url;
    }
}

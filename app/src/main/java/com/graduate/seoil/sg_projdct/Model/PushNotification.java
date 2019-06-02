package com.graduate.seoil.sg_projdct.Model;

/**
 * Created by baejanghun on 02/06/2019.
 */
public class PushNotification {
    String uid;
    String title;
    String contentl;
    String category;
    String sender_name;
    String receiver_uid;
    long timestamp;

    public PushNotification() {
    }

    public PushNotification(String uid, String title, String contentl, String category, String sender_name, String receiver_uid, long timestamp) {
        this.uid = uid;
        this.title = title;
        this.contentl = contentl;
        this.category = category;
        this.sender_name = sender_name;
        this.receiver_uid = receiver_uid;
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

    public String getContentl() {
        return contentl;
    }

    public void setContentl(String contentl) {
        this.contentl = contentl;
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

    public String getReceiver_uid() {
        return receiver_uid;
    }

    public void setReceiver_uid(String receiver_uid) {
        this.receiver_uid = receiver_uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

package com.graduate.seoil.sg_projdct.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private String sender_imageUrl;
    private String sender_name;
    private String send_date;
    private boolean isseen;

    public Chat(String sender, String receiver, String message, String sender_imageUrl, String sender_name, String send_date, boolean isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.sender_imageUrl = sender_imageUrl;
        this.sender_name = sender_name;
        this.send_date = send_date;
        this.isseen = isseen;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender_imageUrl() {
        return sender_imageUrl;
    }

    public void setSender_imageUrl(String sender_imageUrl) {
        this.sender_imageUrl = sender_imageUrl;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getSend_date() {
        return send_date;
    }

    public void setSend_date(String send_date) {
        this.send_date = send_date;
    }
}

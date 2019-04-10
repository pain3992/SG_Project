package com.graduate.seoil.sg_projdct.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private String sender_imageUrl;
    private boolean isseen;

    public Chat(String sender, String receiver, String message, String sender_imageUrl, boolean isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.sender_imageUrl = sender_imageUrl;
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
}

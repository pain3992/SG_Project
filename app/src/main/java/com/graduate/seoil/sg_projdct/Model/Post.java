package com.graduate.seoil.sg_projdct.Model;

/**
 * Created by baejanghun on 03/04/2019.
 */
public class Post {
    private String postid;
    private String postimage;
    private String description;
    private String publisher;
    public String receiver;
    private long registDate;

    public Post() {
    }

    public Post(String postid, String postimage, String description, String publisher,String receiver, long registDate) {
        this.postid = postid;
        this.postimage = postimage;
        this.description = description;
        this.publisher = publisher;
        this.receiver = receiver;
        this.registDate = registDate;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public long getregistDate() {
        return registDate;
    }

    public void setregistDate(long registDate) {
        this.registDate = registDate;
    }
}

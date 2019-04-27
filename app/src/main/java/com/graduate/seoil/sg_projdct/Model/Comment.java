package com.graduate.seoil.sg_projdct.Model;

/**
 * Created by baejanghun on 13/04/2019.
 */
public class Comment {
    private String comment;
    private String publisher;
    private long registDate;

    public Comment() {
    }

    public Comment(String comment, String publisher, long registDate) {
        this.comment = comment;
        this.publisher = publisher;
        this.registDate = registDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public long getRegistDate() {
        return registDate;
    }

    public void setRegistDate(long registDate) {
        this.registDate = registDate;
    }
}

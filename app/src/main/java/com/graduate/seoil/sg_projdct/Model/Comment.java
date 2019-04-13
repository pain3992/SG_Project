package com.graduate.seoil.sg_projdct.Model;

/**
 * Created by baejanghun on 13/04/2019.
 */
public class Comment {
    private String comment;
    private String publisher;

    public Comment() {
    }

    public Comment(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
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
}

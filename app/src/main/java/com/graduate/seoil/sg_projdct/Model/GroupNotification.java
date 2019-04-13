package com.graduate.seoil.sg_projdct.Model;

/**
 * Created by baejanghun on 12/04/2019.
 */
public class GroupNotification {
    private String content;
    private String registDate;
    private String writer;

    public GroupNotification() {
    }

    public GroupNotification(String content, String registDate, String writer) {
        this.content = content;
        this.registDate = registDate;
        this.writer = writer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRegistDate() {
        return registDate;
    }

    public void setRegistDate(String registDate) {
        this.registDate = registDate;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }
}

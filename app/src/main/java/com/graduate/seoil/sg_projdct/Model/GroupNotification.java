package com.graduate.seoil.sg_projdct.Model;

/**
 * Created by baejanghun on 12/04/2019.
 */
public class GroupNotification {
    private String content;
    private long registDate;
    private String writer;
    private String writer_id;
    private String noty_id;

    public GroupNotification() {
    }

    public GroupNotification(String content, long registDate, String writer, String writer_id, String noty_id) {
        this.content = content;
        this.registDate = registDate;
        this.writer = writer;
        this.noty_id = noty_id;
        this.writer_id = writer_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getRegistDate() {
        return registDate;
    }

    public void setRegistDate(long registDate) {
        this.registDate = registDate;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getWriter_id() {
        return writer_id;
    }

    public void setWriter_id(String writer_id) {
        this.writer_id = writer_id;
    }

    public String getNoty_id() {
        return noty_id;
    }

    public void setNoty_id(String noty_id) {
        this.noty_id = noty_id;
    }
}

package com.graduate.seoil.sg_projdct.Model;

public class Goal {
    private String title;
    private String start_date;
    private String end_date;
    private String day_cycle;
    private String grade;
    private int plan_time;
    private int notify_time;
    private int rest_count;
    private int time_status;
    private int processed_time_status;
    private int percent_status;
    private long timestamp;

    public Goal(String title, String start_date, String end_date, String day_cycle, String grade ,int plan_time, int notify_time, int rest_count, int time_status, int processed_time_status, int percent_status, long timestamp) {
        this.title = title;
        this.start_date = start_date;
        this.end_date = end_date;
        this.day_cycle = day_cycle;
        this.grade = grade;
        this.plan_time = plan_time;
        this.notify_time = notify_time;
        this.rest_count = rest_count;
        this.time_status = time_status;
        this.processed_time_status = processed_time_status;
        this.percent_status = percent_status;
        this.timestamp = timestamp;
    }

    public Goal() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getDay_cycle() {
        return day_cycle;
    }

    public void setDay_cycle(String day_cycle) {
        this.day_cycle = day_cycle;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getPlan_time() {
        return plan_time;
    }

    public void setPlan_time(int plan_time) {
        this.plan_time = plan_time;
    }

    public int getNotify_time() {
        return notify_time;
    }

    public void setNotify_time(int notify_time) {
        this.notify_time = notify_time;
    }

    public int getRest_count() {
        return rest_count;
    }

    public void setRest_count(int rest_count) {
        this.rest_count = rest_count;
    }

    public int getTime_status() {
        return time_status;
    }

    public void setTime_status(int time_status) {
        this.time_status = time_status;
    }

    public int getProcessed_time_status() {
        return processed_time_status;
    }

    public void setProcessed_time_status(int processed_time_status) {
        this.processed_time_status = processed_time_status;
    }

    public int getPercent_status() {
        return percent_status;
    }

    public void setPercent_status(int percent_status) {
        this.percent_status = percent_status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

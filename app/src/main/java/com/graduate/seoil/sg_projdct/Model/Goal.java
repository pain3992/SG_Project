package com.graduate.seoil.sg_projdct.Model;

public class Goal {
    private String title;
    private String start_date;
    private String end_date;
    private String day_cycle;
    private int plan_time;
    private int start_time;
    private int time_status;
    private int processed_time_status;
    private int percent_status;
    private long timestamp;

    public Goal(String title, String start_date, String end_date, String day_cycle, int plan_time, int start_time, int time_status, int processed_time_status, int percent_status, long timestamp) {
        this.title = title;
        this.start_date = start_date;
        this.end_date = end_date;
        this.day_cycle = day_cycle;
        this.plan_time = plan_time;
        this.start_time = start_time;
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

    public int getPlan_time() {
        return plan_time;
    }

    public void setPlan_time(int plan_time) {
        this.plan_time = plan_time;
    }

    public int getStart_time() {
        return start_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
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

package com.graduate.seoil.sg_projdct.Model;

public class Goal {
    private String key;
    private String goalname;
    private String goaltext;


    public Goal(String key, String goalname, String goaltext) {
        this.key = key;
        this.goalname = goalname;
        this.goaltext = goaltext;
    }

    public Goal() {
    }

    public String getKey() {
        return key;
    }

    public String getgoalname() {
        return goalname;
    }

    public String getgoaltext() {
        return goaltext;
    }



    public void setKey(String key) {
        this.key = key;
    }

    public void setgoalname(String goalname) {
        this.goalname = goalname;
    }

    public void setgoaltext(String goaltext) {
        this.goaltext = goaltext;
    }

}

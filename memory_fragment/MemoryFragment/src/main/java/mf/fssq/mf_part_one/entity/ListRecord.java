package mf.fssq.mf_part_one.entity;

import android.support.annotation.NonNull;

//转存介质
public class ListRecord {
    private String title,week;

    public ListRecord(String title, String week) {
        this.title = title;
        this.week=week;
    }

    public String getTitle() {
        return title;
    }

    public String getWeek() {
        return week;
    }

    @NonNull
    @Override
    public String toString() {
        return title+" "+week;
    }
}

package mf.fssq.mf_part_one.entity;

import android.util.Log;

public class LatelyTime {
    //    最近年月日
    private int lately_year;
    private String lately_month="s";
    private int lately_date;

    //    最近时分秒
    private int lately_hour;
    private int lately_minute;
    private int lately_second;

    private String lately_week;

    public void setLately_year(int lately_year) {
        this.lately_year = lately_year;
    }

    public void setLately_month(String lately_month) {
        this.lately_month = lately_month;
    }

    public void setLately_date(int lately_date) {
        this.lately_date = lately_date;
    }

    public void setLately_hour(int lately_hour) {
        this.lately_hour = lately_hour;
    }

    public void setLately_minute(int lately_minute) {
        this.lately_minute = lately_minute;
    }

    public void setLately_second(int lately_second) {
        this.lately_second = lately_second;
    }

    public void setLately_week(String lately_week) {
        this.lately_week = lately_week;
    }

    public String getLately_week() {
        return lately_week;
    }

    public int getLately_year() {
        return lately_year;
    }

    public String getLately_month() {
        return lately_month;
    }

    public int getLately_date() {
        return lately_date;
    }

    public int getLately_hour() {
        return lately_hour;
    }

    public int getLately_minute() {
        return lately_minute;
    }

    public int getLately_second() {
        return lately_second;
    }


    //    私有化构造函数
    private LatelyTime(){}

    //    创建唯一实例对象，使用一个私有静态成员变量保存
    private static LatelyTime latelyTime=null;

    //    清空对象，用于页面刷新
    public static LatelyTime Uninstall(){
        if (latelyTime!=null){
            latelyTime=null;
        }
        return latelyTime;
    }

    //    对外提供一个公开成员变量获取方法
    public static synchronized LatelyTime getInstance(){
        if (latelyTime==null){
            latelyTime=new LatelyTime();
        }
        return latelyTime;
    }

    public String backTime(){
        return " DATE: "+this.lately_date+"."+this.lately_month+"." +this.lately_year
                +" TIME: "+this.lately_hour+":"+this.lately_minute+":"+this.lately_second
                +" WEEK: "+this.lately_week;
    }
}

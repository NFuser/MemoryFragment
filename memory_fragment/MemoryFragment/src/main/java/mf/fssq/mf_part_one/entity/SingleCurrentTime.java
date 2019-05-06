package mf.fssq.mf_part_one.entity;

import android.util.Log;

import java.util.Calendar;

//    懒汉式创建当前时间对象
public class SingleCurrentTime {

    //    年月日
    private int year;
    private String month;
    private int date;

    //    时分秒
    private int hour;
    private int minute;
    private int second;

    //    星期
    private String week;

    //    获取Calendar类实例对象
    Calendar mCalendar=Calendar.getInstance();

    //    私有化构造函数
    private SingleCurrentTime() {
        ShowTime();
    }

    //    创建唯一实例对象，使用一个私有静态成员变量保存
    private static SingleCurrentTime singleCurrentTime = null;

    //    对外提供一个公开成员变量获取方法
    public static synchronized SingleCurrentTime getInstance() {
        if (singleCurrentTime == null) {
            singleCurrentTime = new SingleCurrentTime();
        }
        return singleCurrentTime;
    }

    public String GetDate(){
//        获取日期
        this.year=mCalendar.get(Calendar.YEAR);
        this.month=BackMonth((Calendar.MONTH)+2);
        this.date=mCalendar.get(Calendar.DAY_OF_MONTH);

        return this.date+"."+this.month+"."+this.year;
    }

    public String GetTime(){
//        获取时间
        mCalendar=null;
        mCalendar=Calendar.getInstance();
        this.hour=mCalendar.get(Calendar.HOUR_OF_DAY);
        this.minute=mCalendar.get(Calendar.MINUTE);
        this.second=mCalendar.get(Calendar.SECOND);
        return this.hour+":"+this.minute+":"+this.second;
    }


    public void ShowTime(){
        GetDate();//设置日期
        GetTime();//设置时间
        this.week=BackWeek(mCalendar.get(Calendar.DAY_OF_WEEK));//设置星期
    }


    public int getYear(){
        return year;
    }

    public String getMonth() {
        return month;
    }

    public int getDate() {
        return date;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public String getWeek() {
        return week;
    }

    public String BackMonth(int i){
        String str=null;
        switch (i){
            case 1:str= "January";break;
            case 2:str= "February";break;
            case 3:str= "March";break;
            case 4:str= "April";break;
            case 5:str= "May";break;
            case 6:str= "June";break;
            case 7:str= "July";break;
            case 8:str= "August";break;
            case 9:str= "September";break;
            case 10:str= "October";break;
            case 11:str= "November";break;
            case 12:str= "December";break;
        }
        return str;
    }
    //    星期转换int->String
    public String BackWeek(int i){
        String str=null;
        switch (i){
            case 1:str= "Sunday";break;
            case 2:str= "Monday";break;
            case 3:str= "Tuesday";break;
            case 4:str= "Wednesday";break;
            case 5:str= "Thursday";break;
            case 6:str= "Friday";break;
            case 7:str= "Saturday";break;
        }
        return str;
    }

    public void clear(){
        if (LatelyTime.getInstance().getLately_year() != SingleCurrentTime.getInstance().getYear() ||
               !(LatelyTime.getInstance().getLately_month().equals(SingleCurrentTime.getInstance().getMonth())) ||
                LatelyTime.getInstance().getLately_date() != SingleCurrentTime.getInstance().getDate()){
//            重新获取Calendar对象并刷新当前时间
            Log.w("test","运行clear,两次时间不一样");
            mCalendar=null;
            mCalendar=Calendar.getInstance();
            ShowTime();
//            保存关闭时间，并清空对象
            LatelyTime.getInstance().setLately_year(singleCurrentTime.getYear());
            LatelyTime.getInstance().setLately_month(singleCurrentTime.getMonth());
            LatelyTime.getInstance().setLately_date(singleCurrentTime.getDate());
            Log.w("test","clear方法"+LatelyTime.getInstance().getLately_year());

            LatelyTime.getInstance().setLately_hour(singleCurrentTime.getHour());
            LatelyTime.getInstance().setLately_minute(singleCurrentTime.getMinute());
            LatelyTime.getInstance().setLately_second(singleCurrentTime.getSecond());

            LatelyTime.getInstance().setLately_week(singleCurrentTime.getWeek());

            singleCurrentTime=null;
            mCalendar=null;
        }else{
//            重新获取Calendar和SingleCurrentTime对象
            Log.w("test","运行clear，两次时间相同");
            mCalendar=null;
            singleCurrentTime=null;
        }
    }
}

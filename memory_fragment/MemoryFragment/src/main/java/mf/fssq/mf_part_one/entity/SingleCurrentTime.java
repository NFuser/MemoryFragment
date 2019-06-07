package mf.fssq.mf_part_one.entity;

import java.util.Calendar;

//    懒汉式创建当前时间对象
public class SingleCurrentTime {
    //    年月日
    private int year;
    private int month;
    private int date;

    //    时分秒
    private int hour;
    private int minute;
    private int second;

    //    星期
    private String week;

    //    获取Calendar类实例对象
    private static Calendar mCalendar=Calendar.getInstance();

    //    私有化构造函数
    private SingleCurrentTime() {
        hour=mCalendar.get(Calendar.HOUR_OF_DAY);
        minute=mCalendar.get(Calendar.MINUTE);
        second=mCalendar.get(Calendar.SECOND);

        year=mCalendar.get(Calendar.YEAR);
        month=mCalendar.get(Calendar.MONTH)+1;
        date=mCalendar.get(Calendar.DAY_OF_MONTH);

        week=BackWeek(mCalendar.get(Calendar.DAY_OF_WEEK));
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
    public int getYear(){
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDate() {
        return date;
    }

    public String getWeek() {
        return week;
    }

    //获取时间
    public String getTime() {
        return hour+":"+minute+":"+second;
    }

    //旧格式的日期
    public String oldFormatDate(){
        return year+"."+month+"."+date;
    }
    //新格式的日期
    public String newFormatDate(){
        String newFormatMonth;
        newFormatMonth=BackMonth(month);
        return date+"."+newFormatMonth+"."+year;
    }

    //月份转换int->String
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

    //星期转换int->String
    private String BackWeek(int i){
        String str=null;
        switch (i){
            case 1:str= "Sunday";break;
            case 2:str= "Monday";break;
            case 3:str= "Tuesday";break;
            case 4:str= "Wednesday";break;
            case 5:str= "Thursday";break;
            case 6:str= "Friday";break;
            case 7:str= "saturday";break;
        }
        return str;
    }

    public void saveToLatelyTime(){
        LatelyTime.getInstance().setLately_year(year);
        LatelyTime.getInstance().setLately_month(month);
        LatelyTime.getInstance().setLately_date(date);
        LatelyTime.getInstance().setLately_week(week);
    }

    public int transmonth(String month) {
        int x=0;
        switch (month){
            case "January": x= 1;break;
            case "February":x= 2;break;
            case "March":x= 3;break;
            case "April":x= 4;break;
            case "May":x= 5;break;
            case "June":x= 6;break;
            case "July":x= 7;break;
            case "August":x= 8;break;
            case "September":x= 9;break;
            case "October":x= 10;break;
            case "November":x= 11;break;
            case "December":x= 12;break;
        }
        return x;
    }

}

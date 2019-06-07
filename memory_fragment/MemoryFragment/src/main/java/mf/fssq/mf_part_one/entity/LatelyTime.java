package mf.fssq.mf_part_one.entity;

public class LatelyTime {
    //最后一条记录的年月日、星期
    private int lately_year;
    private int lately_month;
    private int lately_date;
    private String lately_week;

    //私有化构造函数
    private LatelyTime(){}

    //创建唯一实例对象，使用一个私有静态成员变量保存
    private static LatelyTime latelyTime=null;

    //清空对象，用于页面刷新
    public static void Uninstall(){
        if (latelyTime!=null){
            latelyTime=null;
        }
    }

    //对外提供一个公开成员变量获取方法
    public static synchronized LatelyTime getInstance(){
        if (latelyTime==null){
            latelyTime=new LatelyTime();
        }
        return latelyTime;
    }

    //setFunction
    public void setLately_year(int lately_year) {
        this.lately_year = lately_year;
    }

    public void setLately_month(int lately_month) {
        this.lately_month = lately_month;
    }

    public void setLately_date(int lately_date) {
        this.lately_date = lately_date;
    }

    public void setLately_week(String lately_week) {
        this.lately_week = lately_week;
    }

    //getFunction
    public String getLately_week() {
        return lately_week;
    }

    public int getLately_year() {
        return lately_year;
    }

    public int getLately_month() {
        return lately_month;
    }

    public int getLately_date() {
        return lately_date;
    }

    public String newFormatDate(){
        return lately_date+"."+lately_month+"."+lately_year;
    }
}

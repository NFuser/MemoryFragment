package mf.fssq.mf_part_one.entity;

public class Dairy {

    private String time,title,content,week;

    public Dairy( String time, String title,String week, String content) {

        this.time = time;
        this.title = title;
        this.week=week;
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getWeek() {
        return week;
    }

    @Override
    public String toString() {
        return time+" "+title+" "+content+" "+week;
    }
}

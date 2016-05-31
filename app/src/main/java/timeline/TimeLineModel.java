package timeline;

import java.io.Serializable;

/**
 * Created by YTW on 2016/5/22.
 */
public class TimeLineModel implements Serializable {

    private int id;             //日记id
    private String title;       //日记标题
    private String time;        //日记时间
    private String titleClass;  //日记类型
    private String content;     //日记内容
    private String year;        //日记年份
    private String month;       //日记月份
    private String day;         //日记天数

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TimeLineModel(int id, String title, String time, String titleClass, String content, String year, String month, String day) {
        this.id = id;

        this.title = title;
        this.time = time;
        this.titleClass = titleClass;
        this.content = content;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitleClass() {
        return titleClass;
    }

    public void setTitleClass(String titleClass) {
        this.titleClass = titleClass;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

package plan;

/**
 * Created by YTW on 2016/6/2.
 */
public class PlanModel {
    String title;       //事件标题
    String date;        //事件的日期
    String content;     //事件的内容

    public PlanModel(String title, String date, String content) {
        this.title = title;
        this.date = date;
        this.content = content;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}


package timeline;

import java.io.Serializable;

/**
 * Created by YTW on 2016/5/22.
 */
public class TimeLineModel implements Serializable {

    private String name;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

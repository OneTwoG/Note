package timeline;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ytw.note.R;

/**
 * Created by YTW on 2016/5/22.
 */
public class TimeLineViewHolder extends RecyclerView.ViewHolder {

    public TextView mTitle;       //日记title、
    public TextView mContent;    //日记内容
    public TextView mYear;       //日记年份
    public TextView mMonth;      //日记月份
    public TextView mDay;        //日记天数
    public TextView mTime;       //日记时间
    public TextView mClass;      //日记类型
    public LinearLayout mLinearLayout;      //item布局
    public TimelineView mTimelineView;


    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);
        //View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.recycler_item, null);
        //初始化控件
        mTitle = (TextView) itemView.findViewById(R.id.tv_log_name);
        mContent = (TextView) itemView.findViewById(R.id.tv_log_content);
        mYear = (TextView) itemView.findViewById(R.id.tv_log_year);
        mMonth = (TextView) itemView.findViewById(R.id.tv_log_month);
        mDay = (TextView) itemView.findViewById(R.id.tv_log_day);
        mTime = (TextView) itemView.findViewById(R.id.tv_log_time);
        mClass = (TextView) itemView.findViewById(R.id.tv_log_class);
        mLinearLayout = (LinearLayout) itemView.findViewById(R.id.log_item);

        mTimelineView = (TimelineView) itemView.findViewById(R.id.tlv_log);
        mTimelineView.initLine(viewType);
    }
}

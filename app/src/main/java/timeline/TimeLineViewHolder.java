package timeline;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.ytw.note.R;

/**
 * Created by YTW on 2016/5/22.
 */
public class TimeLineViewHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public TimelineView mTimelineView;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);
        View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.recycler_item, null);
        name = (TextView) itemView.findViewById(R.id.tv_log_name);
        mTimelineView = (TimelineView) view.findViewById(R.id.tlv_log);
        mTimelineView.initLine(viewType);
    }
}

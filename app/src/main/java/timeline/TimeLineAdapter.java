package timeline;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.ytw.note.R;

import java.util.List;

/**
 * Created by YTW on 2016/5/22.
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private List<TimeLineModel> mFeedList;

    private OnItemClickListener listener;       //点击item的回调对象

    public TimeLineAdapter(List<TimeLineModel> feedList) {
        mFeedList = feedList;
    }

    /**
     * item回调方法
     */
    public interface OnItemClickListener{
        void onItemClickListener(View view, int position);
        void onItemLongClickListener(View view, int position);
    }

    /**
     * TimeLine点击事件，设置回调监听
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    /**
     * 创建ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.recycler_item, null);
        return new TimeLineViewHolder(view, viewType);
    }

    /**
     * 将数据绑定到ViewHolder上
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final TimeLineViewHolder holder, final int position) {
        TimeLineModel timeLineModel = mFeedList.get(position);
//        holder.name.setText("name:" + timeLineModel.getName() + "content:" + timeLineModel.getContent());
        holder.mTitle.setText(timeLineModel.getTitle());
        holder.mContent.setText(timeLineModel.getContent());
        holder.mYear.setText(timeLineModel.getYear());
        holder.mMonth.setText(timeLineModel.getMonth());
        holder.mDay.setText(timeLineModel.getDay());
        holder.mTime.setText(timeLineModel.getTime());
        holder.mClass.setText(timeLineModel.getTitleClass());

        if(listener != null){
            holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(v,position);
                }
            });

            holder.mLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClickListener(v, position);
                    return true;
                }
            });
        }
    }

    /**
     * 获取item总数
     * @return
     */
    @Override
    public int getItemCount() {
        return (mFeedList != null ? mFeedList.size() : 0);
    }

    public void add(TimeLineModel model, int position){
        mFeedList.add(position,model);
        notifyItemChanged(position);
    }
}

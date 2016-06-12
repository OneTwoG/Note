package plan;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ytw.note.R;
import com.github.lzyzsd.randomcolor.RandomColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import plan.PlanViewHolder;

/**
 * Created by YTW on 2016/6/2.
 */
public class PlanRecyclerAdapter extends RecyclerView.Adapter<PlanViewHolder> {
    private List<PlanModel> lists;
    private Context context;
    private List<Integer> heights;
    private OnItemClickListener mListener;
    private List<Integer> colors;

    private static final String TAG = "Adapter";

    public PlanRecyclerAdapter(Context context, List<PlanModel> lists) {
        Log.d(TAG, "PlanRecyclerAdapter: ");
        this.context = context;
        this.lists = lists;
        getRandomHeight(this.lists);
    }

    private void getRandomHeight(List<PlanModel> lists) {//得到随机item的高度
        Log.d(TAG, "getRandomHeight: ");
        heights = new ArrayList<>();
        colors = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            heights.add((int) (400 + Math.random() * 450));
            RandomColor randomColor = new RandomColor();
            int color = randomColor.randomColor();
            colors.add(color);
        }
    }

    public interface OnItemClickListener {
        void ItemClickListener(View view, int postion);

        void ItemLongClickListener(View view, int postion);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(context).inflate(R.layout.paln_item, parent, false);
        PlanViewHolder viewHolder = new PlanViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PlanViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();//得到item的LayoutParams布局参数
        params.height = heights.get(position);//把随机的高度赋予item布局

        PlanModel planModel = lists.get(position);

        holder.itemView.setLayoutParams(params);//把params设置给item布局
        holder.mCardView.setCardBackgroundColor(colors.get(position));

        holder.mTitle.setText(planModel.getTitle());
        holder.mDate.setText(planModel.getDate());//为控件绑定数据
        holder.mContent.setText(planModel.getContent());


        if (mListener != null) {//如果设置了监听那么它就不为空，然后回调相应的方法
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: ");
                    int pos = holder.getLayoutPosition();//得到当前点击item的位置pos
                    mListener.ItemClickListener(holder.itemView, pos);//把事件交给我们实现的接口那里处理
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d(TAG, "onLongClick: ");
                    int pos = holder.getLayoutPosition();//得到当前点击item的位置pos
                    mListener.ItemLongClickListener(holder.itemView, pos);//把事件交给我们实现的接口那里处理
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: ");
        return lists.size() == 0 ? 0 : lists.size();
    }

    public void addItem(PlanModel content, int position) {
        Log.d(TAG, "addItem: ");
        heights.add((int) (400 + Math.random() * 450));
        RandomColor randomColor = new RandomColor();
        int color = randomColor.randomColor();
        colors.add(color);
        lists.add(position, content);

        notifyItemInserted(position); //Attention!
    }

    /**
     * 删除Item
     * @param position
     */
    public void removeItem(int position) {
        lists.remove(position);
        notifyItemRemoved(position);
    }

//    ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT) {
//        @Override
//        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//
//            int fromPosition = viewHolder.getAdapterPosition();     //得到拖拽ViewHolder的position
//            int toPosition = target.getAdapterPosition();           //得到目标ViewHolder的position
//            if (fromPosition < toPosition){
//                //分别把中间所有的item的位置交换
//                for (int i = fromPosition; i < toPosition; i++) {
//                    Collections.swap(lists, i, i+ 1);
//                }
//            }else {
//                for (int i = fromPosition; i < toPosition; i++) {
//                    Collections.swap(lists, i , i - 1);
//                }
//            }
//            notifyItemMoved(fromPosition, toPosition);
//            //返回true表示执行拖动
//            return true;
//        }
//
//        @Override
//        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//
//        }
//
//        @Override
//        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
//                //滑动时改变Item的透明度
//                final float alpha = 1 - Math.abs(dX) / viewHolder.itemView.getWidth();
//                viewHolder.itemView.setAlpha(alpha);
//                viewHolder.itemView.setTranslationX(dX);
//            }
//        }
//    };
//
//    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
//    itemTouchHelper.attachToRecyclerView(mRecyclerView);
}
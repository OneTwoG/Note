package plan;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.ytw.note.R;

/**
 * Created by YTW on 2016/6/2.
 */
public class PlanViewHolder extends RecyclerView.ViewHolder {
    TextView mTitle;
    TextView mDate;
    TextView mContent;
    CardView mCardView;

    public PlanViewHolder(View itemView) {
        super(itemView);

        mTitle = (TextView) itemView.findViewById(R.id.plan_title);
        mDate = (TextView) itemView.findViewById(R.id.plan_date);
        mCardView = (CardView) itemView.findViewById(R.id.plan_card);
        mContent = (TextView) itemView.findViewById(R.id.plan_content);
    }
}

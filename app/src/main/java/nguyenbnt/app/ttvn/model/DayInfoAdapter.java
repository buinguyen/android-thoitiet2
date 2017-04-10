package nguyenbnt.app.ttvn.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import nguyenbnt.app.ttvn.R;
import nguyenbnt.app.ttvn.model.accuweather.DailyForecasts;

/**
 * Created by 4000225 on 3/28/2017.
 */

public class DayInfoAdapter extends RecyclerView.Adapter<DayInfoAdapter.MyViewHolder> {

    private Context mContext;
    private List<DailyForecasts> dayInfos;

    public DayInfoAdapter(Context pContext) {
        mContext = pContext;
    }

    public DayInfoAdapter(Context pContext, List<DailyForecasts> dayInfos) {
        this(pContext);
        this.dayInfos = dayInfos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_weather_info_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DailyForecasts dayInfo = dayInfos.get(position);
        if (dayInfo == null) {
            return;
        }
        holder.mTvTitle.setText(dayInfo.Date);
        holder.mTvMinTemp.setText(mContext.getString(R.string.pretext_min_temp) + dayInfo.Temperature.Minimum + mContext.getResources().getString(R.string.degree_celsius));
        holder.mTvMaxTemp.setText(mContext.getString(R.string.pretext_max_temp) + dayInfo.Temperature.Maximum + mContext.getResources().getString(R.string.degree_celsius));
        holder.mTvDayContent.setText(dayInfo.Day.IconPhrase);
        holder.mTvNightContent.setText(dayInfo.Night.IconPhrase);
        int dayResId = mContext.getResources().getIdentifier("ic_weather_" + dayInfo.Day.Icon,
                "drawable", mContext.getPackageName());
        int nightResId = mContext.getResources().getIdentifier("ic_weather_" + dayInfo.Night.Icon,
                "drawable", mContext.getPackageName());
        Picasso.with(mContext).load(dayResId).into(holder.mImvDayContent);
        Picasso.with(mContext).load(nightResId).into(holder.mImvNightContent);
    }

    @Override
    public int getItemCount() {
        return dayInfos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_title)
        public TextView mTvTitle;
        @Bind(R.id.tv_min_temp)
        public TextView mTvMinTemp;
        @Bind(R.id.tv_max_temp)
        public TextView mTvMaxTemp;
        @Bind(R.id.tv_day_content)
        public TextView mTvDayContent;
        @Bind(R.id.iv_day_content)
        public ImageView mImvDayContent;
        @Bind(R.id.tv_night_content)
        public TextView mTvNightContent;
        @Bind(R.id.iv_night_content)
        public ImageView mImvNightContent;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
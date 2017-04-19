package com.chenxujie.mobileoa.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.model.Schedule;

import java.util.List;


/**
 * Created by minxing on 2017-04-13.
 */

public class ScheduleAdapter extends ArrayAdapter<Schedule> {
    private int resourceId;

    public ScheduleAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Schedule> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Schedule schedule=getItem(position);
        View view;
        final ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.startTime = (TextView) view.findViewById(R.id.item_schedule_starttime);
            viewHolder.endTime = (TextView) view.findViewById(R.id.item_schedule_endtime);
            viewHolder.scheduleTitle=(TextView)view.findViewById(R.id.item_schedule_title);
            viewHolder.position=(TextView)view.findViewById(R.id.item_schedule_position);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.startTime.setText(schedule.getStarttime());
        viewHolder.endTime.setText(schedule.getEndTime());
        viewHolder.scheduleTitle.setText(schedule.getTitle());
        viewHolder.position.setText(Integer.valueOf(position+1).toString());
        return view;
    }


    class ViewHolder{
        TextView startTime;
        TextView endTime;
        TextView scheduleTitle;
        TextView position;
    }
}

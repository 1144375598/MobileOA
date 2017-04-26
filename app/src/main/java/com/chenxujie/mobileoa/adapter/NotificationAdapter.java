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
import com.chenxujie.mobileoa.model.Notification;

import java.util.List;

/**
 * Created by minxing on 2017-04-20.
 */

public class NotificationAdapter extends ArrayAdapter<Notification> {
    private int resourceId;

    public NotificationAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Notification> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Notification notification = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.title_txt);
            viewHolder.content = (TextView) view.findViewById(R.id.content_txt);
            viewHolder.date = (TextView) view.findViewById(R.id.datetime_txt);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.title.setText(notification.getTitle());
        viewHolder.date.setText(notification.getDate());
        viewHolder.content.setText(notification.getContent());
        return view;
    }

    class ViewHolder {
        TextView title;
        TextView date;
        TextView content;
    }
}

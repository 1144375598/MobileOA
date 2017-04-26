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
import com.chenxujie.mobileoa.model.Missive;

import java.util.List;

/**
 * Created by minxing on 2017-04-26.
 */

public class WrittenMissiveAdapter extends ArrayAdapter<Missive> {
    private int resourceId;

    public WrittenMissiveAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Missive> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Missive missive = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.written_missive_title);
            viewHolder.content = (TextView) view.findViewById(R.id.written_missive_content);
            viewHolder.date = (TextView) view.findViewById(R.id.written_missive_datetime);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.title.setText(missive.getTitle());
        viewHolder.date.setText(missive.getTime());
        viewHolder.content.setText(missive.getContent());
        return view;

    }

    class ViewHolder {
        TextView title;
        TextView date;
        TextView content;
    }
}

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
import com.chenxujie.mobileoa.model.Email;

import java.util.List;

/**
 * Created by minxing on 2017-04-19.
 */

public class EmailAdapter extends ArrayAdapter <Email>{
    private int resourceId;
    public EmailAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Email> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Email email=getItem(position);
        View view;
        final ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.position = (TextView) view.findViewById(R.id.item_email_position);
            viewHolder.title = (TextView) view.findViewById(R.id.item_email_title);
            viewHolder.date=(TextView)view.findViewById(R.id.item_email_date);
            viewHolder.sender=(TextView)view.findViewById(R.id.item_email_sender);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.title.setText(email.getTitle());
        viewHolder.sender.setText(email.getSender().getName());
        viewHolder.date.setText(email.getDate());
        viewHolder.position.setText(Integer.valueOf(position+1).toString());
        return view;
    }


    class ViewHolder{
        TextView date;
        TextView title;
        TextView sender;
        TextView position;
    }
}

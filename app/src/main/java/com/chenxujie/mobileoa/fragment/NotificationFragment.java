package com.chenxujie.mobileoa.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.activity.NotificationDetailActivity;
import com.chenxujie.mobileoa.adapter.NotificationAdapter;
import com.chenxujie.mobileoa.model.Notification;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class NotificationFragment extends Fragment {
    private ListView listView;
    private Activity activity;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, null);
        listView = (ListView) view.findViewById(R.id.listview_notification);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notification notification = notificationList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("title", notification.getTitle());
                bundle.putString("content", notification.getContent());
                bundle.putString("sender", notification.getSender());
                bundle.putString("level", notification.getLevel());
                bundle.putString("date",notification.getDate());
                bundle.putString("filename", notification.getFilename());
                bundle.putString("filepath", notification.getFilepath());
                Intent intent = new Intent(activity, NotificationDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        query();
    }

    private void query() {
        final ProgressDialog dialog = ProgressDialog.show(activity, "请稍等...", "正在打开...");
        BmobQuery<Notification> bmobQuery = new BmobQuery<>();
        bmobQuery.setLimit(1000);
        bmobQuery.findObjects(new FindListener<Notification>() {
            @Override
            public void done(List<Notification> list, BmobException e) {
                dialog.dismiss();
                if (e == null) {
                    notificationList = list;
                    notificationAdapter = new NotificationAdapter(activity, R.layout.item_notification, list);
                    listView.setAdapter(notificationAdapter);
                } else {
                    if (e.getErrorCode() == 9009) {
                        Toast.makeText(activity, "无通知", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(activity,"通知查询失败", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("query notification fail", e.getErrorCode()+" "+e.getMessage());
                }
            }
        });
    }
}

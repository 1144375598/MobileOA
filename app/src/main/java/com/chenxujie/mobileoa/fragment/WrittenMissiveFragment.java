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
import com.chenxujie.mobileoa.activity.MissiveDetailActivity;
import com.chenxujie.mobileoa.adapter.WrittenMissiveAdapter;
import com.chenxujie.mobileoa.model.Missive;
import com.chenxujie.mobileoa.model.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class WrittenMissiveFragment extends Fragment {
    private ListView listView;
    private Activity activity;
    private WrittenMissiveAdapter writtenMissiveAdapter;
    private List<Missive> missiveList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_written_missive, null);
        listView = (ListView) view.findViewById(R.id.listview_written_missive);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Missive missive = missiveList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("title", missive.getTitle());
                bundle.putString("content", missive.getContent());
                bundle.putString("receiver", missive.getReceiver());
                bundle.putString("sender", missive.getSender());
                bundle.putString("time", missive.getTime());
                bundle.putString("filename", missive.getFileName());
                bundle.putString("filepath", missive.getFilePath());
                bundle.putString("comment",missive.getComment());
                bundle.putBoolean("isWritten",true);
                bundle.putString("id",missive.getObjectId());
                Intent intent = new Intent(activity, MissiveDetailActivity.class);
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
        BmobQuery<Missive> bmobQuery = new BmobQuery<>();
        bmobQuery.setLimit(1000);
        bmobQuery.addWhereEqualTo("isWritten",true);
        bmobQuery.addWhereEqualTo("receiver", BmobUser.getCurrentUser(User.class).getUsername());
        bmobQuery.findObjects(new FindListener<Missive>() {
            @Override
            public void done(List<Missive> list, BmobException e) {
                dialog.dismiss();
                if (e == null) {
                    missiveList = list;
                    writtenMissiveAdapter = new WrittenMissiveAdapter(activity, R.layout.item_missive, missiveList);
                    listView.setAdapter(writtenMissiveAdapter);
                } else {
                    if (e.getErrorCode() == 9009) {
                        Toast.makeText(activity, "无已阅公文", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(activity,"公文查询失败", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("已阅公文查询失败", e.getErrorCode()+" "+e.getMessage());
                }
            }
        });
    }
}

package com.chenxujie.mobileoa.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.activity.AddScheduleActivity;
import com.chenxujie.mobileoa.activity.ContactActivity;
import com.chenxujie.mobileoa.adapter.ScheduleAdapter;
import com.chenxujie.mobileoa.model.Schedule;
import com.chenxujie.mobileoa.model.User;
import com.chenxujie.mobileoa.util.CalendarHelper;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class ScheduleFragment extends Fragment {
    private ListView listView;
    private List<Schedule> schedules;
    private ScheduleAdapter scheduleAdapter;
    private Activity activity;
    private Button addSchedule;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, null);
        listView = (ListView) view.findViewById(R.id.listview_schedule);
        addSchedule = (Button) view.findViewById(R.id.add_schedule);
        addSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AddScheduleActivity.class);
                intent.putExtra("isAdd", true);
                activity.startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Schedule schedule = schedules.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("title", schedule.getTitle());
                bundle.putString("startTime", schedule.getStarttime());
                bundle.putString("endTime", schedule.getEndTime());
                bundle.putString("content", schedule.getContent());
                bundle.putString("address", schedule.getAddesss());
                bundle.putString("id", schedule.getObjectId());
                bundle.putBoolean("isAdd", false);
                Intent intent = new Intent(activity,
                        AddScheduleActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        registerForContextMenu(listView);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        query();
    }

    private void query() {
        final ProgressDialog dialog = ProgressDialog.show(activity, "请稍等...", "正在打开...");
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<Schedule> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("user", new BmobPointer(user));
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        bmobQuery.findObjects(new FindListener<Schedule>() {
            @Override
            public void done(List<Schedule> list, BmobException e) {
                dialog.dismiss();
                if (e == null) {
                    schedules = list;
                    scheduleAdapter = new ScheduleAdapter(activity, R.layout.item_schedule, list);
                    listView.setAdapter(scheduleAdapter);
                } else {
                    if (e.getErrorCode() == 9009) {
                        Toast.makeText(activity, getString(R.string.no_schedule), Toast
                                .LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(activity, getString(R.string.query_schedule_fail), Toast
                                .LENGTH_SHORT).show();
                    }
                    Log.e("query contact fail", e.toString());
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 1, 0, "删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView
                        .AdapterContextMenuInfo) item
                        .getMenuInfo();
                View view = listView.getChildAt(menuInfo.position);
                TextView position = (TextView) view.findViewById(R.id.item_schedule_position);
                final TextView title = (TextView) view.findViewById(R.id.item_schedule_title);
                final Integer pos = Integer.valueOf(position.getText().toString()) - 1;
                final Schedule temp = schedules.get(pos);
                final Schedule schedule1 = new Schedule();
                schedule1.setObjectId(temp.getObjectId());
                schedule1.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            CalendarHelper.deleteCalendarEvent(activity, title.getText().toString());
                            Toast.makeText(activity, getString(R.string.delete_success), Toast
                                    .LENGTH_SHORT).show();
                            schedules.remove(temp);
                            scheduleAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(activity, getString(R.string.delete_fail), Toast
                                    .LENGTH_SHORT).show();
                            Log.e("删除日程失败", "失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
                break;
        }
        return super.onContextItemSelected(item);
    }
}

package com.chenxujie.mobileoa.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.model.Schedule;
import com.chenxujie.mobileoa.model.User;
import com.chenxujie.mobileoa.util.ActivityManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class AddScheduleActivity extends Activity implements View.OnClickListener {
    private EditText content;
    private EditText title;
    private TextView startTime;
    private TextView endTime;
    private EditText address;
    private Button save;
    private Button selectStart;
    private Button selectEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        setContentView(R.layout.activity_add_schedule);

        content = (EditText) findViewById(R.id.schedule_content);
        title = (EditText) findViewById(R.id.schedule_title);
        startTime = (TextView) findViewById(R.id.show_schedule_starttime);
        endTime = (TextView) findViewById(R.id.show_schedule_endtime);
        address = (EditText) findViewById(R.id.schedule_address);
        save = (Button) findViewById(R.id.schedule_save);
        selectStart = (Button) findViewById(R.id.select_schedule_starttime);
        selectEnd = (Button) findViewById(R.id.select_shechule_endtime);

        init();
    }

    private void init() {
        if (!getIntent().getExtras().getBoolean("isAdd")) {
            content.setText(getIntent().getExtras().getString("content"));
            title.setText(getIntent().getExtras().getString("title"));
            startTime.setText(getIntent().getExtras().getString("startTime"));
            endTime.setText(getIntent().getExtras().getString("endTime"));
            address.setText(getIntent().getExtras().getString("address"));
        }
        save.setOnClickListener(this);
        selectStart.setOnClickListener(this);
        selectEnd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_schedule_starttime:
                showDatePicker(startTime);
                break;
            case R.id.select_shechule_endtime:
                showDatePicker(endTime);
                break;
            case R.id.schedule_save:
                saveOrUpdate();
                break;
        }
    }

    private void showDatePicker(final TextView textView) {
        View view = View.inflate(getApplicationContext(), R.layout.dialog_datetime_picker, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);

        // Init DatePicker
        int year;
        int month;
        int day;
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year, month, day, null);

        int hour;
        int minute;
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);

        // Build DateTimeDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(AddScheduleActivity.this,R.style.Theme_picker);
        builder.setView(view);
        builder.setTitle("设置时间");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Integer year = datePicker.getYear();
                Integer month = datePicker.getMonth();
                Integer day = datePicker.getDayOfMonth();
                Integer hour = timePicker.getCurrentHour();
                Integer minute = timePicker.getCurrentMinute();
               Calendar calendar=Calendar.getInstance();
                calendar.set(year,month,day,hour,minute);
                Date date=new Date(calendar.getTimeInMillis());
                SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
                textView.setText(dateformat.format(date));
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void saveOrUpdate() {
        if (getIntent().getExtras().getBoolean("isAdd")) {
            if (TextUtils.isEmpty(title.getText().toString())) {
                Toast.makeText(AddScheduleActivity.this, getString(R.string.title_is_null), Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(content.getText().toString())) {
                Toast.makeText(AddScheduleActivity.this, getString(R.string.content_is_null), Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.equals(startTime.getText().toString(), getString(R.string.schedule_start_hint))) {
                Toast.makeText(AddScheduleActivity.this, getString(R.string.starttime_is_null), Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.equals(endTime.getText().toString(), getString(R.string.schedule_end_hint))) {
                Toast.makeText(AddScheduleActivity.this, getString(R.string.endtime_is_null), Toast.LENGTH_SHORT).show();
                return;
            } else {
                Schedule schedule = new Schedule();
                schedule.setAddesss(address.getText().toString());
                schedule.setTitle(title.getText().toString());
                schedule.setContent(content.getText().toString());
                schedule.setStarttime(startTime.getText().toString());
                schedule.setEndTime(endTime.getText().toString());
                schedule.setUser(BmobUser.getCurrentUser(User.class));
                schedule.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            Toast.makeText(AddScheduleActivity.this, getString(R.string.add_schedule_success), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddScheduleActivity.this, getString(R.string.add_schedule_fail), Toast.LENGTH_SHORT).show();
                            Log.e("日程添加失败", e.getErrorCode() + " " + e.getMessage());
                        }
                    }
                });
            }
        } else {
            Schedule schedule = new Schedule();
            schedule.setAddesss(address.getText().toString());
            schedule.setTitle(title.getText().toString());
            schedule.setContent(content.getText().toString());
            schedule.setStarttime(startTime.getText().toString());
            schedule.setEndTime(endTime.getText().toString());
            schedule.setUser(BmobUser.getCurrentUser(User.class));
            schedule.update(getIntent().getExtras().getString("id"), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Toast.makeText(AddScheduleActivity.this, getString(R.string.update_schedule_success), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddScheduleActivity.this, getString(R.string.update_schedule_fail), Toast.LENGTH_SHORT).show();
                        Log.e("日程更新失败", e.getErrorCode() + " " + e.getMessage());
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }
}

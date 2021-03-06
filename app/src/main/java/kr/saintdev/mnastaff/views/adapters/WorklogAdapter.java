package kr.saintdev.mnastaff.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import kr.saintdev.mnastaff.R;
import kr.saintdev.mnastaff.models.datas.objects.WorklogObject;
import kr.saintdev.mnastaff.models.datas.profile.MeProfileManager;


/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-09
 */

public class WorklogAdapter extends BaseAdapter {
    private ArrayList<WorklogObject> works = null;
    private MeProfileManager profileManager = null;

    public WorklogAdapter() {
        this.works = new ArrayList<>();
    }

    public void setItem(ArrayList<WorklogObject> items) {
        this.works = items;
    }

    public void addItem(WorklogObject item) {
        this.works.add(item);
    }

    @Override
    public int getCount() {
        return this.works.size();
    }

    @Override
    public Object getItem(int position) {
        return works.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clear() {
        if(works != null) {
            works.clear();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_worklog, parent, false);
        }

        TextView workLogDate = convertView.findViewById(R.id.layout_worklog_date);          // 날짜
        TextView workLogSignTime = convertView.findViewById(R.id.layout_worklog_time);      // 계약상 근무 시간
        TextView workMoney = convertView.findViewById(R.id.layout_worklog_money);           // 급여
        TextView workLogRealTime = convertView.findViewById(R.id.layout_worklog_realwork);  // 실제 근무 시간
        TextView workLogOkTime = convertView.findViewById(R.id.layout_worklog_okwork);      // 인정된 근무 시간

        WorklogObject work = works.get(position);

        String date = work.getWorkDate().substring(5);
        workLogDate.setText(date);    // 근무 시작 시간을 표시
        workLogSignTime.setText(work.getSignTime());    // 계약서상 근무 시간 표시

        if(work.isNowWorking()) {
            // 현재 근무중. 일부 데이터만 표시
            workLogRealTime.setText("퇴근 하지 않음");
            workLogOkTime.setText("근무 중");
            workMoney.setText("근무 중");
        } else {
            // 퇴근함. 모든 데이터 표시
            Date startDate = new Date(work.getWorkStart() * 1000);
            Date endDate = new Date(work.getWorkStop() * 1000);
            workLogRealTime.setText(
                    "출근 " + startDate.getHours() + ":" + startDate.getMinutes() + " ~ 퇴근" + endDate.getHours() + ":" + endDate.getMinutes());
            workLogOkTime.setText("인정된 시간 : " + work.getAdmitTime() + " 분");
            workMoney.setText("총 급여 : " +(work.getMoney() * (work.getAdmitTime() / 10)));
        }


        return convertView;
    }
}

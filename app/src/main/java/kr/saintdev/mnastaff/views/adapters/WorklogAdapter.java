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

        workLogDate.setText(work.getWorkDate());    // 근무 시작 시간을 표시
        workLogSignTime.setText(work.getSignTime());    // 계약서상 근무 시간 표시

        if(work.isNowWorking()) {
            // 현재 근무중. 일부 데이터만 표시
            workLogRealTime.setText("퇴근 하지 않음");
            workLogOkTime.setText("~");
            workMoney.setText("~");
        } else {
            // 퇴근함. 모든 데이터 표시
            Date startDate = new Date(work.getWorkStart() * 1000);
            Date endDate = new Date(work.getWorkStop() * 1000);
            workLogRealTime.setText(
                    "출근 " + startDate.getHours() + ":" + startDate.getMinutes() + " ~ 퇴근" + endDate.getHours() + ":" + endDate.getMinutes());
            workLogOkTime.setText(work.getAdmitTime() + " 분");
            workMoney.setText("총 급여 : " +(work.getMoney() * (work.getAdmitTime() / 10)));
        }


//        if(!work.isNowWorking()) {
//            try {
//                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.KOREA);
//                Date startTime = sdf.parse(work.getWorkStartTime());
//                Date endTime = sdf.parse(work.getWorkEndTime());
//
//                long diff = (endTime.getTime() - startTime.getTime()) / 1000;
//
//                // 인정된 근무시간을 구합니다.
//                String workTime = ConstConverter.getHMS((int) diff);
//                workLogOkTime.setText("인정됨 : " + workTime);
//
//                // 급여를 구합니다.
//                int tenMinPerMoney = Integer.parseInt(accountManager.getValue("staff-money"));
//                int length = (int) ((diff/600) * tenMinPerMoney);
//
//                workMoney.setText(length + " 원");
//            } catch (ParseException pex) {
//                pex.printStackTrace();
//            }
//        } else {
//            workLogOkTime.setText("퇴근하지 않음.");
//            workMoney.setText("퇴근안함");
//        }

        return convertView;
    }
}

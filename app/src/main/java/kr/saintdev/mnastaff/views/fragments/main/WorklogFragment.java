package kr.saintdev.mnastaff.views.fragments.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.saintdev.mnastaff.R;
import kr.saintdev.mnastaff.models.datas.constants.InternetConst;
import kr.saintdev.mnastaff.models.datas.objects.WorklogObject;
import kr.saintdev.mnastaff.models.tasks.BackgroundWork;
import kr.saintdev.mnastaff.models.tasks.OnBackgroundWorkListener;
import kr.saintdev.mnastaff.models.tasks.http.HttpRequester;
import kr.saintdev.mnastaff.models.tasks.http.HttpResponseObject;
import kr.saintdev.mnastaff.views.activitys.MainActivity;
import kr.saintdev.mnastaff.views.adapters.WorklogAdapter;
import kr.saintdev.mnastaff.views.fragments.SuperFragment;
import kr.saintdev.mnastaff.views.windows.dialog.DialogManager;
import kr.saintdev.mnastaff.views.windows.dialog.clicklistener.OnYesClickListener;
import kr.saintdev.mnastaff.views.windows.progress.ProgressManager;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-06-01
 */

public class WorklogFragment extends SuperFragment {
    MainActivity control = null;

    private TextView workspaceNameView = null;
    private TextView staffNameView = null;
    private Button selectDate = null;
    private TextView totalWorkTime = null;
    private ListView worklogList = null;
    private TextView worklogEmptyView = null;
    private LinearLayout worklogContainer = null;

    OnBackgroundWorkHandler backgroundHandler = null;
    WorklogAdapter adapter = null;

    DialogManager dm = null;
    ProgressManager pm = null;

    private static final int REQUEST_WORK_LOG = 0x0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_main_worklog, container, false);
        this.control = (MainActivity) getActivity();

        this.workspaceNameView = v.findViewById(R.id.main_worklog_header_workspace);
        this.staffNameView = v.findViewById(R.id.main_worklog_header_name);
        this.selectDate = v.findViewById(R.id.main_worklog_section);
        this.totalWorkTime = v.findViewById(R.id.main_worklog_section_time);
        this.worklogList = v.findViewById(R.id.main_worklog_listview);
        this.worklogEmptyView = v.findViewById(R.id.main_worklog_empty);
        this.worklogContainer = v.findViewById(R.id.main_worklog_container);

        this.adapter = new WorklogAdapter();
        this.worklogList.setAdapter(this.adapter);
        this.backgroundHandler = new OnBackgroundWorkHandler();

        this.dm = new DialogManager(control);
        this.dm.setOnYesButtonClickListener(new OnYesClickListener() {
            @Override
            public void onClick(DialogInterface dialog) {
                dialog.dismiss();
            }
        }, "OK");
        this.pm = new ProgressManager(control);

        updateWorklog();

        return v;
    }

    /**
     * 이 사용자의 데이터를 업데이트 합니다.
     */
    private void updateWorklog() {
        HttpRequester requester =
                new HttpRequester(InternetConst.MY_WORKLOG, null, REQUEST_WORK_LOG, backgroundHandler, control);
        requester.execute();
    }

    class OnBackgroundWorkHandler implements OnBackgroundWorkListener {
        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            HttpResponseObject respObj = (HttpResponseObject) worker.getResult();

            try {
                if (respObj.getResponseResultCode() == InternetConst.HTTP_OK) {
                    if (requestCode == REQUEST_WORK_LOG) {
                        // 근무 로그를 불러왔습니다.
                        JSONObject body = respObj.getBody();

                        int length = body.getInt("length");
                        JSONArray datas = body.getJSONArray("datas");

                        if(length == 0) {
                            worklogContainer.setVisibility(View.GONE);
                            worklogEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            worklogContainer.setVisibility(View.VISIBLE);
                            worklogEmptyView.setVisibility(View.GONE);

                            for(int i = 0; i < datas.length(); i ++) {
                                JSONObject worklog = datas.getJSONObject(i);

                                WorklogObject obj = new WorklogObject(
                                        worklog.getString("workspace-uuid"),
                                        worklog.getString("staff-sign-time"),
                                        worklog.getString("work-date"),
                                        worklog.getLong("staff-work-start"),
                                        (worklog.isNull("staff-work-stop") ? 0 : worklog.getLong("staff-work-stop")),
                                        worklog.getInt("staff-money"),
                                        (worklog.isNull("staff-admit-time") ? 0 : worklog.getInt("staff-admit-time")),
                                        worklog.getString("staff-status").equals("working")
                                );

                                adapter.addItem(obj);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                } else {
                    dm.setTitle("An error occurred");
                    dm.setDescription("서버에서 데이터를 불러올 수 없습니다.");
                    dm.show();
                }
            } catch(Exception ex) {
                dm.setTitle("An error occurred");
                dm.setDescription(ex.getLocalizedMessage());
                dm.show();

                ex.printStackTrace();
            }
        }

        @Override
        public void onFailed(int requestCode, Exception ex) {
            dm.setTitle("Request error");
            dm.setDescription(ex.getLocalizedMessage());
            dm.show();
        }
    }
}

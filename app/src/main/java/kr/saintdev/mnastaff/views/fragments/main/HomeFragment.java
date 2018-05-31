package kr.saintdev.mnastaff.views.fragments.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import kr.saintdev.mnastaff.R;
import kr.saintdev.mnastaff.models.datas.constants.InternetConst;
import kr.saintdev.mnastaff.models.tasks.BackgroundWork;
import kr.saintdev.mnastaff.models.tasks.OnBackgroundWorkListener;
import kr.saintdev.mnastaff.models.tasks.http.HttpRequester;
import kr.saintdev.mnastaff.models.tasks.http.HttpResponseObject;
import kr.saintdev.mnastaff.views.activitys.MainActivity;
import kr.saintdev.mnastaff.views.fragments.SuperFragment;
import kr.saintdev.mnastaff.views.windows.dialog.DialogManager;
import kr.saintdev.mnastaff.views.windows.dialog.clicklistener.OnYesClickListener;
import kr.saintdev.mnastaff.views.windows.progress.ProgressManager;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-27
 */

public class HomeFragment extends SuperFragment {
    MainActivity control = null;

    TextView workspaceName = null;
    TextView myStatus = null;
    Button[] statusButton = null;

    OnBackgroundHandle backgroundHandle = null;

    DialogManager dm = null;
    ProgressManager pm = null;

    private static final int REQUEST_WORKSPACE_STATUS = 0x0;
    private static final int REQUEST_GOTO_WORK = 0x1;       // 출근 요청
    private static final int REQUEST_GOTO_HOME = 0x2;       // 퇴근 요청

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_main_home, container, false);
        this.control = (MainActivity) getActivity();
        this.workspaceName = v.findViewById(R.id.staff_home_workspace);
        this.myStatus = v.findViewById(R.id.staff_my_status);
        this.statusButton = new Button[]{
                v.findViewById(R.id.staff_home_gowork),
                v.findViewById(R.id.staff_home_gohome)
        };
        this.dm = new DialogManager(control);
        this.dm.setOnYesButtonClickListener(new OnYesClickListener() {
            @Override
            public void onClick(DialogInterface dialog) {
                dialog.dismiss();
            }
        }, "OK");
        this.pm = new ProgressManager(control);
        this.backgroundHandle = new OnBackgroundHandle();

        OnButtonClickHandler handler = new OnButtonClickHandler();
        this.statusButton[0].setOnClickListener(handler);
        this.statusButton[1].setOnClickListener(handler);

        HttpRequester requester =
                new HttpRequester(InternetConst.MY_WORKSPACE_STATUS, null, REQUEST_WORKSPACE_STATUS, backgroundHandle, this.control);
        requester.execute();
        return v;
    }

    /**
     * 버튼 클릭 리스너
     */
    class OnButtonClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }

    /**
     * 백그라운드 스래드 핸들러
     */
    class OnBackgroundHandle implements OnBackgroundWorkListener {
        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            if(requestCode == REQUEST_WORKSPACE_STATUS) {
                // 현재 이 직원의 근무지 상태를 불러왔습니다.
                HttpResponseObject respObj = (HttpResponseObject) worker.getResult();

                if(respObj.getResponseResultCode() == InternetConst.HTTP_OK && !respObj.isErrorOccurred()) {
                    JSONObject body = respObj.getBody();
                    try {
                        workspaceName.setText(body.getString("workspace-name"));
                        myStatus.setText(body.getString("sign-time") + " / " + body.getString("staff-money") + " 원");
                    } catch(JSONException jex) {
                        dm.setTitle("An error occurred");
                        dm.setDescription(jex.getLocalizedMessage());
                        dm.show();
                        jex.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onFailed(int requestCode, Exception ex) {
            dm.setTitle("Internal server error.");
            dm.setDescription("Error code : " + requestCode + "\n" + ex.getLocalizedMessage());
            dm.show();
        }
    }
}

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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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

        updateStatus();
        return v;
    }

    /**
     * 버튼 클릭 리스너
     */
    class OnButtonClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            HttpRequester requester = null;
            HashMap<String, Object> args = new HashMap<>();

            switch(v.getId()) {
                case R.id.staff_home_gowork:
                    args.put("staff-status", "working");
                    requester = new HttpRequester(InternetConst.COMMUTE_ME, args, REQUEST_GOTO_WORK, backgroundHandle, control);
                    break;
                case R.id.staff_home_gohome:
                    args.put("staff-status", "home");
                    requester = new HttpRequester(InternetConst.COMMUTE_ME, args, REQUEST_GOTO_HOME, backgroundHandle, control);
                    break;
            }

            requester.execute();

            pm.setMessage("통근 처리 중...");
            pm.enable();
        }
    }

    /**
     * 백그라운드 스래드 핸들러
     */
    class OnBackgroundHandle implements OnBackgroundWorkListener {
        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            HttpResponseObject respObj = (HttpResponseObject) worker.getResult();
            pm.disable();

            if(requestCode == REQUEST_WORKSPACE_STATUS) {
                // 현재 이 직원의 근무지 상태를 불러왔습니다.
                if(respObj.getResponseResultCode() == InternetConst.HTTP_OK && !respObj.isErrorOccurred()) {
                    JSONObject body = respObj.getBody();
                    try {
                        workspaceName.setText(body.getString("workspace-name"));
                        myStatus.setText(body.getString("sign-time") + " / " + body.getString("staff-money") + " 원");

                        // 직원의 통근 상태를 표시합니다.
                        String status = body.getString("staff-status");
                        if(status.equals("home")) {
                            // 퇴근
                            nowHome();
                        } else if(status.equals("working")) {
                            // 출근
                            nowWorking();
                        }

                    } catch(JSONException jex) {
                        dm.setTitle("An error occurred");
                        dm.setDescription(jex.getLocalizedMessage());
                        dm.show();
                        jex.printStackTrace();
                    }
                }
            } else if(requestCode == REQUEST_GOTO_HOME) {
                // 이 직원은 퇴근하였습니다.
                if(respObj.getResponseResultCode() == InternetConst.HTTP_OK && !respObj.isErrorOccurred()) {
                    Toast.makeText(control, "퇴근 하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    dm.setTitle("An error occurred");
                    dm.setDescription("퇴근 실패!\n" + respObj.getErrorMessage());
                    dm.show();
                }

                updateStatus();
            } else if(requestCode == REQUEST_GOTO_WORK) {
                // 이 직원은 출근하였습니다.
                if(respObj.getResponseResultCode() == InternetConst.HTTP_OK && !respObj.isErrorOccurred()) {
                    Toast.makeText(control, "출근 하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    dm.setTitle("An error occurred");
                    dm.setDescription("출근 실패!\n" + respObj.getErrorMessage());
                    dm.show();
                }

                updateStatus();
            }
        }

        @Override
        public void onFailed(int requestCode, Exception ex) {
            pm.disable();

            dm.setTitle("Internal server error.");
            dm.setDescription("Error code : " + requestCode + "\n" + ex.getLocalizedMessage());
            dm.show();
        }

        /**
         * 절찬 근무중 입니다.
         * 출근하기에 비활성화
         * 퇴근하기에 활성화 / 파란색
         */
        private void nowWorking() {
            statusButton[0].setEnabled(false);
            statusButton[1].setEnabled(true);
            statusButton[0].setBackgroundColor(getResources().getColor(R.color.colorGray));
            statusButton[1].setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        /**
         * 절찬 방콕중입니다.
         */
        private void nowHome() {
            statusButton[1].setEnabled(false);
            statusButton[0].setEnabled(true);
            statusButton[1].setBackgroundColor(getResources().getColor(R.color.colorGray));
            statusButton[0].setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    /**
     * 이 직원의 상태를 업데이트 합니다.
     */
    private void updateStatus() {
        HttpRequester requester =
                new HttpRequester(InternetConst.MY_WORKSPACE_STATUS, null, REQUEST_WORKSPACE_STATUS, backgroundHandle, this.control);
        requester.execute();

    }
}

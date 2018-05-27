package kr.saintdev.mnastaff.views.fragments.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import org.json.JSONObject;

import java.util.HashMap;

import kr.saintdev.mnastaff.R;
import kr.saintdev.mnastaff.models.datas.constants.InternetConst;
import kr.saintdev.mnastaff.models.datas.profile.MeProfile;
import kr.saintdev.mnastaff.models.datas.profile.MeProfileManager;
import kr.saintdev.mnastaff.models.tasks.BackgroundWork;
import kr.saintdev.mnastaff.models.tasks.OnBackgroundWorkListener;
import kr.saintdev.mnastaff.models.tasks.http.HttpRequester;
import kr.saintdev.mnastaff.models.tasks.http.HttpResponseObject;
import kr.saintdev.mnastaff.views.activitys.AuthActivity;
import kr.saintdev.mnastaff.views.activitys.MainActivity;
import kr.saintdev.mnastaff.views.activitys.WaitActivity;
import kr.saintdev.mnastaff.views.fragments.SuperFragment;
import kr.saintdev.mnastaff.views.windows.dialog.DialogManager;
import kr.saintdev.mnastaff.views.windows.dialog.TextEditorDialog;
import kr.saintdev.mnastaff.views.windows.dialog.clicklistener.OnYesClickListener;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @date 2018-05-26
 */

public class LoadingFragment extends SuperFragment {
    AuthActivity control = null;
    MeProfileManager profileManager = null;

    OnBackgroundWorkHandler handler = null;
    DialogManager dm = null;

    private static final int REQUEST_CREATE_ACCOUNT = 0x0;
    private static final int REQUEST_AUTO_LOGIN = 0x1;
    private static final int REQUEST_VAILD_ADMIN = 0x2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_auth_loading, container, false);
        this.control = (AuthActivity) getActivity();
        this.control.setActionBarTitle(null);

        this.profileManager = MeProfileManager.getInstance(control);

        this.dm = new DialogManager(control);
        this.dm.setOnYesButtonClickListener(new OnDialogDismissHandler(), "OK");

        this.handler = new OnBackgroundWorkHandler();

        // 카카오 로그인을 시도합니다.
        UserManagement.getInstance().requestMe(new OnKakaoLoginHandler());
        return v;
    }



    class OnKakaoLoginHandler extends MeResponseCallback {
        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            onNotSignedUp();
        }

        @Override
        public void onNotSignedUp() {
            control.switchFragment(new KakaoLoginFragment());
        }

        @Override
        public void onSuccess(UserProfile result) {
            MeProfile profile = profileManager.getProfile();

            HttpRequester requester = null;
            HashMap<String, Object> args = new HashMap<>();

            // 카카오 계정 정보를 가져온다.
            String kakaoNick = result.getNickname();
            String kakaoProfile = result.getProfileImagePath();

            if(profile == null) {
                editorDialog = new TextEditorDialog(control, "");
                editorDialog.setTag(result);
                editorDialog.setOnDismissListener(new OnAdminIdInputDialogHandler());
                editorDialog.setTitle("관리자 Public ID 입력");
                editorDialog.show();            // 운영자 이름을 받아옵니다.
            } else {
                // 자동 로그인 처리를 한다.
                args.put("kakao-nick", kakaoNick);
                args.put("kakao-profile-icon", kakaoProfile);
                requester = new HttpRequester(InternetConst.AUTO_LOGIN_ACCOUNT, args, REQUEST_AUTO_LOGIN, handler, control);
                requester.execute();
            }
        }
    }

    TextEditorDialog editorDialog = null;
    class OnAdminIdInputDialogHandler implements DialogInterface.OnDismissListener {
        @Override
        public void onDismiss(DialogInterface dialog) {
            String id = editorDialog.getData();

            if(id == null || id.length() == 0) {
                // 입력된 내용이 없다면
                editorDialog.show();
                Toast.makeText(control, "운영자 공용 ID 를 입력하세요.", Toast.LENGTH_LONG).show();
            } else {
                HashMap<String, Object> args = new HashMap<>();
                args.put("my-admin", id);
                HttpRequester requester = new HttpRequester(InternetConst.CHECK_VALID_ADMIN_ID, args, REQUEST_VAILD_ADMIN, handler, control);
                requester.execute();
            }
        }
    }

    class OnBackgroundWorkHandler extends MeResponseCallback implements OnBackgroundWorkListener {
        String mnaUUID = null;

        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            HttpResponseObject httpResp = (HttpResponseObject) worker.getResult();

            if(httpResp.isErrorOccurred()) {
                // 서버 요청 오류
                dm.setTitle("오류");
                dm.setDescription("Internal server error.\n" + httpResp.getErrorMessage());
                dm.show();
            } else {
                try {
                    if (requestCode == REQUEST_CREATE_ACCOUNT) {
                        // 가입에 성공했다면, 인증서를 만듭니다.
                        JSONObject body = httpResp.getBody();
                        this.mnaUUID = body.getString("mna-uuid");

                        UserManagement.getInstance().requestMe(this);
                    } else if (requestCode == REQUEST_AUTO_LOGIN) {
                        // 로그인 처리 결과
                        JSONObject body = httpResp.getBody();

                        // 자동 로그인 성공?
                        if (httpResp.getResponseResultCode() == InternetConst.HTTP_AUTH_ERROR) {
                            // 잘못된 인증서 입니다.
                            dm.setTitle("오류");
                            dm.setDescription("유효하지 않은 인증서 입니다!");
                            dm.show();

                            // 인증서를 제거한다.
                            profileManager.clear();
                        } else if (httpResp.getResponseResultCode() != InternetConst.HTTP_OK) {
                            // 다른 오류
                            dm.setTitle("오류");
                            dm.setDescription("알 수 없는 오류가 발생하였습니다.");
                            dm.show();
                        } else {
                            // 계정 데이터를 업데이트 합니다.
                            if (!body.getBoolean("result")) {
                                // 계정 업데이트 실패
                                Toast.makeText(control, "Can not update account", Toast.LENGTH_SHORT).show();
                            }

                            // 이 계정이 승인된 계정인지 확인합ㄴ디ㅏ
                            if(!body.getBoolean("grant")) {
                                // WaitActivity 를 실행합니다.
                                Intent waitActivity = new Intent(getActivity(), WaitActivity.class);
                                startActivity(waitActivity);

                                getActivity().finish();
                            } else {
                                gotoMainActivity();
                            }
                        }
                    } else if(requestCode == REQUEST_VAILD_ADMIN) {
                        JSONObject body = httpResp.getBody();

                        if(body.getBoolean("result")) {
                            // 유효한 ID, 가입을 진행시킨다.
                            UserProfile profile = (UserProfile) editorDialog.getTag();

                            HashMap<String, Object> args = new HashMap<>();
                            args.put("kakao-nick", profile.getNickname());
                            args.put("kakao-profile-icon", profile.getProfileImagePath());
                            args.put("X-kakao-id", profile.getId());
                            args.put("my-admin", editorDialog.getData());

                            HttpRequester requester = new HttpRequester(InternetConst.CREATE_ACCOUNT, args, REQUEST_CREATE_ACCOUNT, handler, control);
                            requester.execute();        // 가입을 진행 시킨다.
                        } else {
                            // 잘못된 ID 다시 입력!
                            Toast.makeText(control, "잘못된 id 값 입니다!", Toast.LENGTH_LONG).show();
                            editorDialog.show();
                        }
                    }
                } catch (Exception ex) {
                    dm.setTitle("Fatal error!");
                    dm.setDescription("An error occurred.\n" + ex.getMessage());
                    dm.show();
                }
            }
        }

        @Override
        public void onFailed(int requestCode, Exception ex) {
            // 실패했습니다.
            String title;
            if(requestCode == REQUEST_CREATE_ACCOUNT) {
                // 회원가입 처리 결과
                title = "계정 생성에 실패했습니다!";
            } else  if(requestCode == REQUEST_AUTO_LOGIN) {
                // 로그인 처리 결과
                title = "자동 로그인에 실패했습니다!";
            } else {
                title ="Unknwon request!";
            }

            dm.setTitle("Fatal error");
            dm.setDescription(title + ex.getMessage());
            dm.show();

            ex.printStackTrace();
        }

        /*
            카카오 세션을 가져와서 인증서를 만듭니다.
         */
        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            onNotSignedUp();
        }

        @Override
        public void onNotSignedUp() {
            control.switchFragment(new KakaoLoginFragment());
        }

        @Override
        public void onSuccess(UserProfile result) {
            // 여기서 가입 인증서를 생성합니다.
            MeProfile profile = new MeProfile(
                    result.getId()+"",
                    result.getNickname(),
                    result.getProfileImagePath(),
                    mnaUUID
            );
            profileManager.setProfile(profile);

            // 첫 사용자 입니다.
            // 여기서 첫 사용자에게 할 행위를 기입합니다
            gotoMainActivity();
        }
    }

    class OnDialogDismissHandler implements OnYesClickListener {
        @Override
        public void onClick(DialogInterface dialog) {
            dialog.dismiss();
            control.finish();
        }
    }

    private void gotoMainActivity() {
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                startActivity(new Intent(control, MainActivity.class));
                control.finish();
                return true;
            }
        });
        handler.sendEmptyMessageDelayed(0, 1000);


    }
}

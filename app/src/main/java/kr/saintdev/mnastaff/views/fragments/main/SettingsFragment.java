package kr.saintdev.mnastaff.views.fragments.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.UserManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.saintdev.mnastaff.R;
import kr.saintdev.mnastaff.models.datas.profile.MeProfile;
import kr.saintdev.mnastaff.models.datas.profile.MeProfileManager;
import kr.saintdev.mnastaff.models.tasks.BackgroundWork;
import kr.saintdev.mnastaff.models.tasks.OnBackgroundWorkListener;
import kr.saintdev.mnastaff.models.tasks.downloader.ImageDownloader;
import kr.saintdev.mnastaff.views.activitys.AuthActivity;
import kr.saintdev.mnastaff.views.activitys.MainActivity;
import kr.saintdev.mnastaff.views.fragments.SuperFragment;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-06-01
 */

public class SettingsFragment extends SuperFragment {
    private TextView nameView = null;       // 이름 띄우기
    private TextView kakaoId = null;        // 카카오 ID
    private CircleImageView profileView = null;
    private Button logoutButton = null;     // 로그아웃 버튼
    MeProfileManager profileManager = null;

    MainActivity control = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_main_settings, container, false);
        this.control = (MainActivity) getActivity();

        this.nameView = v.findViewById(R.id.main_settings_content_nickname_view);
        this.kakaoId = v.findViewById(R.id.main_settings_content_kakaoid_view);
        this.logoutButton = v.findViewById(R.id.main_settings_logout);
        this.profileView = v.findViewById(R.id.main_settings_profile_icon);
        this.profileManager = MeProfileManager.getInstance(control);

        this.logoutButton.setOnClickListener(new OnLogoutClickHandler());

        MeProfile profile = profileManager.getProfile();
        this.nameView.setText(profile.getKakaoNick());
        this.kakaoId.setText(profile.getKakaoID());

        ImageDownloader downloader = new ImageDownloader(profile.getKakaoProfileIcon(), 0x0, new OnBackgroundWorkListener() {
            @Override
            public void onSuccess(int requestCode, BackgroundWork worker) {
                Bitmap profile = (Bitmap) worker.getResult();

                profileView.setImageBitmap(profile);
            }

            @Override
            public void onFailed(int requestCode, Exception ex) {
                Toast.makeText(control, "프로필 로드 실패!", Toast.LENGTH_LONG).show();
            }
        });
        downloader.execute();
        return v;
    }


    /**
     * 로그아웃을 눌렀을 때 핸들링
     */
    class OnLogoutClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {

                }
            });

            profileManager.clear();

            control.finish();
            Intent intent = new Intent(control, AuthActivity.class);
            startActivity(intent);
        }
    }
}

package kr.saintdev.mnastaff.views.activitys;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import kr.saintdev.mnastaff.R;
import kr.saintdev.mnastaff.views.adapters.MainActivityAdapter;
import kr.saintdev.mnastaff.views.fragments.SuperFragment;
import kr.saintdev.mnastaff.views.fragments.main.AlarmFragment;
import kr.saintdev.mnastaff.views.fragments.main.HomeFragment;
import kr.saintdev.mnastaff.views.fragments.main.SettingsFragment;
import kr.saintdev.mnastaff.views.fragments.main.WorklogFragment;

public class MainActivity extends AppCompatActivity {
    ViewPager contentPager = null;
    MainActivityAdapter viewAdapter = null;
    ImageButton[] buttons = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   // 메인 화면을 띄웁니다.

        this.contentPager = findViewById(R.id.main_container);
        this.buttons = new ImageButton[] {
                findViewById(R.id.main_nav_home),     // 홈 화면
                findViewById(R.id.main_nav_worklog),  // 근무 기록
                findViewById(R.id.main_nav_reqjoin),  // 입사 요청
                findViewById(R.id.main_nav_settings) // 설정
        };

        // Fragment 를 생성합니다.
        SuperFragment[] fragments = new SuperFragment[] {
                new HomeFragment(),
                new WorklogFragment(),
                new AlarmFragment(),
                new SettingsFragment()
        };

        // adapter 을 생성합니다.
        this.viewAdapter = new MainActivityAdapter(getSupportFragmentManager(), fragments);
        this.contentPager.setAdapter(this.viewAdapter);

        // 리스너 처리
        OnButtonClickHandler handler = new OnButtonClickHandler();
        for(ImageButton b : this.buttons) {
            b.setOnClickListener(handler);
        }
    }

    class OnButtonClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.main_nav_home:
                    contentPager.setCurrentItem(0);
                    break;
                case R.id.main_nav_worklog:
                    contentPager.setCurrentItem(1);
                    break;
                case R.id.main_nav_reqjoin:
                    contentPager.setCurrentItem(2);
                    break;
                case R.id.main_nav_settings:
                    contentPager.setCurrentItem(3);
                    break;
            }
        }
    }
}

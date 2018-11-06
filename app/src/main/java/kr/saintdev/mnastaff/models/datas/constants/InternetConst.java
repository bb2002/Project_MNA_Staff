package kr.saintdev.mnastaff.models.datas.constants;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-16
 * @UPDATE 2018-11-06 END POINT UPDATED.
 */

public interface InternetConst {
    int HTTP_OK = 200;
    int HTTP_AUTH_ERROR = 400;

    String SERVER_HOST = "http://api.saintdev.kr/~mna/staff/";

    String CREATE_ACCOUNT = SERVER_HOST + "account/join.php";
    String AUTO_LOGIN_ACCOUNT = SERVER_HOST + "account/auto-login.php";
    String CHECK_VALID_ADMIN_ID = SERVER_HOST + "account/valid-admin.php";
    String REQUGET_GONE = SERVER_HOST + "account/gone.php";

    String MY_WORKSPACE_STATUS = SERVER_HOST + "work/my-workspace.php";
    String COMMUTE_ME = SERVER_HOST + "work/commute.php";
    String MY_WORKLOG = SERVER_HOST + "work/my-worklog.php";

    String ALARM_MY_ALARMS = SERVER_HOST + "alarm/my-alarm.php";
    String DELETE_ALARM = "http://api.saintdev.kr/~mna/admin/alarm/delete-alarm.php";
}

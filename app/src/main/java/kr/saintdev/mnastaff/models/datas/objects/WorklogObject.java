package kr.saintdev.mnastaff.models.datas.objects;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-06-01
 */

public class WorklogObject {
    private String workspaceUUID = null;        // 작업장 고유 UUID
    private String signTime = null;             // 계약서상의 근무 시간
    private String workDate = null;             // 근무한 날짜
    private long workStart = 0;                 // 근무 시작 시간 (timestamp second)
    private long workStop = 0;                  // 근무 종료 시간 (timestamp second)
    private int money = 0;                      // 10분당 급여
    private int admitTime = 0;                  // 실제로 인정된 시간
    private boolean isNowWorking = false;       // 근무중인가?

    public WorklogObject(String workspaceUUID, String signTime, String workDate, long workStart, long workStop, int money, int admitTime, boolean isNowWorking) {
        this.workspaceUUID = workspaceUUID;
        this.signTime = signTime;
        this.workDate = workDate;
        this.workStart = workStart;
        this.workStop = workStop;
        this.money = money;
        this.admitTime = admitTime;
        this.isNowWorking = isNowWorking;
    }

    public String getWorkspaceUUID() {
        return workspaceUUID;
    }

    public String getSignTime() {
        return signTime;
    }

    public String getWorkDate() {
        return workDate;
    }

    public long getWorkStart() {
        return workStart;
    }

    public long getWorkStop() {
        return workStop;
    }

    public int getMoney() {
        return money;
    }

    public int getAdmitTime() {
        return admitTime;
    }

    public boolean isNowWorking() {
        return isNowWorking;
    }
}

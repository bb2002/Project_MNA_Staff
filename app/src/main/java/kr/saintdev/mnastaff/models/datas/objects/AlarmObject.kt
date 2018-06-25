package kr.saintdev.mnastaff.models.datas.objects

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 * @Date 2018-06-26
 */
data class AlarmObject(
        val alarmId: Int,
        val alarmTitle: String,
        val alarmContent: String,
        val alarmTarget: String,
        val alarmSender: String,
        val alarmType: String,
        val alarmCreated: String
)
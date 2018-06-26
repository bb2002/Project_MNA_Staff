package kr.saintdev.mnastaff.views.fragments.main

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import kr.saintdev.mnastaff.R
import kr.saintdev.mnastaff.models.datas.constants.InternetConst
import kr.saintdev.mnastaff.models.datas.objects.AlarmObject
import kr.saintdev.mnastaff.models.tasks.BackgroundWork
import kr.saintdev.mnastaff.models.tasks.OnBackgroundWorkListener
import kr.saintdev.mnastaff.models.tasks.http.HttpRequester
import kr.saintdev.mnastaff.models.tasks.http.HttpResponseObject
import kr.saintdev.mnastaff.views.adapters.AlarmAdapter
import kr.saintdev.mnastaff.views.fragments.SuperFragment
import kr.saintdev.mnastaff.views.windows.dialog.DialogManager
import kr.saintdev.mnastaff.views.windows.dialog.clicklistener.OnYesClickListener


/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 * @Date 2018-06-26
 */
class AlarmFragment : SuperFragment() {
    private val REQUEST_MY_ALARM = 0x0
    private val REQUEST_DELETE_ALARM = 0x1

    var alarmList: ListView? = null
    var alarmEmptyView: TextView? = null
    var alarmAdapter = AlarmAdapter()
    var dm: DialogManager? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragmn_alarm, container, false)
        this.alarmList = view.findViewById(R.id.staff_alarm_list)
        this.alarmEmptyView = view.findViewById(R.id.staff_alarm_empty)
        this.dm = DialogManager(activity)

        this.alarmList?.adapter = this.alarmAdapter
        this.alarmAdapter.setDeleteClickHandler(OnDeleteClickListener())

        return view
    }

    override fun onResume() {
        super.onResume()
        updateAlarm()
    }

    inner class OnBackgroundCallback : OnBackgroundWorkListener, OnYesClickListener {
        override fun onSuccess(requestCode: Int, worker: BackgroundWork<*>?) {
            try {
                val response = worker?.result

                when(requestCode) {
                    REQUEST_MY_ALARM->updateAlarmArray(
                            if (response is HttpResponseObject)
                                response
                            else
                                throw Exception("Casting error!"))           //
                    REQUEST_DELETE_ALARM->updateAlarm()
                }
            } catch(ex: Exception) {
                onFailed(requestCode, ex)
            }
        }

        override fun onFailed(requestCode: Int, ex: Exception) {
            dm?.setTitle("Exception!")
            dm?.setDescription("excetpion.\n${ex.message}")
            dm?.setOnYesButtonClickListener(this, "OK")
            dm?.show()
        }

        override fun onClick(dialog: DialogInterface?) {
            dialog?.dismiss()
        }

        private fun updateAlarmArray(response: HttpResponseObject) {
            if (response.isErrorOccurred) {
                dm?.setTitle("An error occurred!")
                dm?.setDescription("알림 목록을 불러올 수 없습니다.\n${response.errorMessage}")
                dm?.setOnYesButtonClickListener(this, "OK")
                dm?.show()
            } else {
                val body = response.body

                if (body.getInt("length") == 0) {
                    // 아무 알림도 없습니다.
                    setAlarmEmptyView(true)
                } else {
                    setAlarmEmptyView(false)
                    // 알림을 보여줍니다.
                    val myAlarmArray = body.getJSONArray("alarms")

                    for (i in 0..myAlarmArray.length()-1) {
                        val jsonObj = myAlarmArray.getJSONObject(i)

                        alarmAdapter.addAlarmItem(AlarmObject(
                                jsonObj.getInt("_id"),
                                jsonObj.getString("alarm-title"),
                                jsonObj.getString("alarm-content"),
                                jsonObj.getString("alarm-target-uuid"),
                                jsonObj.getString("alarm-sender-uuid"),
                                jsonObj.getString("alarm-type"),
                                jsonObj.getString("created")
                        ))
                    }
                }

                alarmAdapter.notifyDataSetChanged()
            }
        }
    }

    inner class OnDeleteClickListener : View.OnClickListener {
        override fun onClick(v: View) {

            val alarmObj = v.tag
            if(alarmObj is AlarmObject) {
                val args: HashMap<String, Any> = hashMapOf("alarm-id" to alarmObj.alarmId)

                val requester = HttpRequester(
                        InternetConst.DELETE_ALARM,
                        args,
                        REQUEST_DELETE_ALARM,
                        OnBackgroundCallback(),
                        context
                )
                requester.execute()
            }
        }
    }


    fun updateAlarm() {
        alarmAdapter.clear()

        // 서버에 알림 목록을 요청한다.
        val httpRequester = HttpRequester(InternetConst.ALARM_MY_ALARMS, null, REQUEST_MY_ALARM, OnBackgroundCallback(), context)
        httpRequester.execute()
    }

    fun setAlarmEmptyView(b: Boolean)
            = if(b) alarmEmptyView?.visibility = View.VISIBLE else alarmEmptyView?.visibility = View.INVISIBLE
}
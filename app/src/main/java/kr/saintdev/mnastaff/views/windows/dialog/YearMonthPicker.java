package kr.saintdev.mnastaff.views.windows.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import java.util.Calendar;

import kr.saintdev.mnastaff.R;

public class YearMonthPicker extends Dialog {
    private static final int MAX_YEAR = 2099;
    private static final int MIN_YEAR = 1980;

    private DatePickerDialog.OnDateSetListener listener = null;

    private NumberPicker yearPicker = null;
    private NumberPicker monthPicker = null;
    private Button cancelButton = null;
    private Button confirmButton = null;

    public YearMonthPicker(@NonNull Context context, DatePickerDialog.OnDateSetListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_year_month_picker);

        this.yearPicker = findViewById(R.id.ym_picker_year);
        this.monthPicker = findViewById(R.id.ym_picker_month);
        this.cancelButton = findViewById(R.id.ym_picker_cancel);
        this.confirmButton = findViewById(R.id.ym_picker_confirm);

        OnButtonClickHandler handler = new OnButtonClickHandler();
        this.cancelButton.setOnClickListener(handler);
        this.confirmButton.setOnClickListener(handler);

        Calendar cal = Calendar.getInstance();

        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(cal.get(Calendar.YEAR));
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(cal.get(Calendar.MONTH) + 1);
    }

    /**
     * 클릭 이벤트 처리
     */
    class OnButtonClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.ym_picker_cancel:
                    dismiss();
                    break;
                case R.id.ym_picker_confirm:
                    if(listener != null) {
                        listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
                    }
                    dismiss();
                    break;
            }
        }
    }
}

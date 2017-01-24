package stark.a.is.zhang.criminalintentapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import stark.a.is.zhang.criminalintentapp.R;

public class TimePickerFragment extends AppCompatDialogFragment {
    private static final String ARG_TIME = "time";

    private static final String EXTRA_TIME =
            "stark.a.is.zhang.criminalintentapp.time";

    private TimePicker mTimePicker;

    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_TIME);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_time_picker);
        mTimePicker.setIs24HourView(true);
        if (Build.VERSION.SDK_INT >= 23) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        }

        Button button = (Button)v.findViewById(R.id.dialog_time_button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        int hour = mTimePicker.getHour();
                        int minute = mTimePicker.getMinute();
                        Date date = new GregorianCalendar(
                                year, month, day, hour, minute).getTime();
                        sendResult(Activity.RESULT_OK, date);
                        getFragmentManager()
                                .beginTransaction()
                                .remove(TimePickerFragment.this)
                                .commit();
                    }
                }
            });
        }

        return v;
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);

        getTargetFragment().onActivityResult(
                getTargetRequestCode(), resultCode, intent);
    }

    public static Date getDateFromIntent(Intent intent) {
        return (Date)intent.getSerializableExtra(EXTRA_TIME);
    }
}

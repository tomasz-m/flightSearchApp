package info.tomaszminiach.flightsapptest.helper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private static final String MIN_DATE_KEY = "MIN";

    public static void start(long minDate, String tag, FragmentManager fragmentManager) {
        DatePickerFragment newFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putLong(MIN_DATE_KEY, minDate);
        newFragment.setArguments(args);
        newFragment.show(fragmentManager, tag);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        long minDate = 0;
        try {
            minDate = getArguments().getLong(MIN_DATE_KEY);
        } catch (NullPointerException ignored) {
        }

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        if (minDate > 0)
            dialog.getDatePicker().setMinDate(minDate);
        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year,month,day);
        //I know this dependency to the SimpleSingleObserver is not nice, would be better to
        SimpleSingleObserver.sendEvent(getTag(),c);
    }
}
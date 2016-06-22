package info.tomaszminiach.flightsapptest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import info.tomaszminiach.flightsapptest.helper.DateHelper;
import info.tomaszminiach.flightsapptest.helper.DatePickerFragment;
import info.tomaszminiach.flightsapptest.helper.SimpleSingleObserver;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SimpleSingleObserver.SimpleObserver {

    private static final String TAG_START_BUTTON = "START";
    private static final String TAG_END_BUTTON = "END";

    private Calendar fromDateCalendar, toDateCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonDateFrom).setOnClickListener(this);
        findViewById(R.id.buttonDateTo).setOnClickListener(this);
        findViewById(R.id.buttonSearch).setOnClickListener(this);

        fromDateCalendar = Calendar.getInstance();
        toDateCalendar = Calendar.getInstance();
        if(savedInstanceState!=null){
            long fromMilliseconds = savedInstanceState.getLong(TAG_START_BUTTON);
            long toMilliseconds = savedInstanceState.getLong(TAG_END_BUTTON);
            if(fromMilliseconds>0)
                fromDateCalendar.setTimeInMillis(fromMilliseconds);
            if(toMilliseconds>0)
                toDateCalendar.setTimeInMillis(toMilliseconds);
        }

        refreshView();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(TAG_START_BUTTON, fromDateCalendar.getTimeInMillis());
        savedInstanceState.putLong(TAG_END_BUTTON, toDateCalendar.getTimeInMillis());
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        SimpleSingleObserver.subscribe(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SimpleSingleObserver.unsubscribe();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonDateFrom:
                DatePickerFragment.start(0,TAG_START_BUTTON,getFragmentManager());
                break;
            case R.id.buttonDateTo:
                DatePickerFragment.start(0,TAG_END_BUTTON,getFragmentManager());
                break;
            case R.id.buttonSearch:
                EditText editTextFrom = (EditText)findViewById( R.id.editTextFrom );
                EditText editTextTo = (EditText)findViewById( R.id.editTextTo );
                if(editTextFrom==null || editTextTo==null)
                    return;
                boolean completed=true;
                if(editTextFrom.length()<3) {
                    editTextFrom.setError(getString(R.string.errorAirportCode));
                    completed=false;
                }
                if(editTextTo.length()<3) {
                    editTextTo.setError(getString(R.string.errorAirportCode));
                    completed=false;
                }
                if(!completed) {
                    Toast.makeText(this, R.string.notCompleted,Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
        }

    }

    private void refreshView(){
        ((Button)findViewById(R.id.buttonDateFrom)).setText(
                DateHelper.formatToDisplay(fromDateCalendar)
        );
        ((Button)findViewById(R.id.buttonDateTo)).setText(
                DateHelper.formatToDisplay(toDateCalendar)
        );
    }

    @Override
    public void onEvent(String eventTag, Object o) {
        if(TAG_START_BUTTON.equals(eventTag)){
            fromDateCalendar = (Calendar) o;
            refreshView();
        }else if(TAG_END_BUTTON.equals(eventTag)){
            toDateCalendar = (Calendar) o;
            refreshView();
        }
    }
}

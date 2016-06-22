package info.tomaszminiach.flightsapptest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import info.tomaszminiach.flightsapptest.data.Flights;
import info.tomaszminiach.flightsapptest.helper.DateHelper;
import info.tomaszminiach.flightsapptest.helper.DatePickerFragment;
import info.tomaszminiach.flightsapptest.helper.SimpleSingleObserver;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

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

    public static class FlightsAdapter extends RealmBaseAdapter<Flights> {

        private LayoutInflater mInflater;

        /**
         * ViewHolder class for layout.<br />
         * <br />
         * Auto-created on 2016-06-22 17:19:07 by Android Layout Finder
         * (http://www.buzzingandroid.com/tools/android-layout-finder)
         */
        private static class ViewHolder {
            public final LinearLayout rootView;
            public final TextView textTimeFrom;
            public final TextView textTimeTo;
            public final TextView textPrice;

            private ViewHolder(LinearLayout rootView, TextView textTimeFrom, TextView textTimeTo, TextView textPrice) {
                this.rootView = rootView;
                this.textTimeFrom = textTimeFrom;
                this.textTimeTo = textTimeTo;
                this.textPrice = textPrice;
            }

            public static ViewHolder create(LinearLayout rootView) {
                TextView textTimeFrom = (TextView)rootView.findViewById( R.id.textTimeFrom );
                TextView textTimeTo = (TextView)rootView.findViewById( R.id.textTimeTo );
                TextView textPrice = (TextView)rootView.findViewById( R.id.textPrice );
                return new ViewHolder( rootView, textTimeFrom, textTimeTo, textPrice );
            }
        }

        public FlightsAdapter(Context context, RealmResults<Flights> data, boolean automaticUpdate, LayoutInflater mInflater) {
            super(context, data, automaticUpdate);
            this.mInflater = mInflater;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder vh;
            if ( convertView == null ) {
                View view = mInflater.inflate( R.layout.item_flight, parent, false );
                vh = ViewHolder.create( (LinearLayout)view );
                view.setTag( vh );
            } else {
                vh = (ViewHolder)convertView.getTag();
            }

            Flights item = getItem( position );

            vh.textTimeFrom.setText(DateHelper.formatToDisplay(item.getDateFrom()));
            vh.textTimeTo.setText(DateHelper.formatToDisplay(item.getDateTo()));
            String priceString = item.getCurrency()+ String.format("%.02f", item.getPrice());
            vh.textPrice.setText(priceString);

            return vh.rootView;
        }
    }


}

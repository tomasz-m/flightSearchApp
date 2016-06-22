package info.tomaszminiach.flightsapptest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import info.tomaszminiach.flightsapptest.data.DataProvider;
import info.tomaszminiach.flightsapptest.data.DataRoot;
import info.tomaszminiach.flightsapptest.data.Flights;
import info.tomaszminiach.flightsapptest.helper.DateHelper;
import info.tomaszminiach.flightsapptest.helper.DatePickerFragment;
import info.tomaszminiach.flightsapptest.helper.SimpleSingleObserver;
import info.tomaszminiach.flightsapptest.sync.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

                RestClient restClient = new RestClient();
                RestClient.FlightsService webInterface = restClient.getWebInterface();
                String fromDate = DateHelper.formatForServer(fromDateCalendar);
                String toDate = DateHelper.formatForServer(toDateCalendar);
                Call<DataRoot> flights = webInterface.getFlights("DUB", "DUB", fromDate, toDate);
                flights.enqueue(new Callback<DataRoot>() {
                    @Override
                    public void onResponse(Call<DataRoot> call, Response<DataRoot> response) {
                        for(Flights f : response.body().getFlights()){
                            Log.d(">>>flight","price "+f.getPrice());
                        }
                        DataProvider.clearFlights();
                        DataProvider.setFlights(response.body().getFlights());
                        SimpleSingleObserver.sendEvent("NEW_DATA",null);
                    }

                    @Override
                    public void onFailure(Call<DataRoot> call, Throwable t) {

                    }
                });

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

    private void setData(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        MyAdapter myAdapter = new MyAdapter(DataProvider.getFlights());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public void onEvent(String eventTag, Object o) {
        if(TAG_START_BUTTON.equals(eventTag)){
            fromDateCalendar = (Calendar) o;
            refreshView();
        }else if(TAG_END_BUTTON.equals(eventTag)){
            toDateCalendar = (Calendar) o;
            refreshView();
        }else if("NEW_DATA".equals(eventTag)){
            setData();
        }
    }


    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Flights> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder

        public static class ViewHolder  extends RecyclerView.ViewHolder {
            public final View rootView;
            public final TextView textTimeFrom;
            public final TextView textTimeTo;
            public final TextView textPrice;

            private ViewHolder(View rootView) {
                super(rootView);
                this.rootView = rootView;
                this.textTimeFrom = (TextView)rootView.findViewById( R.id.textTimeFrom );
                this.textTimeTo = (TextView)rootView.findViewById( R.id.textTimeTo );
                this.textPrice = (TextView)rootView.findViewById( R.id.textPrice );
            }


        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(List<Flights> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_flight, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder vh, int position) {
            Flights item = mDataset.get(position);
            vh.textTimeFrom.setText(DateHelper.formatToDisplay(item.getDateFrom()));
            vh.textTimeTo.setText(DateHelper.formatToDisplay(item.getDateTo()));
            String priceString = item.getCurrency()+ String.format("%.02f", item.getPrice());
            vh.textPrice.setText(priceString);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }



//
//    public static class FlightsAdapter extends RealmBaseAdapter<Flights> {
//
//        private LayoutInflater mInflater;
//
//        /**
//         * ViewHolder class for layout.<br />
//         * <br />
//         * Auto-created on 2016-06-22 17:19:07 by Android Layout Finder
//         * (http://www.buzzingandroid.com/tools/android-layout-finder)
//         */
//        private static class ViewHolder {
//            public final LinearLayout rootView;
//            public final TextView textTimeFrom;
//            public final TextView textTimeTo;
//            public final TextView textPrice;
//
//            private ViewHolder(LinearLayout rootView, TextView textTimeFrom, TextView textTimeTo, TextView textPrice) {
//                this.rootView = rootView;
//                this.textTimeFrom = textTimeFrom;
//                this.textTimeTo = textTimeTo;
//                this.textPrice = textPrice;
//            }
//
//            public static ViewHolder create(LinearLayout rootView) {
//                TextView textTimeFrom = (TextView)rootView.findViewById( R.id.textTimeFrom );
//                TextView textTimeTo = (TextView)rootView.findViewById( R.id.textTimeTo );
//                TextView textPrice = (TextView)rootView.findViewById( R.id.textPrice );
//                return new ViewHolder( rootView, textTimeFrom, textTimeTo, textPrice );
//            }
//        }
//
//        public FlightsAdapter(Context context, RealmResults<Flights> data, boolean automaticUpdate, LayoutInflater mInflater) {
//            super(context, data, automaticUpdate);
//            this.mInflater = mInflater;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            final ViewHolder vh;
//            if ( convertView == null ) {
//                View view = mInflater.inflate( R.layout.item_flight, parent, false );
//                vh = ViewHolder.create( (LinearLayout)view );
//                view.setTag( vh );
//            } else {
//                vh = (ViewHolder)convertView.getTag();
//            }
//
//            Flights item = getItem( position );
//
//            vh.textTimeFrom.setText(DateHelper.formatToDisplay(item.getDateFrom()));
//            vh.textTimeTo.setText(DateHelper.formatToDisplay(item.getDateTo()));
//            String priceString = item.getCurrency()+ String.format("%.02f", item.getPrice());
//            vh.textPrice.setText(priceString);
//
//            return vh.rootView;
//        }
//    }
//

}

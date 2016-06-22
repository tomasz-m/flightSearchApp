package info.tomaszminiach.flightsapptest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private static final String TAG_NEW_DATA = "NEW_DATA";

    private Calendar fromDateCalendar, toDateCalendar;

    private View searchButton, progressBar;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonDateFrom).setOnClickListener(this);
        findViewById(R.id.buttonDateTo).setOnClickListener(this);
        searchButton = findViewById(R.id.buttonSearch);
        searchButton.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        fromDateCalendar = Calendar.getInstance();
        toDateCalendar = Calendar.getInstance();
        if (savedInstanceState != null) {
            long fromMilliseconds = savedInstanceState.getLong(TAG_START_BUTTON);
            long toMilliseconds = savedInstanceState.getLong(TAG_END_BUTTON);
            if (fromMilliseconds > 0)
                fromDateCalendar.setTimeInMillis(fromMilliseconds);
            if (toMilliseconds > 0)
                toDateCalendar.setTimeInMillis(toMilliseconds);
        }

        refreshButtons();
        displayData();
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
        //subscribe to SimpleSingleObserver
        SimpleSingleObserver.subscribe(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SimpleSingleObserver.unsubscribe();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonDateFrom:
                DatePickerFragment.start(0, TAG_START_BUTTON, getFragmentManager());
                break;
            case R.id.buttonDateTo:
                DatePickerFragment.start(0, TAG_END_BUTTON, getFragmentManager());
                break;
            case R.id.buttonSearch:
                search();
                break;
        }

    }

    private void search() {
        EditText editTextFrom = (EditText) findViewById(R.id.editTextFrom);
        EditText editTextTo = (EditText) findViewById(R.id.editTextTo);
        if (editTextFrom == null || editTextTo == null)
            return;
        boolean completed = true;
        if (editTextFrom.length() < 3) {
            editTextFrom.setError(getString(R.string.errorAirportCode));
            completed = false;
        }
        if (editTextTo.length() < 3) {
            editTextTo.setError(getString(R.string.errorAirportCode));
            completed = false;
        }
        if (fromDateCalendar == null || toDateCalendar == null || fromDateCalendar.after(toDateCalendar))
            completed = false;
        if (!completed) {
            Toast.makeText(this, R.string.notCompleted, Toast.LENGTH_SHORT).show();
            return;
        }

        RestClient restClient = new RestClient();
        RestClient.FlightsService webInterface = restClient.getWebInterface();
        String fromDate = DateHelper.formatForServer(fromDateCalendar);
        String toDate = DateHelper.formatForServer(toDateCalendar);
        Call<DataRoot> flights = webInterface.getFlights(
                editTextFrom.getText().toString(),
                editTextTo.getText().toString(), fromDate, toDate);

        DataProvider.clearFlights();
        DataProvider.setSTATUS(DataProvider.STATUS_PROCESSING);
        SimpleSingleObserver.sendEvent(TAG_NEW_DATA, null);
        flights.enqueue(new Callback<DataRoot>() {
            @Override
            public void onResponse(Call<DataRoot> call, Response<DataRoot> response) {
                DataProvider.setFlights(response.body().getFlights());
                DataProvider.setSTATUS(DataProvider.STATUS_READY);
                SimpleSingleObserver.sendEvent(TAG_NEW_DATA, null);
            }

            @Override
            public void onFailure(Call<DataRoot> call, Throwable t) {
                DataProvider.setSTATUS(DataProvider.STATUS_ERROR);
                SimpleSingleObserver.sendEvent(TAG_NEW_DATA, null);
            }
        });
    }

    /**
     * just to bind displayed date with one stored in private fields of this activity
     */
    private void refreshButtons() {
        View errorView = findViewById(R.id.dateError);
        if (fromDateCalendar != null && toDateCalendar != null && fromDateCalendar.after(toDateCalendar))
            errorView.setVisibility(View.VISIBLE);
        else
            errorView.setVisibility(View.INVISIBLE);

        ((Button) findViewById(R.id.buttonDateFrom)).setText(
                DateHelper.formatToDisplay(fromDateCalendar)
        );
        ((Button) findViewById(R.id.buttonDateTo)).setText(
                DateHelper.formatToDisplay(toDateCalendar)
        );
    }

    /**
     * displays flights on recycler view and checks status from custom data provider
     * displays progress bar when data provider says that data are processed
     */
    private void displayData() {
        if (DataProvider.getSTATUS() == DataProvider.STATUS_PROCESSING) {
            searchButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(null);
        } else if (DataProvider.getSTATUS() == DataProvider.STATUS_ERROR) {
            searchButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, R.string.connectionProblem, Toast.LENGTH_SHORT).show();
            recyclerView.setAdapter(null);
        } else {
            searchButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            if (DataProvider.getFlights() == null) {
                recyclerView.setAdapter(null);
                return;
            }
            MyAdapter myAdapter = new MyAdapter(DataProvider.getFlights());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(myAdapter);
        }
    }

    /**
     * Callback from my little custom event bus class SimpleSingleObserver
     * @param eventTag
     * @param o
     */
    @Override
    public void onEvent(String eventTag, Object o) {

        if (TAG_START_BUTTON.equals(eventTag)) {
            fromDateCalendar = (Calendar) o;
            refreshButtons();
        } else if (TAG_END_BUTTON.equals(eventTag)) {
            toDateCalendar = (Calendar) o;
            refreshButtons();
        } else if (TAG_NEW_DATA.equals(eventTag)) {
            displayData();
        }
    }


    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Flights> mDataset;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View rootView;
            public final TextView textTimeFrom;
            public final TextView textTimeTo;
            public final TextView textPrice;

            private ViewHolder(View rootView) {
                super(rootView);
                this.rootView = rootView;
                this.textTimeFrom = (TextView) rootView.findViewById(R.id.textTimeFrom);
                this.textTimeTo = (TextView) rootView.findViewById(R.id.textTimeTo);
                this.textPrice = (TextView) rootView.findViewById(R.id.textPrice);
            }
        }

        public MyAdapter(List<Flights> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_flight, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder vh, int position) {
            Flights item = mDataset.get(position);
            vh.textTimeFrom.setText(DateHelper.formatToDisplay(item.getDateFrom()));
            vh.textTimeTo.setText(DateHelper.formatToDisplay(item.getDateTo()));
            String priceString = item.getCurrency() + String.format("%.02f", item.getPrice());
            vh.textPrice.setText(priceString);

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }


}

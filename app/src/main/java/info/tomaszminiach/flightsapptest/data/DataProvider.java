package info.tomaszminiach.flightsapptest.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * There must be some place to keep date so they are saved when orientation changes
 * Initially I used realm database but using it with recycler view was quite complicated.
 * Data are saved in memory in static list but we don't expect more than 15 records so that's not bad solution
 */
public class DataProvider {

    @IntDef({STATUS_PROCESSING, STATUS_READY, STATUS_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AvailableStatus {}
    public static final int STATUS_ERROR=2;
    public static final int STATUS_PROCESSING=1;
    public static final int STATUS_READY=0;

    private static List<Flights> flights;
    private static int STATUS;

    public static List<Flights> getFlights() {
        return flights;
    }

    public static void clearFlights() {
        DataProvider.flights = null;
    }

    public static void setFlights(List<Flights> flights) {
        DataProvider.flights = flights;
    }

    @AvailableStatus
    public static int getSTATUS() {
        return STATUS;
    }

    public static void setSTATUS(@AvailableStatus int STATUS) {
        DataProvider.STATUS = STATUS;
    }
}

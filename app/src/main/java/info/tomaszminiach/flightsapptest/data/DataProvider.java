package info.tomaszminiach.flightsapptest.data;

import java.util.List;

/**
 * Created by tomaszminiach on 22/06/16.
 */
public class DataProvider {

    private static List<Flights> flights;


    public static List<Flights> getFlights() {
        return flights;
    }

    public static void clearFlights() {
        DataProvider.flights = null;
    }

    public static void setFlights(List<Flights> flights) {
        DataProvider.flights = flights;
    }
}

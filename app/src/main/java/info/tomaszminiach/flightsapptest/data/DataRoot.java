package info.tomaszminiach.flightsapptest.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataRoot{
    public static final String FLIGHTS = "flights";

    @SerializedName("flights")
    private List<Flights> flights;

    public DataRoot(){ }

    public void setFlights(List<Flights> flights){
        this.flights = flights;
    }
    public List<Flights> getFlights(){
        return this.flights;
    }
}


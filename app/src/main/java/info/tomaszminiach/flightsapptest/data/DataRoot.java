package info.tomaszminiach.flightsapptest.data;

import com.google.gson.annotations.SerializedName;
import io.realm.RealmList;
import io.realm.RealmObject;

public class DataRoot extends RealmObject {
    public static final String FLIGHTS = "flights";

    @SerializedName("flights")
    private RealmList<Flights> flights;

    public DataRoot(){ }

    public void setFlights(RealmList<Flights> flights){
        this.flights = flights;
    }
    public RealmList<Flights> getFlights(){
        return this.flights;
    }
}


package info.tomaszminiach.flightsapptest.data;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Flights {
    public static final String DATE_FROM = "dateFrom";
    public static final String DATE_TO = "dateTo";
    public static final String CURRENCY = "currency";
    public static final String PRICE = "price";

    @SerializedName("dateFrom")
    private Date dateFrom;
    @SerializedName("dateTo")
    private Date dateTo;
    @SerializedName("currency")
    private String currency;
    @SerializedName("price")
    private float price;

    public Flights(){ }

    public void setDateFrom(Date dateFrom){
        this.dateFrom = dateFrom;
    }
    public Date getDateFrom(){
        return this.dateFrom;
    }
    public void setDateTo(Date dateTo){
        this.dateTo = dateTo;
    }
    public Date getDateTo(){
        return this.dateTo;
    }
    public void setCurrency(String currency){
        this.currency = currency;
    }
    public String getCurrency(){
        return this.currency;
    }
    public void setPrice(float price){
        this.price = price;
    }
    public float getPrice(){
        return this.price;
    }
}


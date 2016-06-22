package info.tomaszminiach.flightsapptest.sync;

import info.tomaszminiach.flightsapptest.data.DataRoot;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

///https://murmuring-ocean-10826.herokuapp.com/en/api/2/flights/from/DUB/to/BCN/2016-04-02/2016-04-02/250/unique/?limit=15&offset-0
public class RestClient {
    public static final String API_URL = "https://murmuring-ocean-10826.herokuapp.com/";

    public interface FlightsService {
        @GET("en/api/2/flights/from/{from_code}/to/{to_code}/{from_date}/{to_date}/250/unique/?limit=15&offset-0")
        Call<DataRoot> getFlights(@Path("from_code") String fromCode,
                                  @Path("to_code") String toCode,
                                  @Path("from_date") String fromDate,
                                  @Path("to_date") String toDate);
    }

    FlightsService webInterface;

    public RestClient(){
        if(webInterface==null) {
//            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
/**
 * Logging interceptor
 * @see "https://futurestud.io/blog/retrofit-2-log-requests-and-responses"
 */
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            /**
             * TODO Remove before publishing!!!
             */
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    //.client(httpClient.build())
                    .build();
            webInterface = retrofit.create(FlightsService.class);
        }
    }

    public FlightsService getWebInterface() {
        return webInterface;
    }
}

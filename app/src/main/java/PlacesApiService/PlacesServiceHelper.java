package PlacesApiService;

import com.google.android.gms.location.places.Place;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ofek on 26-Feb-18.
 */

public class PlacesServiceHelper {
    private static String GOOGLE_PLACES_NEARBY_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearby";
    public static void getBarsNearby(double lat, double lng, int radius, Callback<ResultPojo> callback){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(GOOGLE_PLACES_NEARBY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        GooglePlacesService service = builder.build().create(GooglePlacesService.class);
        retrofit2.Call<ResultPojo> call = service.searchBarsNearbyByRadius(PlacePojo.LatLngFormatter(lat,lng),radius);
        call.enqueue(callback);
    }
    public static ArrayList<Entities.Place> placePojoToPlaceList(ResultPojo resultPojo){
        ArrayList<Entities.Place> placesList = new ArrayList<>();

        for (PlacePojo place : resultPojo.getPlaces()){
            placesList.add(Entities.Place.getPlaceFromPlacePojo(place));
        }
        return placesList;
    }
}

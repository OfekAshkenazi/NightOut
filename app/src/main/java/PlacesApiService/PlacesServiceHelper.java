package PlacesApiService;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ofek on 26-Feb-18.
 */

public class PlacesServiceHelper {
    private static String GOOGLE_PLACES_NEARBY_BASE_URL = "https://maps.googleapis.com/maps/api/place/";

    public static void getBarsNearby(double lat, double lng, int radius, Callback<ResultPojo> callback){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(GOOGLE_PLACES_NEARBY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        GooglePlacesService service = builder.build().create(GooglePlacesService.class);
        retrofit2.Call<ResultPojo> call = service.searchBarsNearbyByRadius(PlacePojo.LatLngFormatter(lat,lng),radius);
        call.enqueue(callback);
    }

    public static ArrayList<Entities.Place> placePojoToPlaceList(ResultPojo resultPojo) {
        ArrayList<Entities.Place> placesList = new ArrayList<>();
        Log.e("places Count", "" + resultPojo.getPlaces().length);
        for (PlacePojo place : resultPojo.getPlaces()) {
            ArrayList<String> types = new ArrayList<>(Arrays.asList(place.getTypes()));
            ArrayList<String> unwantedTypes = getUnwantedTypes();
//            boolean flg = true;
//            for (String type:unwantedTypes) {
//                if (types.contains(type)){
//                    flg = false;
//                    break;
//                }
//            }
//            if (!flg){
//                continue;
//            }
            placesList.add(Entities.Place.getPlaceFromPlacePojo(place));
            Log.e("place name", place.getName());
            Log.e("photos count",""+place.getPhotos().length);
        }
        return placesList;
    }

    public static ArrayList<String> getUnwantedTypes() {
        ArrayList<String> unwantedTypes = new ArrayList<>();
        unwantedTypes.add("bakery");
        unwantedTypes.add("cafe");
        unwantedTypes.add("meal_delivery");
        unwantedTypes.add("meal_takeaway");
        unwantedTypes.add("lodging");
        unwantedTypes.add("home_goods_store");
        unwantedTypes.add("food");
        unwantedTypes.add("restaurant");
        unwantedTypes.add("supermarket");
        unwantedTypes.add("store");
        return unwantedTypes;
    }
}

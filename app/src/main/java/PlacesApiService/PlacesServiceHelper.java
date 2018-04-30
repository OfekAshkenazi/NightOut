package PlacesApiService;

import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ofek on 26-Feb-18.
 */

public class PlacesServiceHelper {
    private static String GOOGLE_PLACES_NEARBY_BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    private static String BAR_ICON_URL = "https://maps.gstatic.com/mapfiles/place_api/icons/bar-71.png";

    public static void getBarsNearby(LatLng placeLatLng,int radius,Callback<ResultPojo> callback){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(GOOGLE_PLACES_NEARBY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        GooglePlacesService service = builder.build().create(GooglePlacesService.class);
        retrofit2.Call<ResultPojo> call = service.searchBarsNearbyByRadius(PlacePojo.LatLngFormatter(placeLatLng.latitude,placeLatLng.longitude),radius);

        call.enqueue(callback);
    }
    public static void getNightClubsNearby(double lat, double lng, int radius, Callback<ResultPojo> callback){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(GOOGLE_PLACES_NEARBY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        GooglePlacesService service = builder.build().create(GooglePlacesService.class);
        retrofit2.Call<ResultPojo> call = service.searchClubsNearbyByRadius(PlacePojo.LatLngFormatter(lat,lng),radius);
        call.enqueue(callback);
    }
    public static ArrayList<Entities.Place> placePojoToPlaceList(ResultPojo resultPojo,int maxDistance,LatLng currentPosition) {
        ArrayList<Entities.Place> placesList = new ArrayList<>();
        Log.e("places Count", "" + resultPojo.getPlaces().length);
        for (PlacePojo placePojo : resultPojo.getPlaces()) {
            Log.e("icon url",placePojo.getIconUrl());
            Entities.Place place =  Entities.Place.getPlaceFromPlacePojo(placePojo);
            // filtering all the places who aren't bars/nightclubs
            if (!place.getIconUrl().equals(BAR_ICON_URL)) {
                Log.e("placesServiceHelper", "placePojoToPlaceList: place added. "+ "icon : "+placePojo.getIconUrl() +" place name: "+placePojo.getName() );
                continue;
            }
            int distance = (int) (getDistance(currentPosition,new LatLng(place.getLat(),place.getLng()))*1000);
            if (distance>maxDistance){
                continue;
            }
            placesList.add(place);
        }
        return placesList;
    }

    public static Entities.Place.PlacePhoto[] getPlacePhotos(final Entities.Place place) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(GOOGLE_PLACES_NEARBY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        GooglePlacesService service = builder.build().create(GooglePlacesService.class);
        Call<PlaceDetailsResultPojo> placeDetailsResultPojoCall = service.getPlaceDetails(place.getPlaceId());
        try {
            Log.e("getPlacePhotos"," url : "+placeDetailsResultPojoCall.request().url().toString());
            Entities.Place.PlacePhoto[] photos = placeDetailsResultPojoCall.execute().body().result.getPlacePhotos();
            return photos;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Entities.Place.PlacePhoto[0];
    }
    // base on the Haversine formula. copied from - jasonwinn/haversine. Link - https://github.com/jasonwinn/haversine/blob/master/Haversine.java
    private static final int EARTH_RADIUS = 6371; // Approx Earth radius in KM
    public static double getDistance(LatLng latLng1,LatLng latLng2){
        double endLat = latLng2.latitude;
        double endLong = latLng2.longitude;
        double startLat = latLng1.latitude;
        double startLong = latLng1.longitude;
        double dLat  = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat   = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // <-- d

    }
    private static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}

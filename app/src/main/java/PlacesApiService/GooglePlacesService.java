package PlacesApiService;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;


/**
 * Created by Ofek on 23-Feb-18.
 */
public interface GooglePlacesService {
    static String GOOGLE_PLACES_API_KEY = "AIzaSyAT122JQodpYI9XlgdZdxJQ16CcVUFvr-E";
    @GET("textsearch/json?language=en&type=bar&key="+GOOGLE_PLACES_API_KEY)
    @Streaming
    public Call<ResultPojo> searchBarsNearbyByRadius(@Query("location") String formattedLatLng,@Query("radius") int radius,@Query("query") String query);
    @GET("textsearch/json?query=Night+Clubs+nearby&language=en&type=night_club&key="+GOOGLE_PLACES_API_KEY)
    @Streaming
    public Call<ResultPojo> searchClubsNearbyByRadius(@Query("location") String formattedLatLng,@Query("radius") int radius);
    @GET("details/json?key="+GOOGLE_PLACES_API_KEY)
    @Streaming
    public Call<PlaceDetailsResultPojo> getPlaceDetails(@Query("placeid") String id);
}

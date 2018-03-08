package PlacesApiService;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by Ofek on 23-Feb-18.
 */

public interface GooglePlacesService {
    static String GOOGLE_PLACES_API_KEY = "AIzaSyAT122JQodpYI9XlgdZdxJQ16CcVUFvr-E";
    @GET("textsearch/json?query=Pubs+nearby&language=en&type=bar&key="+GOOGLE_PLACES_API_KEY)
    public Call<ResultPojo> searchBarsNearbyByRadius(@Query("location") String formattedLatLng,@Query("radius") int radius);
    @GET("textsearch/json?query=Night+Clubs+nearby&language=en&type=night_club&key="+GOOGLE_PLACES_API_KEY)
    public Call<ResultPojo> searchClubsNearbyByRadius(@Query("location") String formattedLatLng,@Query("radius") int radius);
}

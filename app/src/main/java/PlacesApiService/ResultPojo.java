package PlacesApiService;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ofek on 22-Feb-18.
 */

public class ResultPojo {
    @SerializedName("result")
    PlacePojo[] places;
    @SerializedName("next_page_token")
    String nextPage;

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

    public PlacePojo[] getPlaces() {
        return places;
    }

    public void setPlaces(PlacePojo[] places) {
        this.places = places;
    }
}

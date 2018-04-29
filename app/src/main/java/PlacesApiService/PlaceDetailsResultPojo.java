package PlacesApiService;

import com.google.gson.annotations.SerializedName;

import Entities.Place;

/**
 * Created by Ofek on 26-Mar-18.
 */

public class PlaceDetailsResultPojo {
    @SerializedName("result")
    Result result;

    public PlaceDetailsResultPojo(Result result) {
        this.result = result;
    }

    class Result{
        @SerializedName("photos")
        PlacePojo.PlacePhotoPojo[] photos;

        public Result() {
        }

        public Place.PlacePhoto[] getPlacePhotos() {
            if (photos == null){
                return null;
            }
            Place.PlacePhoto[] placePhotos = new Place.PlacePhoto[photos.length];
            for (int i = 0;i<photos.length;i++){
                PlacePojo.PlacePhotoPojo photoPojo = photos[i];
                Place.PlacePhoto photo = new Place.PlacePhoto(photoPojo.getHeight(),photoPojo.getWidth(),photoPojo.getReference());
                placePhotos[i]=photo;
            }

            return placePhotos;
        }

        public PlacePojo.PlacePhotoPojo[] getPhotos() {
            return photos;
        }

        public void setPhotos(PlacePojo.PlacePhotoPojo[] photos) {
            this.photos = photos;
        }
    }
}

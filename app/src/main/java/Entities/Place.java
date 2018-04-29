package Entities;

import PlacesApiService.PlacePojo;

/**
 * Created by Ofek on 22-Feb-18.
 */
public class Place{
    private String placeId;
    private String iconUrl;
    private String name;
    private String textualAddress;
    private String[] types;
    private double lat;
    private double lng;
    private PlacePhoto[] photos;
    private boolean isFavorite = false;

    public Place() {

    }
    public Place(String id, String iconUrl, String name, String textualAddress, String[] types, double lat, double lng) {
        this.iconUrl = iconUrl;
        this.placeId = id;
        this.name = name;
        this.textualAddress = textualAddress;
        this.types = types;
        this.lat = lat;
        this.lng = lng;
    }
    public Place(String id, String iconUrl, String name, String textualAddress, String[] types, double lat, double lng, PlacePhoto[] photos) {
        this.iconUrl = iconUrl;
        this.placeId = id;
        this.name = name;
        this.textualAddress = textualAddress;
        this.types = types;
        this.lat = lat;
        this.lng = lng;
        this.photos = photos;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }

    public String getTextualAddress() {
        return textualAddress;
    }

    public String[] getTypes() {
        return types;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public PlacePhoto[] getPhotos() {
        return photos;
    }

    public void setPhotos(PlacePhoto[] photos) {
        this.photos = photos;
    }

    public static class PlacePhoto {
        public PlacePhoto(int height, int width, String reference) {
            this.height = height;
            this.width = width;
            this.reference = reference;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public String getReference() {
            return reference;
        }

        private int height;
        private int width;
        private String reference;
    }

    public static Place getPlaceFromPlacePojo(PlacePojo pojo){
        return new Place(pojo.getId(),
                pojo.getIconUrl(),
                pojo.getName(),
                pojo.getTextualAddress(),
                pojo.getTypes(),
                pojo.getLocation().getLocation().getLat(),
                pojo.getLocation().getLocation().getLng(),
                getPhotosFromPojo(pojo.getPhotos()));
    }

    private static PlacePhoto[] getPhotosFromPojo(PlacePojo.PlacePhotoPojo[] photosPojo) {
        if (photosPojo == null){
            return new PlacePhoto[]{};
        }
        PlacePhoto[] photos = new PlacePhoto[photosPojo.length];
        for (int i = 0;i<photosPojo.length;i++){
            PlacePojo.PlacePhotoPojo photoPojo = photosPojo[i];
            photos[i] = new PlacePhoto(photoPojo.getHeight(),photoPojo.getWidth(),photoPojo.getReference());
        }

        return photos;
    }
}

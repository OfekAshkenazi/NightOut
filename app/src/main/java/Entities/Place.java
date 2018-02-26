package Entities;

import java.io.Serializable;

import PlacesApiService.PlacePojo;

/**
 * Created by Ofek on 22-Feb-18.
 */

public class Place implements Serializable {
    private String iconUrl;
    private String id;
    private String name;
    private String textualAddress;
    private String[] types;
    private double lat;
    private double lang;
    private PlacePhoto[] photos;
    private boolean isFavorite = false;
    public Place(String iconUrl, String id, String name, String textualAddress, String[] types, double lat, double lang, PlacePhoto[] photos) {
        this.iconUrl = iconUrl;
        this.id = id;
        this.name = name;
        this.textualAddress = textualAddress;
        this.types = types;
        this.lat = lat;
        this.lang = lang;
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

    public String getId() {
        return id;
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

    public double getLang() {
        return lang;
    }

    public PlacePhoto[] getPhotos() {
        return photos;
    }

    public static class PlacePhoto{
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
        return new Place(pojo.getIconUrl(),
                pojo.getId(),
                pojo.getName(),
                pojo.getTextualAddress(),
                pojo.getTypes(),
                pojo.getLocation().getLocation().getLat(),
                pojo.getLocation().getLocation().getLng(),
                getPhotosFromPojo(pojo.getPhoto()));
    }

    private static PlacePhoto[] getPhotosFromPojo(PlacePojo.PlacePhotoPojo[] photosPojo) {
        PlacePhoto[] photos = new PlacePhoto[photosPojo.length];
        for (int i = 0;i<photosPojo.length;i++){
            PlacePojo.PlacePhotoPojo photoPojo = photosPojo[i];
            photos[i] = new PlacePhoto(photoPojo.getHeight(),photoPojo.getWidth(),photoPojo.getReference());
        }

        return photos;
    }
}

package PlacesApiService;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ofek on 22-Feb-18.
 */

public class PlacePojo implements Serializable {
    @SerializedName("icon")
    private String iconUrl;
    @SerializedName("place_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("vicinity")
    private String textualAddress;
    @SerializedName("types")
    private String[] types;
    @SerializedName("geometry")
    private Geometry location;
    @SerializedName("photos")
    private PlacePhotoPojo[] photo;

    public PlacePhotoPojo[] getPhoto() {
        return photo;
    }

    public void setPhoto(PlacePhotoPojo[] photo) {
        this.photo = photo;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTextualAddress() {
        return textualAddress;
    }

    public void setTextualAddress(String textualAddress) {
        this.textualAddress = textualAddress;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public Geometry getLocation() {
        return location;
    }

    public void setLocation(Geometry location) {
        this.location = location;
    }

    public class PlacePhotoPojo {
        @SerializedName("height")
        private int height;
        @SerializedName("width")
        private int width;
        @SerializedName("photo_reference")
        private String reference;

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }
    }

    public class Geometry {
        @SerializedName("location")
        private Location location;

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public class Location{
            @SerializedName("lat")
            private double lat;
            @SerializedName("lng")
            private double lng;

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }

            public double getLng() {
                return lng;
            }

            public void setLng(double lng) {
                this.lng = lng;
            }
        }
    }
    public static String LatLngFormatter(double lat, double lng){
        return ""+lat+","+lng;
    }
}

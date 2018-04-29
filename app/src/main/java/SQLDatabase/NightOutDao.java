package SQLDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

import Entities.Place;
import PlacesApiService.PlacesServiceHelper;

/**
 * Created by Ofek on 19-Mar-18.
 */

public class NightOutDao {
    private static NightOutDb instance;
    public static void init(Context context){
        instance = new NightOutDb(context);
    }
    public static ArrayList<Place> getAllFavouritePlaces(){
        SQLiteDatabase database = instance.getReadableDatabase();
        Cursor cursor = database.query(NightOutDb.DatabaseEntries.PLACE_TABLE_NAME,null,null,null,null,null,null);
        try {
            ArrayList<Place> places = new ArrayList<>();
            while (cursor.moveToNext()){
                String id =  cursor.getString(cursor.getColumnIndex(NightOutDb.DatabaseEntries.ID_COL_1));
                String icon = cursor.getString(cursor.getColumnIndex(NightOutDb.DatabaseEntries.ICON_COL_2));
                String name = cursor.getString(cursor.getColumnIndex(NightOutDb.DatabaseEntries.NAME_COL_3));
                String address = cursor.getString(cursor.getColumnIndex(NightOutDb.DatabaseEntries.ADDRESS_COL_4));
                String[] types = cursor.getString(cursor.getColumnIndex(NightOutDb.DatabaseEntries.TYPES_COL_5)).split(",");
                double lat = cursor.getDouble(cursor.getColumnIndex(NightOutDb.DatabaseEntries.LAT_COL_6));
                double lng =  cursor.getDouble(cursor.getColumnIndex(NightOutDb.DatabaseEntries.LNG_COL_7));
                Place place = new Place(id,icon,name,address,types,lat,lng);
                places.add(place);
            }
            return places;
        }
        finally {
            cursor.close();
        }
    }
    public static boolean isFavourite(Place place){
        ArrayList<Place> places = getAllFavouritePlaces();

        for (Place temp: places){
            if (place.getPlaceId().equals(temp.getPlaceId())){
                return true;
            }
        }
        return false;
    }
    public static void addToFavourites(@NonNull Place[] places){
        SQLiteDatabase database = instance.getWritableDatabase();
        for (Place place: places){
            ContentValues contentValues = new ContentValues();
            String types = "";
            for (String type: place.getTypes()){
                types +=type;
                types += ",";
            }
            types = types.substring(0,types.length()-2);
            StringBuilder photoReferences = new StringBuilder();
            for (Place.PlacePhoto photo: place.getPhotos()){
                Log.e("onAddToFav","place name: "+ place.getName()+" photo url: "+photo.getReference());
                photoReferences.append('"');
                photoReferences.append(photo.getReference());
                photoReferences.append('"');
                photoReferences.append(",");
            }
            photoReferences = new StringBuilder(photoReferences.substring(0, types.length() - 2));
            contentValues.put(NightOutDb.DatabaseEntries.ID_COL_1,place.getPlaceId());
            Log.e("inserting","place id = "+place.getPlaceId());
            contentValues.put(NightOutDb.DatabaseEntries.ICON_COL_2,place.getIconUrl());
            contentValues.put(NightOutDb.DatabaseEntries.NAME_COL_3,place.getName());
            contentValues.put(NightOutDb.DatabaseEntries.ADDRESS_COL_4,place.getTextualAddress());
            contentValues.put(NightOutDb.DatabaseEntries.TYPES_COL_5,types);
            contentValues.put(NightOutDb.DatabaseEntries.LAT_COL_6,place.getLat());
            contentValues.put(NightOutDb.DatabaseEntries.LNG_COL_7,place.getLng());
            Log.e("addToFavourites","" + database.insert(NightOutDb.DatabaseEntries.PLACE_TABLE_NAME,null,contentValues));
        }
    }

    public static void deleteFromFavourites(Place[] places) {
        SQLiteDatabase database = instance.getWritableDatabase();
        for (int i = 0; i <places.length ; i++) {
            database.delete(NightOutDb.DatabaseEntries.PLACE_TABLE_NAME, "place_id='"+places[i].getPlaceId()+"'",null);
        }
    }
}

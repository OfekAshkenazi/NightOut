package Adapters;

import android.graphics.Color;
import android.media.Image;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import java.util.ArrayList;
import java.util.List;

import CostumeViews.ImageRecyclerView;
import CostumeViews.ImageViewPager;
import Entities.Place;
import ofeksprojects.ofek.com.nightout.R;
/**
 * Created by Ofek on 26-Feb-18.
 */

public class PlacesSearchAdapter extends BaseQuickAdapter<Place,PlacesSearchAdapter.PlaceViewHolder> {

    private static String PLACE_PHOTOS_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    public PlacesSearchAdapter(@Nullable List<Place> data) {
        super(R.layout.place_item_base_layout,data);
    }

    @Override
    protected void convert(PlaceViewHolder helper, Place item) {
        helper.nameTV.setText(item.getName());
        helper.addressTV.setText(item.getTextualAddress());
//        if (Place.findById(Place.class,new String[]{item.getPlaceId()}) != null){
//            helper.favoriteButton.setFavorite(true,false);
//        }
        loadThumbs(helper,item);
    }

    private void loadThumbs(PlaceViewHolder helper, Place item) {
        ArrayList<String> photosUrl = new ArrayList<>();
        Log.e("place name: "+item.getName(),"photos count = "+item.getPhotos().length);
        for (Place.PlacePhoto photo:item.getPhotos()) {
            StringBuilder builder = new StringBuilder(PLACE_PHOTOS_BASE_URL);
            builder.append("maxwidth=" + photo.getWidth());
            builder.append("&");
            builder.append("maxheight=" + photo.getHeight());
            builder.append("&");
            builder.append("photoreference=" + photo.getReference());
            builder.append("&");
            builder.append("key=AIzaSyAT122JQodpYI9XlgdZdxJQ16CcVUFvr-E");
            photosUrl.add(builder.toString());
        }
        helper.pager.setPhotos(photosUrl);
    }

    public class PlaceViewHolder extends BaseViewHolder {
        TextView nameTV,addressTV;
        MaterialFavoriteButton favoriteButton;
        ImageRecyclerView pager;
        public PlaceViewHolder(View view) {
            super(view);
            nameTV = view.findViewById(R.id.nameTV_placeAdapter);
            addressTV = view.findViewById(R.id.addressTV_placeAdapter);
            favoriteButton = view.findViewById(R.id.favoriteButton_placeAdapter);
            pager = view.findViewById(R.id.imagePager_placeAdapter);
//            favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
//                @Override
//                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
//                    Place currentPlace = getData().get(getLayoutPosition());
//                    if (favorite&&Place.findById(Place.class,new String[]{currentPlace.getPlaceId()}) == null){
//                        currentPlace.save();
//                        return;
//                    }
//                    if (!favorite){
//                        currentPlace.delete();
//                    }
//                }
//            });
        }
    }
}

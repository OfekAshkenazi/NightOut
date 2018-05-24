package Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import CostumeViews.ImageRecyclerView;
import Entities.Place;
import lal.adhish.gifprogressbar.GifView;
import ofeksprojects.ofek.com.nightout.R;

/**
 * Created by Ofek on 21-Mar-18.
 */

public class FavouritesAdapter extends BaseQuickAdapter<Place,FavouritesAdapter.ViewHolder> {
    ShowMapCallback mapCallback;
    private static String PLACE_PHOTOS_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    public FavouritesAdapter(@Nullable List<Place> data, ShowMapCallback mapCallback,Context context) {
        super(R.layout.favourite_list_item,data);
        this.mapCallback = mapCallback;
        mContext = context;

    }

    public void activateLoadingView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.loading_layout,getRecyclerView(),false);
        setEmptyView(view);
        GifView loadingView = getEmptyView().findViewById(R.id.loadingIV_loadLay);
        loadingView.setImageResource(R.drawable.filled_glass_loading);
        setNewData(new ArrayList<Place>());
    }


    @Override
    protected void convert(ViewHolder helper, Place item) {
        helper.nameTV.setText(item.getName());
        helper.addressTV.setHint(item.getTextualAddress());
        loadThumbs(helper,item);
    }

    private void loadThumbs(ViewHolder helper, Place item) {
        ArrayList<String> photosUrl = new ArrayList<>();
        for (Place.PlacePhoto photo:item.getPhotos()) {
            StringBuilder builder = new StringBuilder(PLACE_PHOTOS_BASE_URL);
            builder.append("maxwidth=" + photo.getWidth() );
            builder.append("&");
            builder.append("maxheight=" + photo.getHeight());
            builder.append("&");
            builder.append("photoreference=" + photo.getReference());
            builder.append("&");
            builder.append("key=");
            builder.append(R.string.google_places_api_key);
            Log.e("photo url","url = "+builder.toString());
            photosUrl.add(builder.toString());
        }
        helper.thumbIV.setPhotos(photosUrl,null);
    }

    public class ViewHolder extends BaseViewHolder {
        TextView nameTV,addressTV;
        ImageRecyclerView thumbIV;
        ImageButton wazeBtn,mapBtn;
        public ViewHolder(View view) {
            super(view);
            nameTV = view.findViewById(R.id.nameTV_favAdapter);
            addressTV = view.findViewById(R.id.addressTV_favAdapter);
            thumbIV = view.findViewById(R.id.thumbRV_favAdapter);
            wazeBtn = view.findViewById(R.id.wazeButton_favAdapter);
            mapBtn = view.findViewById(R.id.mapButton_favAdapter);
            mapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //show the place on the map
                    mapCallback.showMap(getData().get(getLayoutPosition()));
                }
            });
            wazeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // open navigation with waze
                    Place place = getData().get(getLayoutPosition());
                    StringBuilder wazeUriBuilder = new StringBuilder();
                    wazeUriBuilder.append("https://waze.com/ul");
                    wazeUriBuilder.append("?ll=");
                    wazeUriBuilder.append(place.getLat());
                    wazeUriBuilder.append(",");
                    wazeUriBuilder.append(place.getLng());
                    wazeUriBuilder.append("&navigate=yes");
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(wazeUriBuilder.toString()));
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}

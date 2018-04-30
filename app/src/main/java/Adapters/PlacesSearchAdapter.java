package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import CostumeViews.ImageRecyclerView;
import Entities.Place;
import PlacesApiService.PlacesServiceHelper;
import SQLDatabase.NightOutDao;
import lal.adhish.gifprogressbar.GifView;
import ofeksprojects.ofek.com.nightout.R;

/**
 * Created by Ofek on 26-Feb-18.
 */

public class PlacesSearchAdapter extends BaseQuickAdapter<Place,PlacesSearchAdapter.PlaceViewHolder> {
    private final Handler requestHandler;
    private HandlerThread requestThread;
    private ShowMapCallback mapCallback;
    private com.google.android.gms.location.places.Place currentPlace;
    private static String PLACE_PHOTOS_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    public PlacesSearchAdapter(@Nullable List<Place> data, Context context,ShowMapCallback mapCallback) {
        super(R.layout.place_item_base_layout,data);
        requestThread = new HandlerThread("photo_request_handler");
        requestThread.start();
        requestHandler = new Handler(requestThread.getLooper());
        mContext = context;
        this.mapCallback = mapCallback;
    }

    public void setCurrentPlace(com.google.android.gms.location.places.Place currentPlace) {
        this.currentPlace = currentPlace;
    }

    public void activateLoadingView() {
        View loadingView = LayoutInflater.from(mContext).inflate(R.layout.loading_layout,getRecyclerView(),false);
        setEmptyView(loadingView);
        GifView loadingImageView = getEmptyView().findViewById(R.id.loadingIV_loadLay);
        loadingImageView.setImageResource(R.drawable.filled_glass_progress_bar_gif);
        setNewData(new ArrayList<Place>());
    }
    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(PlaceViewHolder helper, Place item) {
        helper.nameTV.setText(item.getName());
        helper.addressTV.setText(item.getTextualAddress());
        if (item.isFavorite()){
            helper.favoriteButton.setFavorite(true,false);
        }
        else {
            helper.favoriteButton.setFavorite(false,false);
        }
        helper.imagesRV.setPhotos(getThumbsAsLinks(item));
        // activate after adding zoom animation.
        loadThumbs(helper,item);
        if (currentPlace!=null){
            helper.distanceTV.setText(""+(int) PlacesServiceHelper.getDistance(currentPlace.getLatLng(),new LatLng(item.getLat(),item.getLng())));
        }
        else {
            helper.distanceTV.setText(R.string.error_text);
        }
    }

    private void loadThumbs(final PlaceViewHolder helper, final Place item) {
        if (item.getPhotos().length>=1)
            requestHandler.post(new Runnable() {
                @Override
                public void run() {
                    item.setPhotos(PlacesServiceHelper.getPlacePhotos(item));
                    // if the current cell is no longer holds this item skip the photos display.
                    if (helper.getLayoutPosition()==getData().indexOf(item)) {
                        helper.imagesRV.post(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<String> photos = new ArrayList<String>(getThumbsAsLinks(item));
                                photos.addAll(getThumbsAsLinks(item));
                                helper.imagesRV.setPhotos(photos);
                            }
                        });
                    }
                }
            });
    }
    private ArrayList<String> getThumbsAsLinks(Place item) {
        ArrayList<String> photosUrl = new ArrayList<>();
        Log.e("place name: "+item.getName(),"photos count = "+item.getPhotos().length);
        for (Place.PlacePhoto photo:item.getPhotos()) {
            StringBuilder builder = new StringBuilder(PLACE_PHOTOS_BASE_URL);
            builder.append("maxwidth=" + photo.getWidth() );
            builder.append("&");
            builder.append("maxheight=" + photo.getHeight());
            builder.append("&");
            builder.append("photoreference=" + photo.getReference());
            builder.append("&");
            builder.append("key=AIzaSyAT122JQodpYI9XlgdZdxJQ16CcVUFvr-E");
            Log.e("photo url","url = "+builder.toString());
            if (!photosUrl.contains(builder.toString())) {
                photosUrl.add(builder.toString());
            }
        }
        return photosUrl;
    }
    public class PlaceViewHolder extends BaseViewHolder {
        TextView nameTV,addressTV,distanceTV;
        MaterialFavoriteButton favoriteButton;
        ImageRecyclerView imagesRV;
        ImageView wazeBtn,mapBtn;
        public PlaceViewHolder(final View view) {
            super(view);
            distanceTV = view.findViewById(R.id.distanceTV_placeAdapter);
            nameTV = view.findViewById(R.id.nameTV_placeAdapter);
            addressTV = view.findViewById(R.id.addressTV_placeAdapter);
            favoriteButton = view.findViewById(R.id.favoriteButton_placeAdapter);
            imagesRV = view.findViewById(R.id.imagePager_placeAdapter);
            wazeBtn = view.findViewById(R.id.wazeBtn_placesAdapter);
            mapBtn = view.findViewById(R.id.mapButton_placeAdapter);
            mapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mapCallback.showMap(getData().get(getLayoutPosition()));
                }
            });
            wazeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // open with waze
                }
            });
            favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    // add and delete places from favourites database
                    final Place currentPlace = getData().get(getLayoutPosition());
                    if (!favorite&&currentPlace.isFavorite()){
                        currentPlace.setFavorite(false);
                        requestHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                NightOutDao.deleteFromFavourites(new Place[]{currentPlace});
                            }
                        });
                        return;
                    }
                    if (favorite&&!currentPlace.isFavorite()){
                        currentPlace.setFavorite(true);
                        requestHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                NightOutDao.addToFavourites(new Place[]{currentPlace});
                            }
                        });
                    }
                }
            });
            setNestingTouch();
        }
        private void setNestingTouch(){
            imagesRV.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    int action = e.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_MOVE:
                            rv.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                    }
                    return false;
                }

                @Override
                public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            });
        }
    }
}

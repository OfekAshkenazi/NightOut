package Dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.UltraViewPagerAdapter;

import java.util.ArrayList;

import Entities.Place;
import PlacesApiService.PlacesServiceHelper;
import ofeksprojects.ofek.com.nightout.R;

/**
 * Created by Ofek on 30-Apr-18.
 */

public class ImageGalleryDialogFragment extends DialogFragment {
    private static final String PLACE_ID_TAG = "placeID";
    private static String PLACE_THUMB_REFERENCE_TAG = "place_thumb";
    private Handler requestHandler;
    private HandlerThread handlerThread;
    private String placeId;
    private ArrayList<String> photosUrls = new ArrayList<>();
    private String thumbRef = "";
    private boolean isLoaded = false;
    private ProgressBar progressBar;
    private UltraViewPager imagePager;

    public static ImageGalleryDialogFragment getInstance(Place place){
        ImageGalleryDialogFragment galleryDialogFragment = new ImageGalleryDialogFragment();
        Bundle args = new Bundle();
        args.putString(PLACE_ID_TAG,place.getPlaceId());
        if (place.getPhotos().length>=1){
            args.putString(PLACE_THUMB_REFERENCE_TAG,place.getPhotos()[0].getReference());
        }
        galleryDialogFragment.setArguments(args);
        return galleryDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handlerThread = new HandlerThread("request thread");
        handlerThread.start();
        requestHandler = new Handler(handlerThread.getLooper());
        requestPhotos();
        if (getArguments()==null){
            // handle data transfer error
            Log.e("ImageGalleryDialog","onCreate: arguments not available. data transfer error");
            return;
        }
        placeId = getArguments().getString(PLACE_ID_TAG);
        thumbRef = getArguments().getString(PLACE_THUMB_REFERENCE_TAG);
    }

    private void requestPhotos() {
        requestHandler.post(new Runnable() {
            @Override
            public void run() {
                Place.PlacePhoto[] photosArr = PlacesServiceHelper.getPlacePhotos(placeId);
                if (photosArr==null){
                    dismiss();
                    Toast.makeText(getContext(), R.string.no_photos_found, Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Place.PlacePhoto photo: photosArr){
                    String url = PlacesServiceHelper.getUrlFromReference(photo.getReference(),1600,1600);
                    Log.e("ImageDialog","requestPhotos: "+url);
                    photosUrls.add(url);
                }
                if (!photosUrls.contains(PlacesServiceHelper.getUrlFromReference(thumbRef,1600,1600))){
                    photosUrls.add(PlacesServiceHelper.getUrlFromReference(thumbRef,1600,1600));
                }
                if (imagePager !=null&&!isLoaded){
                    imagePager.post(new Runnable() {
                        @Override
                        public void run() {
                            imagePager.setAdapter(new ImagePagerAdapter(photosUrls));
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    isLoaded = true;
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_image_gallery,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progressBar_imageDialog);
        imagePager = view.findViewById(R.id.photosPager_imageDialog);
        setPager();
        if (photosUrls.size() >= 1 && !isLoaded) {
            progressBar.setVisibility(View.GONE);
            imagePager.setAdapter(new ImagePagerAdapter(photosUrls));
            isLoaded = true;
        }
    }

    private void setPager() {
        imagePager.initIndicator();
        imagePager.getIndicator()
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(R.color.purple)
                .setNormalColor(Color.WHITE)
                .setRadius((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
//set the alignment
        imagePager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
//construct built-in indicator, and add it to  UltraViewPager
        imagePager.getIndicator().build();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_image_gallery,null);
        Dialog dialog = new Dialog(getContext(),R.style.ImageGalleryDialogTheme);
        dialog.setContentView(view);
        Point point = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(point);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,point.x);
        dialog.getWindow().setGravity(Gravity.CENTER);
        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handlerThread.quitSafely();
    }
    protected class ImagePagerAdapter extends PagerAdapter {
        private ArrayList<String> urlsArr;

        public ImagePagerAdapter(ArrayList<String> urlsArr) {
            this.urlsArr = new ArrayList<>();
            this.urlsArr.addAll(urlsArr);
        }

        @Override
        public int getCount() {
            return urlsArr.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView photosIV = new ImageView(container.getContext());
            photosIV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            Picasso.with(getContext()).load(urlsArr.get(position)).fit().into(photosIV);
            container.addView(photosIV,0);
            return photosIV;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((ImageView) object);
        }
    }
}

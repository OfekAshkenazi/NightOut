package CostumeViews;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.rd.PageIndicatorView;

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
    private ImageRecyclerView imagesRV = null;
    private Handler requestHandler;
    private HandlerThread handlerThread;
    private String placeId;
    private ArrayList<String> photosUrls = new ArrayList<>();
    private String thumbRef = "";
    private boolean isLoaded = false;
    private ProgressBar progressBar;
    private PageIndicatorView pagerIndicators;

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
                for (Place.PlacePhoto photo: photosArr){
                    String url = PlacesServiceHelper.getUrlFromReference(photo.getReference(),1600,1600);
                    Log.e("ImageDialog","requestPhotos: "+url);
                    photosUrls.add(url);
                }
                if (!photosUrls.contains(PlacesServiceHelper.getUrlFromReference(thumbRef,1600,1600))){
                    photosUrls.add(PlacesServiceHelper.getUrlFromReference(thumbRef,1600,1600));
                }
                if (imagesRV !=null&&!isLoaded){
                    imagesRV.post(new Runnable() {
                        @Override
                        public void run() {
                            imagesRV.setPhotos(photosUrls,pagerIndicators);
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
        imagesRV = view.findViewById(R.id.imagePager_gallery_dialog);
        progressBar = view.findViewById(R.id.progressBar_imageDialog);
        pagerIndicators = view.findViewById(R.id.indicators_ImageDialog);

        if (photosUrls.size() >= 1 && !isLoaded) {
            progressBar.setVisibility(View.GONE);
            imagesRV.setPhotos(photosUrls,pagerIndicators);
            isLoaded = true;
        }
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

}

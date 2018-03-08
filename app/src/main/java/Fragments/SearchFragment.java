package Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Adapters.PlacesSearchAdapter;
import PlacesApiService.PlacePojo;
import PlacesApiService.PlacesServiceHelper;
import PlacesApiService.ResultPojo;
import ofeksprojects.ofek.com.nightout.MainNavActivity;
import ofeksprojects.ofek.com.nightout.R;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private static final int SEARCH_AUTO_COMPLETE_REQUEST = 154;
    private SupportPlaceAutocompleteFragment searchFragment;
    private BubbleSeekBar radiusSeekBar;
    private TextView currentPlaceTV;
    private Place currentPlace;
    private ArrayList<Entities.Place> nearbyBarsList = new ArrayList<>();
    private ArrayList<Entities.Place> nearbyClubsList = new ArrayList<>();
    private RecyclerView placesRV;
    private PlacesSearchAdapter barsAdapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final CardView searchCard = view.findViewById(R.id.searchCard_searchFrag);
        placesRV = view.findViewById(R.id.resultList_searchFrag);
        placesRV.setLayoutManager(new GridLayoutManager(placesRV.getContext(),1,GridLayoutManager.HORIZONTAL,false));
        assert getActivity()!=null;
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(placesRV);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().show();
        currentPlaceTV = view.findViewById(R.id.currentPlaceTV_fragSearch);
        searchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaceAutocomplete.IntentBuilder builder = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY);
                builder.setFilter(new AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE)
                        .build());
                try {
                    startActivityForResult(builder.build(getActivity()),SEARCH_AUTO_COMPLETE_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        radiusSeekBar = view.findViewById(R.id.radiusPicker_searchFrag);
        radiusSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                if (currentPlace!=null){
                    requestPlaces();
                }
            }
        });
    }

    private void requestPlaces() {
        PlacesServiceHelper.getBarsNearby(currentPlace.getLatLng().latitude, currentPlace.getLatLng().longitude, radiusSeekBar.getProgress(), new Callback<ResultPojo>() {
            @Override
            public void onResponse(Call<ResultPojo> call, Response<ResultPojo> response) {
                for (PlacePojo placePojo : response.body().getPlaces()){
                    Log.e("place name",placePojo.getName());
                    Log.e("place address",placePojo.getTextualAddress());
                }
                nearbyBarsList = PlacesServiceHelper.placePojoToPlaceList(response.body());
                barsAdapter = new PlacesSearchAdapter(nearbyBarsList);
                placesRV.setAdapter(barsAdapter);
            }

            @Override
            public void onFailure(Call<ResultPojo> call, Throwable t) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert getContext() !=null;
        if (requestCode == SEARCH_AUTO_COMPLETE_REQUEST){
            if (resultCode == RESULT_OK){
                currentPlace = PlaceAutocomplete.getPlace(getContext(),data);
                currentPlaceTV.setText(currentPlace.getName());
                requestPlaces();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // TODO: Handle the error.

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}

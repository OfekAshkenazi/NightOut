package Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;

import Adapters.PlacesSearchAdapter;
import Adapters.ShowMapCallback;
import PlacesApiService.PlacesServiceHelper;
import PlacesApiService.ResultPojo;
import SQLDatabase.NightOutDao;
import ofeksprojects.ofek.com.nightout.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private static final int SEARCH_AUTO_COMPLETE_REQUEST = 154;
    private static final int SEARCH_BARS = 0;
    private static final int SEARCH_NIGHT_CLUBS = 1;
    private SupportPlaceAutocompleteFragment searchFragment;
    private BubbleSeekBar radiusSeekBar;
    private TextView currentPlaceTV;
    private Place currentPlace;
    private RecyclerView placesRV;
    private PlacesSearchAdapter placesAdapter;
    ArrayList<Entities.Place> barsList;
    ArrayList<Entities.Place> clubsList;
    private CardView searchCard;
    private TabLayout tabLayout;

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
        setViews();
        placesAdapter = new PlacesSearchAdapter(new ArrayList<Entities.Place>(),getContext(), (ShowMapCallback) getActivity());
        tabLayout.addOnTabSelectedListener(this);
        placesRV.setLayoutManager(new GridLayoutManager(placesRV.getContext(),1,GridLayoutManager.HORIZONTAL,false));
        placesRV.setAdapter(placesAdapter);
        assert getActivity()!=null;
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(placesRV);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity.getSupportActionBar()!=null;
        activity.getSupportActionBar().show();
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

        radiusSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });
    }

    private void setViews() {
        View view = getView();
        assert view!=null;
        tabLayout = view.findViewById(R.id.tabLay_searchFrag);
        searchCard = view.findViewById(R.id.searchCard_searchFrag);
        placesRV = view.findViewById(R.id.resultList_searchFrag);
        currentPlaceTV = view.findViewById(R.id.currentPlaceTV_fragSearch);
        radiusSeekBar = view.findViewById(R.id.radiusPicker_searchFrag);
    }

    private void requestPlaces() {
        if (currentPlace == null){
            return;
        }
        placesAdapter.activateLoadingView();
        PlacesServiceHelper.getBarsNearby(currentPlace, radiusSeekBar.getProgress(), new Callback<ResultPojo>() {
            @Override
            public void onResponse(Call<ResultPojo> call, Response<ResultPojo> response) {
                Log.e("request url",call.request().url().toString());
                barsList =PlacesServiceHelper.placePojoToPlaceList(response.body());
                checkForFavourites(barsList);
                if (tabLayout.getSelectedTabPosition()==0){
                    placesAdapter.setNewData(barsList);
                }
            }

            @Override
            public void onFailure(Call<ResultPojo> call, Throwable t) {

            }
        });
        PlacesServiceHelper.getNightClubsNearby(currentPlace.getLatLng().latitude, currentPlace.getLatLng().longitude, radiusSeekBar.getProgress(), new Callback<ResultPojo>() {
            @Override
            public void onResponse(Call<ResultPojo> call, Response<ResultPojo> response) {
                clubsList =PlacesServiceHelper.placePojoToPlaceList(response.body());
                checkForFavourites(clubsList);
                if (tabLayout.getSelectedTabPosition()==1){
                    placesAdapter.setNewData(clubsList);
                }
            }

            @Override
            public void onFailure(Call<ResultPojo> call, Throwable t) {

            }
        });
    }

    private void checkForFavourites(ArrayList<Entities.Place> placesList) {
        for (Entities.Place place : placesList){
            if (NightOutDao.isFavourite(place)){
                place.setFavorite(true);
            }
            else {
                place.setFavorite(false);
            }
        }
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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.e("onTabSelected","tab position: "+tab.getPosition());
        switch (tab.getPosition()){
            case 0:{
                placesAdapter.setNewData(barsList);
                break;
            }
            case 1:{
                placesAdapter.setNewData(clubsList);
                break;
            }
            default:{
                Log.e("onTabSelected","tab index error");
                return;
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}

package Fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.xw.repo.BubbleSeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
public class SearchFragment extends Fragment implements TabLayout.OnTabSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int SEARCH_AUTO_COMPLETE_REQUEST = 154;
    public static final String PREDEFINED_SEARCH_TAG = "predefinedSearch";
    private BubbleSeekBar radiusSeekBar;
    private TextView currentPlaceTV;
    private LatLng currentPlaceLatLng;
    private RecyclerView placesRV;
    private PlacesSearchAdapter placesAdapter;
    private ArrayList<Entities.Place> barsList;
    private ArrayList<Entities.Place> clubsList;
    private CardView searchCard;
    private TabLayout tabLayout;
    private GoogleApiClient googleApiClient;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
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
        placesAdapter = new PlacesSearchAdapter(new ArrayList<Entities.Place>(), getContext(), (ShowMapCallback) getActivity(), (OpenGalleryDialog) getActivity());
        tabLayout.addOnTabSelectedListener(this);
        placesRV.setLayoutManager(new GridLayoutManager(placesRV.getContext(), 1, GridLayoutManager.HORIZONTAL, false));
        placesRV.setAdapter(placesAdapter);
        assert getActivity() != null;
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(placesRV);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity.getSupportActionBar() != null;
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
                    startActivityForResult(builder.build(getActivity()), SEARCH_AUTO_COMPLETE_REQUEST);
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
        checkForPredefinedSearch();
    }

    private void checkForPredefinedSearch() {
        if (!checkPlayServices()) {
            Toast.makeText(getContext(), "this devicec not support google play services", Toast.LENGTH_SHORT).show();
        }
        if (getArguments() == null) {
            return;
        }
        switch (getArguments().getInt(PREDEFINED_SEARCH_TAG)) {
            //search for pubs nearby
            case 1: {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                else {

                }
                LocationServices.getFusedLocationProviderClient(getContext()).getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        try {
                            String currentAddress = getAddressFromLatLng(location.getLatitude(),location.getLongitude());
                            currentPlaceTV.setText(currentAddress);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), R.string.loaction_track_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            // search for clubs nearby
            case 2: {


            }
            default: {
                return;
            }
        }
    }
    // get the current user address. copied from - https://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude
    private String getAddressFromLatLng(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        return city+", "+state+", "+country;
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices(){
        return true;
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
        if (currentPlaceLatLng == null){
            return;
        }
        placesAdapter.activateLoadingView();
        PlacesServiceHelper.getBarsNearby(currentPlaceLatLng, radiusSeekBar.getProgress(), new Callback<ResultPojo>() {
            @Override
            public void onResponse(Call<ResultPojo> call, Response<ResultPojo> response) {
                Log.e("request url",call.request().url().toString());
                barsList =PlacesServiceHelper.placePojoToPlaceList(response.body(),radiusSeekBar.getProgress(), currentPlaceLatLng);
                checkForFavourites(barsList);
                if (tabLayout.getSelectedTabPosition()==0){
                    placesAdapter.setNewData(barsList);
                }
            }

            @Override
            public void onFailure(Call<ResultPojo> call, Throwable t) {

            }
        });
        PlacesServiceHelper.getNightClubsNearby(currentPlaceLatLng.latitude, currentPlaceLatLng.longitude, radiusSeekBar.getProgress(), new Callback<ResultPojo>() {
            @Override
            public void onResponse(Call<ResultPojo> call, Response<ResultPojo> response) {
                clubsList =PlacesServiceHelper.placePojoToPlaceList(response.body(),radiusSeekBar.getProgress(), currentPlaceLatLng);
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
                Place currentPlace = PlaceAutocomplete.getPlace(getContext(),data);
                currentPlaceLatLng = currentPlace.getLatLng();
                currentPlaceTV.setText(currentPlace.getName());
                placesAdapter.setCurrentPlace(currentPlace);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("searchFragment", "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    /* ----------------------- Callbacks interface --------------------------*/
    public interface OpenGalleryDialog{
        void openGalleryDialog(Entities.Place place);
    }
}

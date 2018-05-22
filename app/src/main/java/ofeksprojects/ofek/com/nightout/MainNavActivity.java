package ofeksprojects.ofek.com.nightout;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import Adapters.ShowMapCallback;
import Dialogs.ImageGalleryDialogFragment;
import Dialogs.ActivateGpsDialog;
import Entities.Place;
import Fragments.FavouritesFragment;
import Fragments.HomeFragment;
import Fragments.SearchFragment;
import SQLDatabase.NightOutDao;
import ofeksprojects.ofek.com.nightout.BaseActivity.BaseDrawerActivity;

import static Fragments.SearchFragment.PREDEFINED_SEARCH_TAG;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainNavActivity extends BaseDrawerActivity implements OnMapReadyCallback, ShowMapCallback, HomeFragment.SearchForPredefinedQuery, SearchFragment.OpenGalleryDialog, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int OPEN_LOCATION_SETTINGS_REQUEST_CODE = 74;
    private static final int LOCATIOM_PERMISSION_REQUEST = 454;
    private SlidingUpPanelLayout mapPanel;
    private GoogleMap map;
    private FusedLocationProviderClient locationClient;
    private GoogleApiClient googleApiCleint;
    private long UPDATE_INTERVAL;
    private LocationCallback locationCallbacks;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NightOutDao.init(this);
        initGooglePlayServiceApi();
        setMenuItemsFragment();
        setMap();
        setPanel();
    }

    private void initGooglePlayServiceApi() {
        googleApiCleint = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this).build();
        googleApiCleint.connect();
    }

    private void setPanel() {
        mapPanel = findViewById(R.id.mapPanel_navActivity);
        mapPanel.setOverlayed(true);
    }
    private void setMap() {
        mapFragment =SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.map_container_navActivity,mapFragment).commit();
        mapFragment.getMapAsync(this);
    }
    private void setMenuItemsFragment(){
        menuItemsFragments = new SparseArray<>();
        menuItemsFragments.append(R.id.nav_search,new SearchFragment());
        menuItemsFragments.append(R.id.nav_favorites,new FavouritesFragment());
        menuItemsFragments.append(R.id.nav_home,new HomeFragment());
        setMenuItemsFragments(menuItemsFragments);
        selectNavItem(R.id.nav_home);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("MainNavActivity","onActivityResult() called");
        if (requestCode == OPEN_LOCATION_SETTINGS_REQUEST_CODE){
            Log.e("MainNavActivity","result from location settings");
            if (isGpsAvailable()&&data.hasExtra(PREDEFINED_SEARCH_TAG)){
                search(data.getIntExtra(PREDEFINED_SEARCH_TAG,TYPE_PUB));
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setScrollGesturesEnabled(false);
    }

    @Override
    public void showMap(Place place) {
        mapPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        Log.e("place lat",place.getLat()+"");
        Log.e("place lng", place.getLng()+"");
        map.addMarker(new MarkerOptions().title(place.getName()).position(new LatLng(place.getLat(),place.getLng()))).setTag(place.getPlaceId());
        map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
        .target(new LatLng(place.getLat(),place.getLng()))
        .zoom(18.0f)
        .build()));
    }

    @Override
    public void search(int type) {
        Log.e("MainNavActivity", "mainNavActivity: search() called");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},LOCATIOM_PERMISSION_REQUEST);
        }
        else {
            if (isGpsAvailable()){
                trackLocation(type);
            }
            else {
                ActivateGpsDialog dialog = new ActivateGpsDialog(this,type);
                dialog.show();
            }
        }


    }

    private boolean isGpsAvailable() {
        final LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        assert manager != null;
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)&&manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void trackLocation(final int type) {
        locationClient = new FusedLocationProviderClient(this);
        locationCallbacks = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null){
                    Log.e("MainNavActivity","onLocationResult() called: locationResult == null");
                }
                else{
                    for (Location location :locationResult.getLocations()){
                        if (location == null){
                            continue;
                        }
                        Log.e("MainNavActivity","onLocationResult() called: location  - lat: "+location.getLatitude()+" lng: "+location.getLongitude());
                        showSearchFragmnet(type,location);
                        break;
                    }
                }
            }
        };
        LocationRequest locationRequest = new LocationRequest();
        locationClient.requestLocationUpdates(locationRequest,locationCallbacks,null)
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("MainNavActivity","onFailure(): requestLocation failed");
            }
        });
    }

    private void showSearchFragmnet(int type,Location location) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(SearchFragment.PREDEFINED_SEARCH_TAG,type);
        args.putDouble(SearchFragment.LATITUDE_TAG,location.getLatitude());
        args.putDouble(SearchFragment.LONGITUDE_TAG,location.getLongitude());
        searchFragment.setArguments(args);
        menuItemsFragments.append(R.id.nav_search,searchFragment);
        selectNavItem(R.id.nav_search);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void openGalleryDialog(Place place) {
        ImageGalleryDialogFragment fragment = ImageGalleryDialogFragment.getInstance(place);
        fragment.show(getSupportFragmentManager(),"Photos Dialog");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("MainNavActivity","onConnected() called");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("MainNavActivity","onConnectionSuspended() called");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("MainNavActivity","onConnectionFailed() called");
    }
}

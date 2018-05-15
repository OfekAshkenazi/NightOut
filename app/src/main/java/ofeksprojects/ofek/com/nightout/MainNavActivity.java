package ofeksprojects.ofek.com.nightout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.ScrollableViewHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import Adapters.ShowMapCallback;
import CostumeViews.ImageGalleryDialogFragment;
import Entities.Place;
import Fragments.FavouritesFragment;
import Fragments.HomeFragment;
import Fragments.SearchFragment;
import SQLDatabase.NightOutDao;
import ofeksprojects.ofek.com.nightout.BaseActivity.BaseDrawerActivity;

public class MainNavActivity extends BaseDrawerActivity implements OnMapReadyCallback, ShowMapCallback, HomeFragment.SearchForPredefinedQuery, SearchFragment.OpenGalleryDialog{


    private MapView mapView;
    private SlidingUpPanelLayout mapPanel;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NightOutDao.init(this);
        setMenuItemsFragment();
        setMap();
        mapView.onCreate(savedInstanceState);
        setPanel();
    }

    private void setPanel() {
        mapPanel = findViewById(R.id.mapPanel_navActivity);
        mapPanel.setOverlayed(true);
        mapPanel.setScrollableView(mapView);
    }
    private void setMap() {
        mapView = (MapView) findViewById(R.id.map_navActivity);
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
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        mapView.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
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
        SearchFragment searchFragment  = new SearchFragment();
        Bundle searchArgs = new Bundle();
        searchArgs.putInt(SearchFragment.PREDEFINED_SEARCH_TAG,type);
        searchFragment.setArguments(searchArgs);
        menuItemsFragments.append(R.id.nav_search,searchFragment);
        selectNavItem(R.id.nav_search);
    }

    @Override
    public void openGalleryDialog(Place place) {
        ImageGalleryDialogFragment fragment = ImageGalleryDialogFragment.getInstance(place);
        fragment.show(getSupportFragmentManager(),"Photos Dialog");
    }
}

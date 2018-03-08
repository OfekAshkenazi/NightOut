package ofeksprojects.ofek.com.nightout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.orm.SugarContext;
import com.sothree.slidinguppanel.ScrollableViewHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import Fragments.SearchFragment;
import ofeksprojects.ofek.com.nightout.BaseActivity.BaseDrawerActivity;

public class MainNavActivity extends BaseDrawerActivity implements OnMapReadyCallback {

    private SparseArray<Fragment> menuItemsFragments;
    private MapView mapView;
    private SlidingUpPanelLayout mapPanel;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMenuItemsFragment();
        SugarContext.init(this);
        setMap();
        mapView.onCreate(savedInstanceState);
        setPanel();
    }

    private void setPanel() {
        mapPanel = findViewById(R.id.mapPanel_navActivity);
        mapPanel.setOverlayed(true);
        mapPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        mapPanel.setScrollableView(mapView);
        mapPanel.setScrollableViewHelper(new NestedScrollableViewHelper());
    }
    private void setMap() {
        mapView = (MapView) findViewById(R.id.map_navActivity);
    }
    private void setMenuItemsFragment(){
        menuItemsFragments = new SparseArray<>();
        menuItemsFragments.append(R.id.nav_search,new SearchFragment());
        setMenuItemsFragments(menuItemsFragments);
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
        SugarContext.terminate();
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }
    public class NestedScrollableViewHelper extends ScrollableViewHelper {
        @Override
        public int getScrollableViewScrollPosition(View scrollableView, boolean isSlidingUp) {
            if (scrollableView instanceof NestedScrollView) {
                if(isSlidingUp){
                    return scrollableView.getScrollY();
                } else {
                    NestedScrollView nsv = ((NestedScrollView) scrollableView);
                    View child = nsv.getChildAt(0);
                    return (child.getBottom() - (nsv.getHeight() + nsv.getScrollY()));
                }
            } else {
                return 0;
            }
        }
    }
}

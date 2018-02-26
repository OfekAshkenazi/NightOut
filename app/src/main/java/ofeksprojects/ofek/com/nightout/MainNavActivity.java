package ofeksprojects.ofek.com.nightout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import java.util.HashMap;

import Fragments.SearchFragment;
import ofeksprojects.ofek.com.nightout.BaseActivity.BaseDrawerActivity;

public class MainNavActivity extends BaseDrawerActivity {

    private SparseArray<Fragment> menuItemsFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMenuItemsFragment();

    }
    private void setMenuItemsFragment(){
        menuItemsFragments = new SparseArray<>();
        menuItemsFragments.append(R.id.nav_search,new SearchFragment());
        setMenuItemsFragments(menuItemsFragments);
    }
}

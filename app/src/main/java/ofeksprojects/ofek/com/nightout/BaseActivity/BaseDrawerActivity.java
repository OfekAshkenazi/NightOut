package ofeksprojects.ofek.com.nightout.BaseActivity;

import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import ofeksprojects.ofek.com.nightout.R;

public class BaseDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private TextView mainTV,subTV;
    private RoundedImageView profilePicIV;
    private View pager;
    SparseArray<Fragment> menuItemsFragments;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_drawer);
        initNavigationViews();
    }

    private void initNavigationViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        mainTV = headerLayout.findViewById(R.id.mainTV_header);
        subTV = headerLayout.findViewById(R.id.subTV_header);
        profilePicIV = headerLayout.findViewById(R.id.profilePicIV_header);
        setHeaderContent(new HeaderDetails("Ofek Regev","regevofek@live.com",""));
    }

    // giving access to the navigation header
    void setHeaderInfo(String mainTV,String subTV,String profilePicUrl){
        HeaderDetails headerDetails = new HeaderDetails(mainTV, subTV, profilePicUrl);
        setHeaderContent(headerDetails);
    }

    // saving the state of the header content
    private void setHeaderContent(@NonNull HeaderDetails details) {
        mainTV.setText(details.getMainText());
        subTV.setText(details.getSubText());
        if (details.profilePicUrl.isEmpty()){
            Picasso.with(this).load(R.mipmap.ic_launcher).into(profilePicIV);
        }
        else {
            Picasso.with(this).load(details.getProfilePicUrl()).into(profilePicIV);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void setMenuItemsFragments(SparseArray<Fragment> menuItemsFragments){
        this.menuItemsFragments = menuItemsFragments;
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Integer id = item.getItemId();
        if (menuItemsFragments!=null && menuItemsFragments.indexOfKey(id) >= 0){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer_navActivity,menuItemsFragments.get(id)).commit();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private static class HeaderDetails{
        private String mainText,subText,profilePicUrl;

        public HeaderDetails(String mainText, String subText, String profilePicUrl) {
            this.mainText = mainText;
            this.subText = subText;
            this.profilePicUrl = profilePicUrl;
        }

        public String getMainText() {
            return mainText;
        }

        public String getSubText() {
            return subText;
        }

        public String getProfilePicUrl() {
            return profilePicUrl;
        }
    }
}

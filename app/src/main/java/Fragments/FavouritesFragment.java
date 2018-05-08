package Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import Adapters.FavouritesAdapter;
import Adapters.ShowMapCallback;
import Entities.Place;
import PlacesApiService.PlacesServiceHelper;
import SQLDatabase.NightOutDao;
import ofeksprojects.ofek.com.nightout.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment {


    private RecyclerView favouritesRV;
    private ArrayList<Place> favPlaces;
    private FavouritesAdapter favouritesAdapter;

    public FavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favouritesAdapter = new FavouritesAdapter(null, (ShowMapCallback) getActivity(),getContext());
        favouritesRV = view.findViewById(R.id.favouritesRV);
        favouritesRV.setLayoutManager(new LinearLayoutManager(view.getContext()));
        favouritesRV.setAdapter(favouritesAdapter);
        Thread thread  = new Thread(new Runnable() {
            @Override
            public void run() {
                favouritesRV.post(new Runnable() {
                    @Override
                    public void run() {
                        favouritesAdapter.activateLoadingView();
                    }
                });
                favPlaces = NightOutDao.getAllFavouritePlaces();
                for (Place place: favPlaces){
                    place.setPhotos(PlacesServiceHelper.getPlacePhotos(place.getPlaceId()));
                }
                onFavPlacesLoaded();
            }
        });
        thread.start();
    }

    private void onFavPlacesLoaded() {
        favouritesRV.post(new Runnable() {
            @Override
            public void run() {
                favouritesAdapter.setNewData(favPlaces);
            }
        });
    }
}

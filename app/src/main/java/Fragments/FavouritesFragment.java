package ofeksprojects.ofek.com.nightout;


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


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment {


    private RecyclerView favouritesRV;
    private ArrayList<Place> favPlaces;

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
        favouritesRV = view.findViewById(R.id.favouritesRV);
        favouritesRV.setLayoutManager(new LinearLayoutManager(view.getContext()));
        Thread thread  = new Thread(new Runnable() {
            @Override
            public void run() {
                favPlaces = NightOutDao.getAllFavouritePlaces();
                for (Place place: favPlaces){
                    place.setPhotos(PlacesServiceHelper.getPlacePhotos(place));
                }
                onFavPlacesLoaded();
            }
        });
        thread.start();
    }

    private void onFavPlacesLoaded() {
        final FavouritesAdapter favouritesAdapter = new FavouritesAdapter(favPlaces, (ShowMapCallback) getActivity());
        favouritesRV.post(new Runnable() {
            @Override
            public void run() {
                favouritesRV.setAdapter(favouritesAdapter);
            }
        });
    }
}

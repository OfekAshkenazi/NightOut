package Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ofeksprojects.ofek.com.nightout.R;

/**
 * Created by Ofek on 17-Feb-18.
 */

public class HomeFragment extends Fragment {
    private Button pubsBtn,clubsBtn;
    private SearchForPredefinedQuery searchCall;
    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchCall = (SearchForPredefinedQuery) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pubsBtn = view.findViewById(R.id.pubsBtn_homeFrag);
        clubsBtn = view.findViewById(R.id.clubsBtn_homeFrag);
        pubsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchCall.search(SearchForPredefinedQuery.TYPE_PUB);
            }
        });
        clubsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchCall.search(SearchForPredefinedQuery.TYPE_CLUB);
            }
        });
    }

    public interface SearchForPredefinedQuery{
        public static final int TYPE_PUB=1;
        public static final int TYPE_CLUB=2;
        void search(int type);
    }

}

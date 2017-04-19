package es.npatarino.android.gotchallenge.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import es.npatarino.android.gotchallenge.HomeActivity;
import es.npatarino.android.gotchallenge.R;
import es.npatarino.android.gotchallenge.adapter.GoTHouseAdapter;
import es.npatarino.android.gotchallenge.model.GoTCharacter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class GoTHousesListFragment extends Fragment {

    private static final String TAG = "GoTHousesListFragment";

    public GoTHousesListFragment() {
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        final ContentLoadingProgressBar pb = (ContentLoadingProgressBar) rootView.findViewById(R.id.pb);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv);

        final GoTHouseAdapter adp = new GoTHouseAdapter(getActivity());
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setHasFixedSize(true);
        rv.setAdapter(adp);

        new Thread(new Runnable() {

            @Override
            public void run() {


                URL obj = null;
                try {
                    if (((HomeActivity) getActivity()).characters == null) {
                        ((HomeActivity) getActivity()).fillCharacters(obj);
                    }
                    GoTHousesListFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<GoTCharacter.GoTHouse> hs = new ArrayList<GoTCharacter.GoTHouse>();
                            for (int i = 0; i < ((HomeActivity) getActivity()).characters.size(); i++) {
                                boolean b = false;
                                for (int j = 0; j < hs.size(); j++) {
                                    if (hs.get(j).getN().equalsIgnoreCase(((HomeActivity) getActivity()).characters.get(i).getHn())) {
                                        b = true;
                                    }
                                }
                                if (!b) {
                                    if (((HomeActivity) getActivity()).characters.get(i).getHi() != null && !((HomeActivity) getActivity()).characters.get(i).getHi().isEmpty()) {
                                        GoTCharacter.GoTHouse h = new GoTCharacter.GoTHouse();
                                        h.setI(((HomeActivity) getActivity()).characters.get(i).getHi());
                                        ;
                                        h.setN(((HomeActivity) getActivity()).characters.get(i).getHn());
                                        h.setU(((HomeActivity) getActivity()).characters.get(i).getHu());
                                        hs.add(h);
                                        b = false;
                                    }
                                }
                            }
                            adp.addAll(hs);
                            adp.notifyDataSetChanged();
                            pb.hide();
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }).start();
        return rootView;
    }


}
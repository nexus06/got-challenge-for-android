package es.npatarino.android.gotchallenge.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.*;
import es.npatarino.android.gotchallenge.HomeActivity;
import es.npatarino.android.gotchallenge.R;
import es.npatarino.android.gotchallenge.adapter.GoTAdapter;

import java.io.IOException;
import java.net.URL;

public class GoTListFragment extends Fragment {

    private static final String TAG = "GoTListFragment";

    private GoTAdapter adp = null;

    public GoTListFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = (menu.findItem(R.id.action_search));
        menuItem.setVisible(true);
        SearchManager manager = (SearchManager) (getActivity().getSystemService(Context.SEARCH_SERVICE));
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                filterCharacter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterCharacter(s);
                return true;
            }

        });
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        final ContentLoadingProgressBar pb = (ContentLoadingProgressBar) rootView.findViewById(R.id.pb);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv);
        setHasOptionsMenu(true);
        adp = new GoTAdapter(getActivity());
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

                    GoTListFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adp.addAll(((HomeActivity) getActivity()).characters);
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

    public void filterCharacter(String s) {
        if (adp != null && !s.isEmpty()) {
            adp.filterCharacter(s, ((HomeActivity) getActivity()).characters);
            adp.notifyDataSetChanged();
        } else if (adp != null && s.isEmpty()) {
            adp.replaceGoTCharacter(((HomeActivity) getActivity()).characters);
            adp.notifyDataSetChanged();
        }

    }
}
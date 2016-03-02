package es.npatarino.android.gotchallenge;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    SectionsPagerAdapter spa;
    ViewPager vp;
    Toolbar toolbar;
    TabLayout tabLayout;
    private GoTListFragment goTListFragment;
    private GoTHousesListFragment goTHousesListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        setSpa(new SectionsPagerAdapter(getSupportFragmentManager()));

        setVp((ViewPager) findViewById(R.id.container));
        getVp().setAdapter(getSpa());

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(getVp());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        //menuItem.setVisible(false);
       SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        // search.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                goTListFragment.filterCharacter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                goTListFragment.filterCharacter(s);
                return true;
            }

        });
        return true;
    }


    public SectionsPagerAdapter getSpa() {
        return spa;
    }

    public void setSpa(SectionsPagerAdapter spa) {
        this.spa = spa;
    }

    public ViewPager getVp() {
        return vp;
    }

    public void setVp(ViewPager vp) {
        this.vp = vp;
    }



    public static class GoTListFragment extends Fragment {

        private static final String TAG = "GoTListFragment";

        private GoTAdapter adp = null;

        List<GoTCharacter> characters = null;

        public GoTListFragment() {
        }

         @Override
         public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
             super.onCreateOptionsMenu(menu, inflater);
             MenuItem menuItem =((HomeActivity)getActivity()).toolbar.getMenu().findItem(R.id.action_search);
             menuItem.setVisible(true);
             SearchManager manager = (SearchManager) (getActivity().getSystemService(Context.SEARCH_SERVICE));
             SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
             search.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
             // search.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
             search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                 @Override
                 public boolean onQueryTextSubmit(String s) {
                     if(adp!=null && s!=null){
                         adp.filterCharacter(s);
                         adp.notifyDataSetChanged();
                     }else if(adp!=null && s.isEmpty()) {
                         adp.replaceGoTCharacter(characters);
                          adp.notifyDataSetChanged();
                     }

                     return true;
                 }

                 @Override
                 public boolean onQueryTextChange(String s) {
                     return true;
                 }

             });
         }




        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            return super.onOptionsItemSelected(item);
        }


        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_list, container, false);
            final ContentLoadingProgressBar pb = (ContentLoadingProgressBar) rootView.findViewById(R.id.pb);
            RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv);

            adp = new GoTAdapter(getActivity());
            rv.setLayoutManager(new LinearLayoutManager(getActivity()));
            rv.setHasFixedSize(true);
            rv.setAdapter(adp);

            new Thread(new Runnable() {

                @Override
                public void run() {
                    String url = "http://ec2-52-18-202-124.eu-west-1.compute.amazonaws.com:3000/characters";

                    URL obj = null;
                    try {
                        obj = new URL(url);
                        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                        con.setRequestMethod("GET");
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        Type listType = new TypeToken<ArrayList<GoTCharacter>>() {
                        }.getType();
                        characters = new Gson().fromJson(response.toString(), listType);
                        GoTListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adp.addAll(characters);
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
            if(adp!=null && s!=null){
                adp.filterCharacter(s);
                adp.notifyDataSetChanged();
            }else if(adp!=null && s.isEmpty()) {
                adp.replaceGoTCharacter(characters);
                adp.notifyDataSetChanged();
            }

        }
    }

    public static class GoTHousesListFragment extends Fragment {

        private static final String TAG = "GoTHousesListFragment";

        public GoTHousesListFragment() {
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            menu.findItem(R.id.action_search).setVisible(false);
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
                    String url = "http://ec2-52-18-202-124.eu-west-1.compute.amazonaws.com:3000/characters";

                    URL obj = null;
                    try {
                        obj = new URL(url);
                        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                        con.setRequestMethod("GET");
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        Type listType = new TypeToken<ArrayList<GoTCharacter>>() {
                        }.getType();
                        final List<GoTCharacter> characters = new Gson().fromJson(response.toString(), listType);
                        GoTHousesListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<GoTCharacter.GoTHouse> hs = new ArrayList<GoTCharacter.GoTHouse>();
                                for (int i = 0; i < characters.size(); i++) {
                                    boolean b = false;
                                    for (int j = 0; j < hs.size(); j++) {
                                        if (hs.get(j).n.equalsIgnoreCase(characters.get(i).hn)) {
                                            b = true;
                                        }
                                    }
                                    if (!b) {
                                        if (characters.get(i).hi != null && !characters.get(i).hi.isEmpty()) {
                                            GoTCharacter.GoTHouse h = new GoTCharacter.GoTHouse();
                                            h.i = characters.get(i).hi;
                                            h.n = characters.get(i).hn;
                                            h.u = characters.get(i).hu;
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new GoTListFragment();
            } else {
                return new GoTHousesListFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Characters";
                case 1:
                    return "Houses";
            }
            return null;
        }

        //http://stackoverflow.com/questions/14035090/how-to-get-existing-fragments-when-using-fragmentpageradapter
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    goTListFragment = (GoTListFragment) createdFragment;
                    break;
                case 1:
                    goTHousesListFragment = (GoTHousesListFragment) createdFragment;
                    break;
            }
            return createdFragment;
        }
    }

    static class GoTAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<GoTCharacter> gcs;

        private Activity a;

        public GoTAdapter(Activity activity) {
            this.gcs = new ArrayList<>();
            a = activity;
        }

        void addAll(Collection<GoTCharacter> collection) {
            for (int i = 0; i < collection.size(); i++) {
                gcs.add((GoTCharacter) collection.toArray()[i]);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GotCharacterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.got_character_row, parent, false));
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            GotCharacterViewHolder gotCharacterViewHolder = (GotCharacterViewHolder) holder;
            gotCharacterViewHolder.render(gcs.get(position));
            ((GotCharacterViewHolder) holder).imp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Intent intent = new Intent(((GotCharacterViewHolder) holder).itemView.getContext(), DetailActivity.class);
                    intent.putExtra("description", gcs.get(position).d);
                    intent.putExtra("name", gcs.get(position).n);
                    intent.putExtra("imageUrl", gcs.get(position).iu);
                    ((GotCharacterViewHolder) holder).itemView.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return gcs.size();
        }

        private void filterCharacter(String strFilter) {
            final List<GoTCharacter> filteredCharacter = new ArrayList<>();
            for(GoTCharacter character:gcs){
                if(character.getN().contains(strFilter)){
                    filteredCharacter.add(character);
                }
            }
            gcs.clear();
            gcs.addAll(filteredCharacter);
        }

        private void replaceGoTCharacter(List<GoTCharacter> filteredCharacter) {
            gcs.clear();
            gcs.addAll(filteredCharacter);
        }

        class GotCharacterViewHolder extends RecyclerView.ViewHolder {

            private static final String TAG = "GotCharacterViewHolder";
            ImageView imp;
            TextView tvn;

            public GotCharacterViewHolder(View itemView) {
                super(itemView);
                imp = (ImageView) itemView.findViewById(R.id.ivBackground);
                tvn = (TextView) itemView.findViewById(R.id.tv_name);
            }

            public void render(final GoTCharacter goTCharacter) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        URL url = null;
                        try {
                            url = new URL(goTCharacter.iu);
                            final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            a.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imp.setImageBitmap(bmp);
                                    tvn.setText(goTCharacter.n);
                                }
                            });
                        } catch (IOException e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }
                    }
                }).start();
            }
        }

    }

    static class GoTHouseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<GoTCharacter.GoTHouse> gcs;
        private Activity a;

        public GoTHouseAdapter(Activity activity) {
            this.gcs = new ArrayList<>();
            a = activity;
        }

        void addAll(Collection<GoTCharacter.GoTHouse> collection) {
            for (int i = 0; i < collection.size(); i++) {
                gcs.add((GoTCharacter.GoTHouse) collection.toArray()[i]);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GotCharacterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.got_house_row, parent, false));
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            GotCharacterViewHolder gotCharacterViewHolder = (GotCharacterViewHolder) holder;
            gotCharacterViewHolder.render(gcs.get(position));
        }

        @Override
        public int getItemCount() {
            return gcs.size();
        }

        class GotCharacterViewHolder extends RecyclerView.ViewHolder {

            private static final String TAG = "GotCharacterViewHolder";
            ImageView imp;

            public GotCharacterViewHolder(View itemView) {
                super(itemView);
                imp = (ImageView) itemView.findViewById(R.id.ivBackground);
            }

            public void render(final GoTCharacter.GoTHouse goTHouse) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        URL url = null;
                        try {
                            url = new URL(goTHouse.u);
                            final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            a.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imp.setImageBitmap(bmp);
                                }
                            });
                        } catch (IOException e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }
                    }
                }).start();
            }
        }

    }
}

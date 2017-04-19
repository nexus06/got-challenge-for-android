package es.npatarino.android.gotchallenge;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.LruCache;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import es.npatarino.android.gotchallenge.fragments.GoTHousesListFragment;
import es.npatarino.android.gotchallenge.fragments.GoTListFragment;
import es.npatarino.android.gotchallenge.fragments.RetainedFragment;
import es.npatarino.android.gotchallenge.model.GoTCharacter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {


    //tag for log
    public static final String TAG = HomeActivity.class.getCanonicalName();

    //extras passed between activities
    public static final String DESCRIPTION_EXTRA = "DESCRIPTION_EXTRA";
    public static final String NAME_EXTRA = "NAME_EXTRA";
    public static final String IMAGE_URL_EXTRA = "IMAGE_URL_EXTRA";
    public static final String CHARACTRS_EXTRA = "CHARACTRS_EXTRA";

    /*memory cache manages offline mode. It stores already decoded bitmaps*/
    public static LruCache<String, Bitmap> mMemoryCache;
    /*loaded info*/
    public List<GoTCharacter> characters = null;
    private SectionsPagerAdapter spa;
    private ViewPager vp;
    private Toolbar toolbar;

    /*OFFLINE MANAGING*/
    private TabLayout tabLayout;
    /*retain fragment allow mantain info between states changes. We use this to
    * store memory cache*/
    private RetainedFragment retainFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*transition fade in effect*/
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        setSpa(new SectionsPagerAdapter(getSupportFragmentManager()));

        setVp((ViewPager) findViewById(R.id.container));
        getVp().setAdapter(getSpa());

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(getVp());


        initialyzeMemoryCache();
    }

    private void initialyzeMemoryCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 4;

        //check if retain fragment has been already created
        if (retainFragment == null) {
            retainFragment = RetainedFragment.findOrCreateRetainFragment(getSupportFragmentManager());
        }

        //try to get memory cache from retain fragment. E.g if state changes(e.g orientation changes)
        if (retainFragment.mRetainedCache == null && mMemoryCache == null) {
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getByteCount() / 1024;
                }
            };
            retainFragment.mRetainedCache = mMemoryCache;
        } else if (retainFragment.mRetainedCache != null) {
            mMemoryCache = retainFragment.mRetainedCache;
        }
        //force put offLine bitmap
        Utils.addBitmapToMemoryCache(Utils.CACHED_BITMAP_KEY, BitmapFactory.decodeResource(getResources(), R.mipmap.off_line), mMemoryCache);
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


    public void fillCharacters(URL obj) throws IOException {
        obj = new URL(Utils.URL_CHARACTERS);
        String info = "";
        //get info from server if online
        if (Utils.isNetworkAvailable(this)) {
            info = Utils.downloadInfo(obj);
            Utils.persistInfo(PreferenceManager.
                    getDefaultSharedPreferences(this), info);
        } else {//take from stored info in settings
            info = Utils.getStoredInfo(PreferenceManager.
                    getDefaultSharedPreferences(this));
        }

        Type listType = new TypeToken<ArrayList<GoTCharacter>>() {
        }.getType();

        characters = new Gson().fromJson(info, listType);
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

    }
}

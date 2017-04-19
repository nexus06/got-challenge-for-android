package es.npatarino.android.gotchallenge.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import es.npatarino.android.gotchallenge.R;

/**
 * Created by nexus on 3/2/16.
 */
public class RetainedFragment extends Fragment {
    private static final String TAG = "RetainedFragment";
    public LruCache<String, Bitmap> mRetainedCache;

    public RetainedFragment() {
    }

    public static RetainedFragment findOrCreateRetainFragment(FragmentManager fm) {
        RetainedFragment fragment = (RetainedFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new RetainedFragment();
            fm.beginTransaction().add(fragment, TAG).commit();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.retained_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }
}
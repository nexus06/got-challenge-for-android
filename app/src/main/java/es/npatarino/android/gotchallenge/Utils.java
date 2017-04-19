package es.npatarino.android.gotchallenge;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.LruCache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nexus on 3/2/16.
 * <p/>
 * This helper class encapsulates several static constants and methods that are used to
 * download info, connection and caching operations called from different activities.
 */
public class Utils {

    /*separator used for character info in bundle passed to DetailActivity*/
    public static final String CHARACTER_SEPARATOR = ",";
    /*separator used for pass image uri info in a bundle to DetailActivity*/
    public static final java.lang.String URL_SEPARATOR = "#";
    public static final String CACHED_BITMAP_KEY = "CACHED_BITMAP_KEY";
    /*GoT info server url*/
    public static final String URL_CHARACTERS = "http://ec2-52-18-202-124.eu-west-1.compute.amazonaws.com:3000/characters";
    /*requested json is stored as string in settings with this key*/
    private static final String STORED_INFO_KEY = "STORED_INFO_KEY";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) (context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap, LruCache<String, Bitmap> mMemoryCache) {
        if (mMemoryCache.get(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key, URL url, LruCache<String, Bitmap> mMemoryCache) {
        Bitmap bm;
        bm = mMemoryCache.get(key);
        if (bm == null) {
            try {
                bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                addBitmapToMemoryCache(key, bm, mMemoryCache);
            } catch (IOException e) {
                Log.e("getBitmapFromMemCache", Log.getStackTraceString(e));
                bm = mMemoryCache.get(CACHED_BITMAP_KEY);
            }

        }

        return bm;
    }


    public static String downloadInfo(URL obj) throws IOException {
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    public static void persistInfo(SharedPreferences prefs, String sInfo) {
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putString(STORED_INFO_KEY, sInfo);
        prefEditor.commit();
    }

    public static String getStoredInfo(SharedPreferences prefs) {
        return prefs.getString(STORED_INFO_KEY, "");
    }
}

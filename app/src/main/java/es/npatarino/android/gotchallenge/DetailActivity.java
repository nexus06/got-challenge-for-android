package es.npatarino.android.gotchallenge;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import es.npatarino.android.gotchallenge.adapter.CharacterAdapter;
import es.npatarino.android.gotchallenge.model.GoTCharacter;
import es.npatarino.android.gotchallenge.model.SimpleCharacter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailActivity extends AppCompatActivity {


    private static final String TAG = DetailActivity.class.getSimpleName();


    private ContentLoadingProgressBar pb = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_detail);

        final ImageView ivp = (ImageView) findViewById(R.id.iv_photo);
        final TextView tvn = (TextView) findViewById(R.id.tv_name);
        final TextView tvd = (TextView) findViewById(R.id.tv_description);
        final GridView lvc = (GridView) findViewById(R.id.grid_characters);

        pb = (ContentLoadingProgressBar) findViewById(R.id.pb);


        final String d = getIntent().getStringExtra(HomeActivity.DESCRIPTION_EXTRA);
        final String n = getIntent().getStringExtra(HomeActivity.NAME_EXTRA);
        final String i = getIntent().getStringExtra(HomeActivity.IMAGE_URL_EXTRA);
        final String chars = getIntent().getStringExtra(HomeActivity.CHARACTRS_EXTRA);


        Toolbar toolbar = (Toolbar) findViewById(R.id.t);
        toolbar.setTitle(n);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(i);
                    final Bitmap bmp;
                    bmp = Utils.getBitmapFromMemCache(n, url, HomeActivity.mMemoryCache);


                    final ArrayList<String> charactersList = new ArrayList<String>();
                    if (chars != null && !chars.isEmpty()) {
                        ;
                        Collections.addAll(charactersList, chars.split(","));
                    }


                    DetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivp.setImageBitmap(bmp);
                            tvn.setText(n);
                            tvd.setText(d);
                            if (!charactersList.isEmpty()) {
                                ivp.setVisibility(View.GONE);

                                tvd.setVisibility(View.GONE);

                                List<GoTCharacter> characters = new ArrayList<GoTCharacter>();
                                for (String chararctStr : charactersList) {
                                    GoTCharacter character = new GoTCharacter();
                                    String[] l = chararctStr.split(Utils.URL_SEPARATOR);
                                    character.setN(l[0]);
                                    character.setIu(l[1]);
                                    characters.add(character);
                                }
                                new GetAsyncImages(lvc, pb).execute(characters);

                            } else {
                                lvc.setVisibility(View.GONE);
                                pb.hide();
                            }
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }).start();
    }

    /*this class preload all character images to load them at same time in adapter*/
    private class GetAsyncImages extends AsyncTask<List<GoTCharacter>, Void, List<SimpleCharacter>> {

        private final GridView grid;
        private final ContentLoadingProgressBar dialog;


        GetAsyncImages(GridView gridView, ContentLoadingProgressBar dialog) {
            this.grid = gridView;
            this.dialog = dialog;

        }

        @Override
        protected List<SimpleCharacter> doInBackground(List<GoTCharacter>... params) {
            URL url = null;
            List<SimpleCharacter> allCharacters = new ArrayList<>();
            for (GoTCharacter character : params[0]) {
                try {

                    url = new URL(character.getIu());
                    final Bitmap bmp;
                    bmp = Utils.getBitmapFromMemCache(character.getN(), url, HomeActivity.mMemoryCache);

                    allCharacters.add(new SimpleCharacter(character.getN(), bmp));
                } catch (MalformedURLException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
            return allCharacters;
        }

        @Override
        protected void onPostExecute(List<SimpleCharacter> result) {
            grid.setAdapter(new CharacterAdapter(DetailActivity.this, result));
            dialog.hide();
        }

        @Override
        protected void onPreExecute() {
            dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}

package es.npatarino.android.gotchallenge.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import es.npatarino.android.gotchallenge.DetailActivity;
import es.npatarino.android.gotchallenge.HomeActivity;
import es.npatarino.android.gotchallenge.R;
import es.npatarino.android.gotchallenge.Utils;
import es.npatarino.android.gotchallenge.model.GoTCharacter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GoTAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GoTCharacter> gcs;

    private Activity a;

    public GoTAdapter(Activity activity) {
        this.gcs = new ArrayList<>();
        a = activity;
    }

    public void addAll(Collection<GoTCharacter> collection) {
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
                intent.putExtra(HomeActivity.DESCRIPTION_EXTRA, gcs.get(position).getD());
                intent.putExtra(HomeActivity.NAME_EXTRA, gcs.get(position).getN());
                intent.putExtra(HomeActivity.IMAGE_URL_EXTRA, gcs.get(position).getIu());
                ((GotCharacterViewHolder) holder).itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gcs.size();
    }

    public void filterCharacter(String strFilter, List<GoTCharacter> allCharacter) {
        final List<GoTCharacter> filteredCharacterFileter = new ArrayList<>();
        for (GoTCharacter character : allCharacter) {
            if (character.getN().contains(strFilter)) {
                filteredCharacterFileter.add(character);
            }
        }
        gcs.clear();
        gcs.addAll(filteredCharacterFileter);
    }

    public void replaceGoTCharacter(List<GoTCharacter> filteredCharacter) {
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
                        url = new URL(goTCharacter.getIu());
                        final Bitmap bmp;
                        bmp = Utils.getBitmapFromMemCache(goTCharacter.getN(), url, HomeActivity.mMemoryCache);
                        a.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imp.setImageBitmap(bmp);
                                tvn.setText(goTCharacter.getN());
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
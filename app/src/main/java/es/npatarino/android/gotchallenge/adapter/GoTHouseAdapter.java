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

public class GoTHouseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final List<GoTCharacter.GoTHouse> gcs;
    private Activity a;

    public GoTHouseAdapter(Activity activity) {
        this.gcs = new ArrayList<>();
        a = activity;
    }

    public void addAll(Collection<GoTCharacter.GoTHouse> collection) {
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

        ((GotCharacterViewHolder) holder).imp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(((GotCharacterViewHolder) holder).itemView.getContext(), DetailActivity.class);
                intent.putExtra(HomeActivity.CHARACTRS_EXTRA, getStrCharacters(gcs.get(position).getI()));
                intent.putExtra(HomeActivity.NAME_EXTRA, gcs.get(position).getN());
                intent.putExtra(HomeActivity.IMAGE_URL_EXTRA, gcs.get(position).getU());
                ((GotCharacterViewHolder) holder).itemView.getContext().startActivity(intent);
            }
        });
    }

    private String getStrCharacters(String idHouse) {
        StringBuilder str = new StringBuilder();
        for (GoTCharacter character : ((HomeActivity) a).characters) {
            if (character.getHi().equals(idHouse)) {
                if (str.length() > 0) {
                    str.append(Utils.CHARACTER_SEPARATOR);
                }
                str.append(character.getN());
                str.append(Utils.URL_SEPARATOR);
                str.append(character.getIu());
            }
        }
        return str.toString();
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
                        url = new URL(goTHouse.getU());
                        final Bitmap bmp;
                        bmp = Utils.getBitmapFromMemCache(goTHouse.getN(), url, HomeActivity.mMemoryCache);

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
package es.npatarino.android.gotchallenge.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import es.npatarino.android.gotchallenge.DetailActivity;
import es.npatarino.android.gotchallenge.R;
import es.npatarino.android.gotchallenge.model.SimpleCharacter;

import java.lang.ref.WeakReference;
import java.util.List;

public class CharacterAdapter extends BaseAdapter implements View.OnClickListener {

    private static final String TAG = CharacterAdapter.class.getCanonicalName();

    private final WeakReference<Activity> context;
    private final List<SimpleCharacter> simpleCharacters;
    private TextView txvName;
    private ImageView imvCharacter;


    public CharacterAdapter(DetailActivity detailActivity, List<SimpleCharacter> simpleCharacters) {
        this.simpleCharacters = simpleCharacters;
        this.context = new WeakReference<Activity>(detailActivity);
    }

    @Override
    public int getCount() {
        return simpleCharacters.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        //recycle if it si possible
        if (convertView == null) {
            final LayoutInflater inflater = (LayoutInflater) this.context.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.got_characters_cell, parent, false);
        }

        final SimpleCharacter curCharacter = simpleCharacters.get(position);
        this.txvName = (TextView) row.findViewById(R.id.tv_name);
        this.txvName.setText(curCharacter.getN());
        this.imvCharacter = (ImageView) row.findViewById(R.id.ivBackground);
        imvCharacter.setImageBitmap(curCharacter.getI());
        return row;
    }

    @Override
    public void onClick(View v) {

    }
}
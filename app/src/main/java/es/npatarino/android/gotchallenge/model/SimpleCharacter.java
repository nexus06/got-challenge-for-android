package es.npatarino.android.gotchallenge.model;

import android.graphics.Bitmap;

public class SimpleCharacter {

    private String n;

    private Bitmap i;


    public SimpleCharacter(String name, Bitmap bitmap) {
        this.n = name;
        this.i = bitmap;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }


    public Bitmap getI() {
        return i;
    }

    public void setI(Bitmap i) {
        this.i = i;
    }


}

package com.techery.spares.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PicassoImageLoader implements ImageLoader {
    private final Context context;

    public PicassoImageLoader(Context context) {
        this.context = context;
    }

    @Override
    public void loadImage(String url, ImageView imageView) {
        Picasso.with(this.context).load(url).into(imageView);
    }

    public Picasso getPicasso() {
        return Picasso.with(this.context);
    }
}

package com.techery.spares.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public interface ImageLoader {
    public void loadImage(String url, ImageView imageView);
}

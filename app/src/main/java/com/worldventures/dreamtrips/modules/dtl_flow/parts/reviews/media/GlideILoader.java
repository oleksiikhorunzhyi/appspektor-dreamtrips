package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.media;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

/**
 * Created by Andres Rubiano Del Chiaro on 16/04/16.
 */
public class GlideILoader implements ImageLoader{

    @Override
    public void load(String url, ImageView imageView)
    {
        Glide.with(imageView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @Override
    public void loadLocal(String path, ImageView imageView) {
        Glide.with(imageView.getContext()).load(new File(path)).into(imageView);
    }
}

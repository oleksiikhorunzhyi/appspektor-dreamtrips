package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.media;

import android.widget.ImageView;

/**
 * Created by Andres Rubiano Del Chiaro on 23/09/2016.
 */

public interface ImageLoader {

    void load(String url, ImageView imageView);
    void loadLocal(String path, ImageView imageView);
}

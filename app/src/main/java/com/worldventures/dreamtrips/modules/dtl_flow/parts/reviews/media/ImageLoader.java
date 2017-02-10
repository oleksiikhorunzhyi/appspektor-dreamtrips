package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.media;

import android.widget.ImageView;


public interface ImageLoader {

    void load(String url, ImageView imageView);
    void loadLocal(String path, ImageView imageView);
}

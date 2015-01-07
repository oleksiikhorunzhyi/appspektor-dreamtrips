package com.worldventures.dreamtrips.utils;

import android.net.Uri;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.worldventures.dreamtrips.R;

public class UniversalImageLoader  {


    public static final DisplayImageOptions OP_AVATAR = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .showImageForEmptyUri(R.drawable.fake_avatar)
            .showImageOnFail(R.drawable.fake_avatar)
            .showImageOnLoading(R.drawable.fake_avatar)
            .displayer(new FadeInBitmapDisplayer(300))
            .build();
    public static final DisplayImageOptions OP_COVER = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .showImageForEmptyUri(R.drawable.fake_cover)
            .showImageOnFail(R.drawable.fake_cover)
            .showImageOnLoading(R.drawable.fake_cover)
            .displayer(new FadeInBitmapDisplayer(300))
            .build();

    public UniversalImageLoader() {
    }

    public void loadImage(String url, ImageView imageView, DisplayImageOptions displayImageOptions) {
        ImageLoader.getInstance().displayImage(url, imageView, displayImageOptions);
    }

    public void loadImage(Uri uri, ImageView imageView) {
        loadImage(uri, imageView, null);
    }

    public void loadImage(Uri uri, ImageView imageView, DisplayImageOptions displayImageOptions) {
        String uriS = "";
        if (uri != null) {
            uriS = uri.toString();
        }
      loadImage(uriS, imageView, displayImageOptions);
    }

}

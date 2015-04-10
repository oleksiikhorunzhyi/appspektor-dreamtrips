package com.worldventures.dreamtrips.core.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.worldventures.dreamtrips.R;

public class UniversalImageLoader {

    public static final String PATTERN = "?width=%d&height=%d";

    public static final DisplayImageOptions OP_AVATAR = new DisplayImageOptions.Builder()
            .cacheOnDisk(false)
            .cacheInMemory(false)
            .showImageForEmptyUri(R.drawable.ic_avatar_placeholder)
            .showImageOnFail(R.drawable.ic_avatar_placeholder)
            .displayer(new FadeInBitmapDisplayer(300))
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .build();
    public static final DisplayImageOptions OP_AVATAR_WITH_CACHE = new DisplayImageOptions.Builder()
            .cacheOnDisk(false)
            .cacheInMemory(false)
            .showImageForEmptyUri(R.drawable.ic_avatar_placeholder)
            .showImageOnFail(R.drawable.ic_avatar_placeholder)
            .displayer(new FadeInBitmapDisplayer(300))
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .build();
    public static final DisplayImageOptions OP_COVER = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .showImageForEmptyUri(R.drawable.ic_cover_place_holder)
            .showImageOnFail(R.drawable.ic_cover_place_holder)
            .showImageOnLoading(R.drawable.ic_cover_place_holder)
            .displayer(new FadeInBitmapDisplayer(300))
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .build();
    public static final DisplayImageOptions OP_DEF = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .showImageForEmptyUri(R.drawable.ic_trip_image_placeholder)
            .showImageOnFail(R.drawable.ic_trip_image_placeholder)
            .showImageOnLoading(R.drawable.ic_trip_image_placeholder)
            .displayer(new FadeInBitmapDisplayer(300))
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .build();
    public static final DisplayImageOptions OP_TRIP_PHOTO = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .showImageForEmptyUri(R.drawable.ic_trip_image_placeholder)
            .showImageOnFail(R.drawable.ic_trip_image_placeholder)
            .showImageOnLoading(R.drawable.ic_trip_image_placeholder)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .build();
    public static final DisplayImageOptions OP_FULL_SCREEN = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(false)
            .showImageForEmptyUri(R.drawable.ic_trip_image_placeholder)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY)
            .build();
    public static final DisplayImageOptions OP_LIST_SCREEN = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(false)
            .resetViewBeforeLoading(true)
            .showImageForEmptyUri(R.drawable.ic_trip_image_placeholder)
            .showImageOnFail(R.drawable.ic_trip_image_placeholder)
            .showImageOnLoading(R.drawable.ic_trip_image_placeholder)
            .displayer(new FadeInBitmapDisplayer(300))
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .build();


    public UniversalImageLoader() {
    }

    public void loadImage(String url, ImageView imageView, DisplayImageOptions displayImageOptions) {
        loadImage(url, imageView, displayImageOptions, null);
    }

    public void loadImage(Uri uri, ImageView imageView) {
        loadImage(uri, imageView, null);
    }

    public void loadImage(Uri uri, ImageView imageView, DisplayImageOptions displayImageOptions) {
        String uriS = "";
        if (uri != null) {
            uriS = uri.toString();
        }

        loadImage(uriS, imageView, displayImageOptions, new SimpleImageLoadingListener());
    }

    public void loadImage(String url, ImageView imageView, DisplayImageOptions displayImageOptions, ImageLoadingListener listener) {
        DisplayImageOptions localDisplayImageOptions
                = displayImageOptions != null ? displayImageOptions : OP_DEF;
        ImageLoader.getInstance().displayImage(url, imageView,
                localDisplayImageOptions, listener);
    }

    public void pause() {
        ImageLoader.getInstance().pause();
    }

    public void resume() {
        ImageLoader.getInstance().resume();
    }

    public void destroy() {
        ImageLoader.getInstance().destroy();
    }

}

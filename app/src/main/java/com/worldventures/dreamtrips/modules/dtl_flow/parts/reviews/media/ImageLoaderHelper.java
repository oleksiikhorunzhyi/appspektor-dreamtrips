package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.media;

/**
 * Created by Andres Rubiano Del Chiaro on 16/04/16.
 */
public class ImageLoaderHelper {

    public static final int GLIDE=1;
    public static final int PICASSO=2;

    private int type=GLIDE;
    private ImageLoader imageLoader;

    public ImageLoaderHelper(int type) {
        this.type = type;
        imageLoader= factory();
    }

    public ImageLoader getLoader() {
        return imageLoader;
    }

    private ImageLoader factory()
    {
        switch (type)
        {
            case PICASSO:
               return new PicassoILoader();
            case GLIDE:
                return new GlideILoader();
            default:
                return new GlideILoader();
        }
    }
}

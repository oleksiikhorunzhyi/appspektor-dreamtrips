package com.worldventures.dreamtrips.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;

import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.fromFile;
import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.saveToFile;
import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.scaleBitmap;
import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.toMonochromeBitmap;

public class SmartCardAvatarHelper {

    private final Context context;

    @Inject SmartCardAvatarHelper(@ForApplication Context context) {
        this.context = context;
    }

    public File compressPhotoFromFile(String originImage, int imageSize) throws IOException {
        return toMonochromeFile(scaleBitmap(fromFile(originImage), imageSize));
    }

    public Observable<File> compressPhotoFromUrl(String url, int imageSize) {
        return ImageUtils.getBitmap(context, Uri.parse(url), imageSize, imageSize)
                .flatMap(bitmap ->
                        Observable.fromCallable(() -> toMonochromeFile(bitmap))
                );
    }

    public File toMonochromeFile(Bitmap bitmap) throws IOException {
        return saveToFile(context, toMonochromeBitmap(bitmap));
    }

}

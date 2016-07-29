package com.worldventures.dreamtrips.util;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.fromFile;
import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.saveToFile;
import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.scaleBitmap;
import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.toMonochromeBitmap;

public class SmartCardAvatarHelper {

    private final Context context;

    @Inject SmartCardAvatarHelper(@ForApplication Context context) {
        this.context = context;
    }

    public File compressPhoto(String originImage, int imageSize) throws IOException {
        return saveToFile(context, toMonochromeBitmap(scaleBitmap(fromFile(originImage), imageSize)));
    }
}

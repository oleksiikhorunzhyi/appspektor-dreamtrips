package com.worldventures.dreamtrips.modules.tripsimages.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.io.IOException;
import java.net.URL;

public class DownloadImageCommand extends Command<String> {

    private String url;
    private Context context;

    public DownloadImageCommand(Context context, String url) {
        super(String.class);
        this.context = context;
        this.url = url;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        return cacheBitmap();
    }

    private String cacheBitmap() {
        try {
            Bitmap bitmap =  BitmapFactory.decodeStream(new URL(url).openStream());
            return MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    bitmap, CachedEntity.getFileName(url), "");
        } catch (IOException e) {
            return null;
        }
    }
}

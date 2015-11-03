package com.worldventures.dreamtrips.modules.feed.api;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.feed.model.PhotoGalleryModel;

import java.util.ArrayList;

import timber.log.Timber;

public class PhotoGalleryRequest extends SpiceRequest<ArrayList<PhotoGalleryModel>>{

    private Context context;

    public PhotoGalleryRequest(Context context) {
        super((Class<ArrayList<PhotoGalleryModel>>) new ArrayList<PhotoGalleryModel>().getClass());
        this.context = context;
    }

    @Override
    public ArrayList<PhotoGalleryModel> loadDataFromNetwork() throws Exception {
        return getGalleryPhotos();
    }

    private ArrayList<PhotoGalleryModel> getGalleryPhotos() {
        Cursor cursor = null;
        String[] projectionPhotos = {MediaStore.Images.Media.DATA};
        ArrayList<PhotoGalleryModel> photos = new ArrayList<>();

        try {
            cursor = MediaStore.Images.Media.query(context.getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projectionPhotos,
                    "",
                    null,
                    MediaStore.Images.Media.DATE_TAKEN + " DESC");

            if (cursor != null) {
                int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

                while (cursor.moveToNext()) {
                    String path = cursor.getString(dataColumn);
                    PhotoGalleryModel photo = new PhotoGalleryModel(path);
                    photos.add(photo);
                }
            }
        } catch (Throwable e) {
            Timber.e(e.getMessage());
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    Timber.e(e.getMessage());
                }
            }
        }

        return photos;
    }
}

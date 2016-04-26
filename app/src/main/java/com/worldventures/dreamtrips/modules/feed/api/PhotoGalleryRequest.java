package com.worldventures.dreamtrips.modules.feed.api;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;

import java.util.ArrayList;

import timber.log.Timber;

public class PhotoGalleryRequest extends SpiceRequest<ArrayList<PhotoGalleryModel>> {
    private final Context context;

    private int limit = -1;
    private long lastDateTaken = -1;

    public PhotoGalleryRequest(Context context) {
        super((Class<ArrayList<PhotoGalleryModel>>) new ArrayList<PhotoGalleryModel>().getClass());

        this.context = context;
    }

    public PhotoGalleryRequest(Context context, int limit) {
        this(context);

        checkLimit(limit);
        this.limit = limit;
    }

    public PhotoGalleryRequest(Context context, int limit, long lastDateTaken) {
        this(context, limit);
        this.lastDateTaken = lastDateTaken;
    }

    @Override
    public ArrayList<PhotoGalleryModel> loadDataFromNetwork() throws Exception {
        return getGalleryPhotos();
    }

    private ArrayList<PhotoGalleryModel> getGalleryPhotos() {
        Cursor cursor = null;
        String[] projectionPhotos = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN};
        ArrayList<PhotoGalleryModel> photos = new ArrayList<>();
        //
        try {
            cursor = MediaStore.Images.Media.query(context.getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projectionPhotos,
                    hasLastDateTaken() ? MediaStore.Images.Media.DATE_TAKEN + "<?" : "",
                    hasLastDateTaken() ? new String[]{String.valueOf(lastDateTaken)} : null,
                    createOrderLimit());
            if (cursor != null) {
                int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int dateColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                while (cursor.moveToNext()) {
                    String path = cursor.getString(dataColumn);
                    long dateTaken = cursor.getLong(dateColumn);
                    PhotoGalleryModel photo = new PhotoGalleryModel(path, dateTaken);
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

    @NonNull
    private String createOrderLimit() {
        StringBuilder orderLimit = new StringBuilder(MediaStore.Images.Media.DATE_TAKEN)
                .append(" DESC");

        if (hasLimit()) {
            orderLimit.append(" LIMIT ")
                    .append(limit);
        }
        return orderLimit.toString();
    }

    private boolean hasLimit() {
        return limit != -1;
    }

    private boolean hasLastDateTaken() {
        return lastDateTaken != -1;
    }

    private void checkLimit(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Limit must be > 0");
        }
    }
}
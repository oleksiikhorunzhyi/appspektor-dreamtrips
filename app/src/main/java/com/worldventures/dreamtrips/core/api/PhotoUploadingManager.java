package com.worldventures.dreamtrips.core.api;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.api.upload.PhotoUploadCommand;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import retrofit.mime.TypedFile;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class PhotoUploadingManager {

    @Inject
    Context context;

    @Inject
    SnappyRepository db;

    @Inject
    DreamSpiceManager spiceManager;

    private final Subject<UploadTask, UploadTask> bus = new SerializedSubject<>(PublishSubject.create());

    public PhotoUploadingManager(Injector injector) {
        injector.inject(this);
    }

    public void upload(UploadTask uploadTask, String purpose) {
        if (uploadTask.getId() <= 0) {
            uploadTask.setId(System.currentTimeMillis());
        }
        uploadTask.setPurpose(purpose);
        uploadTask.setStatus(UploadTask.Status.STARTED);
        String path = null;
        try {
            path = getPath(context, Uri.parse(uploadTask.getFilePath()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        db.saveUploadTask(uploadTask);
        bus.onNext(uploadTask);
        if (!spiceManager.isStarted()) {
            spiceManager.start(context);
        }

        File file = new File(path);
        final TypedFile typedFile = new TypedFile("image/*", file);

        spiceManager.execute(new PhotoUploadCommand(typedFile), String.valueOf(uploadTask.getId()), DurationInMillis.ALWAYS_EXPIRED, originUrl -> {
            uploadTask.setOriginUrl(originUrl.getLocation());
            uploadTask.setStatus(UploadTask.Status.COMPLETED);
            db.removeUploadTask(uploadTask);
            bus.onNext(uploadTask);
        }, spiceException -> {
            uploadTask.setStatus(UploadTask.Status.FAILED);
            db.saveUploadTask(uploadTask);
            bus.onNext(uploadTask);
        });
    }

    public List<UploadTask> getUploadTasksForLinkedItemId(String purpose, String linkedId) {
        List<UploadTask> items = getUploadTasks(purpose);
        return Queryable.from(items)
                .filter(item -> linkedId.equals(item.getLinkedItemId()))
                .toList();
    }


    public void cancelUpload(UploadTask uploadTask) {
        spiceManager.cancel(String.class, String.valueOf(uploadTask.getId()));
        uploadTask.setStatus(UploadTask.Status.CANCELED);
        db.removeUploadTask(uploadTask);
        bus.onNext(uploadTask);
    }

    public Observable<List<UploadTask>> getUploadTasksObservable(String purpose) {
        return Observable.from(db.getAllUploadTask()).filter((t) -> t.getPurpose().equals(purpose)).toList();
    }

    public List<UploadTask> getUploadTasks(String purpose) {
        return Queryable.from(db.getAllUploadTask()).filter((t) -> t.getPurpose().equals(purpose)).toList();
    }

    public Observable<UploadTask> getTaskChangingObservable(String purpose) {
        return bus.filter((t) -> t.getPurpose().equals(purpose));
    }

    @SuppressLint("NewApi")
    private static String getPath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}

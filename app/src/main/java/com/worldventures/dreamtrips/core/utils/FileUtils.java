package com.worldventures.dreamtrips.core.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.innahema.collections.query.queriables.Queryable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import timber.log.Timber;

public class FileUtils {

    public static void cleanDirectory(Context context, File directory) throws IOException {
        if (!directory.exists()) {
            throw new IllegalArgumentException(directory + " does not exist");
        }

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(directory + " is not a directory");
        }

        File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }

        Queryable.from(files).forEachR(file -> {
            try {
                forceDelete(context, file);
            } catch (IOException e) {
                Timber.e("Unable to delete file: " + file, e);
            }
        });

    }

    public static void forceDelete(Context context, File file) throws IOException {
        if (file.isDirectory()) {
            cleanDirectory(context, file);
        } else {
            if (!file.exists()) {
                throw new FileNotFoundException("File does not exist: " + file);
            }

            if (!file.delete()) {
                String message =
                        "Unable to delete file: " + file;
                throw new IOException(message);
            }

            // Set up the projection (we only need the ID)
            String[] projection = {MediaStore.Images.Media._ID};

            // Match on the file path
            String selection = MediaStore.Images.Media.DATA + " = ?";
            String[] selectionArgs = new String[]{file.getAbsolutePath()};

            // Query for the ID of the media matching the file path
            Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver contentResolver = context.getContentResolver();
            Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
            if (c.moveToFirst()) {
                // We found the ID. Deleting the item via the content provider will also remove the file
                long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                contentResolver.delete(deleteUri, null, null);
            }
            c.close();
        }
    }
}
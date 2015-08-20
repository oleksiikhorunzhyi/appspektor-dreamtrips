package com.worldventures.dreamtrips.core.utils;

import android.content.Context;
import android.net.Uri;

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
            context.getContentResolver().delete(Uri.parse(file.getAbsolutePath()), "", null);

            if (!file.exists()) {
                throw new FileNotFoundException("File does not exist: " + file);
            }

            if (!file.delete()) {
                String message =
                        "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }
}

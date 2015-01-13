package com.worldventures.dreamtrips.core.uploader;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

public class UploadingFileManager {
    private static final String TAG = UploadingFileManager.class.getSimpleName();

    private final Context context;

    public UploadingFileManager(Context context) {
        this.context = context;
    }

    public File copyFileIfNeed(String filePath) {
        checkNotNull(filePath);

        File file = null;

        Uri uri = Uri.parse(filePath);

        checkNotNull(uri);

        ContentResolver resolver = context.getContentResolver();
        InputStream in = null;
        FileOutputStream out = null;

        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(
                context.getContentResolver().getType(uri)
        );

        checkNotNull(extension);

        HashFunction hf = Hashing.md5();

        String fileKey = filePath + new Date().toString();

        HashCode hashCode = hf.hashString(fileKey, Charset.defaultCharset());

        String fileKeyHash = hashCode.toString();

        try {
            in = resolver.openInputStream(uri);
            file = File.createTempFile(
                    fileKeyHash,
                    "." + extension,
                    context.getFilesDir()
            );
            out = new FileOutputStream(file, false);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            Log.e(TAG, "", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
        }

        return file;
    }
}

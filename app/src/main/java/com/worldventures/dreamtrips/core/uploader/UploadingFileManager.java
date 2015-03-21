package com.worldventures.dreamtrips.core.uploader;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.worldventures.dreamtrips.core.utils.ValidationUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


public class UploadingFileManager {
    private static final String TAG = UploadingFileManager.class.getSimpleName();

    private final Context context;

    public UploadingFileManager(Context context) {
        this.context = context;
    }

    public File copyFileIfNeed(String filePath) {
        ValidationUtils.checkNotNull(filePath);

        File file = null;

        Uri uri = Uri.parse(filePath);

        ValidationUtils.checkNotNull(uri);

        ContentResolver resolver = context.getContentResolver();
        InputStream in = null;
        FileOutputStream out = null;

        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());

        ValidationUtils.checkNotNull(extension);


        String fileKey = filePath + new Date().toString();

        String fileKeyHash = md5(fileKey);

        try {
            if (uri.getScheme().startsWith("http")) {
                in = new URL(uri.toString()).openStream();
            } else {
                in = resolver.openInputStream(uri);
            }
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


    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Log.e("", "", e);
        }
        return "";
    }
}

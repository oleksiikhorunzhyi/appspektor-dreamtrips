package com.worldventures.dreamtrips.modules.common.view.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;

import com.kbeanie.imagechooser.exceptions.ChooserException;
import com.kbeanie.imagechooser.helpers.StreamHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import timber.log.Timber;

public class DrawableUtil {

    public static final int THUMBNAIL_BIG = 1;
    private static final int FULL_HD_WIDTH = 1920;

    public static final String CACHE_DIR = "dreamtrips_cache_images";

    private Context context;

    public DrawableUtil(Context context) {
        this.context = context;
    }

    public Drawable copyIntoDrawable(Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), copyBitmap(bitmap));
    }

    public Bitmap copyBitmap(Bitmap bitmap) {
        return bitmap.copy(bitmap.getConfig(), true);
    }

    public String compressAndRotateImage(String fileImage, int scale) {
        FileOutputStream stream = null;
        BufferedInputStream bstream = null;
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options optionsForGettingDimensions = new BitmapFactory.Options();
            optionsForGettingDimensions.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(fileImage, optionsForGettingDimensions);
            if (bitmap != null) {
                bitmap.recycle();
            }
            int w, l;
            w = optionsForGettingDimensions.outWidth;
            l = optionsForGettingDimensions.outHeight;

            ExifInterface exif = new ExifInterface(fileImage);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int rotate = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = -90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            int what = w > l ? w : l;

            BitmapFactory.Options options = new BitmapFactory.Options();
            if (what > FULL_HD_WIDTH) {
                options.inSampleSize = scale * (int)Math.round((double)what / FULL_HD_WIDTH);
            } else {
                options.inSampleSize = scale;
            }

            options.inJustDecodeBounds = false;

            bitmap = BitmapFactory.decodeFile(fileImage, options);

            File original = new File(fileImage);
            File file = new File(getImagesCacheDir(), original.getName());
            stream = new FileOutputStream(file);
            if (rotate != 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, false);
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);

            return file.getAbsolutePath();
        } catch (IOException e) {
            return fileImage;
        } catch (Exception e) {
            return fileImage;
        } finally {
            try {
                StreamHelper.close(bstream);
                StreamHelper.flush(stream);
                StreamHelper.close(stream);
            } catch (ChooserException e) {
                Timber.e(e.getMessage());
            }
        }
    }

    public void removeCacheImages() {
        File[] cachedFiles = getImagesCacheDir().listFiles();
        for (File cachedFile : cachedFiles) {
            cachedFile.delete();
        }
    }

    public File getImagesCacheDir() {
        File cacheDir = new File(context.getCacheDir(), CACHE_DIR);
        cacheDir.mkdir();
        return cacheDir;
    }
}

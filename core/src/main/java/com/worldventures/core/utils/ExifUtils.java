package com.worldventures.core.utils;

import android.media.ExifInterface;
import android.util.Pair;

import com.innahema.collections.query.queriables.Queryable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public final class ExifUtils {

   private ExifUtils() {
   }

   public static void copyExif(String oldPath, String newPath, @NotNull List<Pair<String, String>> customParams) throws IOException {
      String[] attributes = new String[]{
            ExifInterface.TAG_APERTURE,
            ExifInterface.TAG_DATETIME,
            ExifInterface.TAG_DATETIME_DIGITIZED,
            ExifInterface.TAG_EXPOSURE_TIME,
            ExifInterface.TAG_FLASH,
            ExifInterface.TAG_FOCAL_LENGTH,
            ExifInterface.TAG_GPS_ALTITUDE,
            ExifInterface.TAG_GPS_ALTITUDE_REF,
            ExifInterface.TAG_GPS_DATESTAMP,
            ExifInterface.TAG_GPS_LATITUDE,
            ExifInterface.TAG_GPS_LATITUDE_REF,
            ExifInterface.TAG_GPS_LONGITUDE,
            ExifInterface.TAG_GPS_LONGITUDE_REF,
            ExifInterface.TAG_GPS_PROCESSING_METHOD,
            ExifInterface.TAG_GPS_TIMESTAMP,
            ExifInterface.TAG_IMAGE_LENGTH,
            ExifInterface.TAG_IMAGE_WIDTH,
            ExifInterface.TAG_ISO,
            ExifInterface.TAG_MAKE,
            ExifInterface.TAG_MODEL,
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.TAG_SUBSEC_TIME,
            ExifInterface.TAG_SUBSEC_TIME_DIG,
            ExifInterface.TAG_SUBSEC_TIME_ORIG,
            ExifInterface.TAG_WHITE_BALANCE
      };

      ExifInterface oldExif = new ExifInterface(oldPath);
      ExifInterface newExif = new ExifInterface(newPath);

      for (String attribute : attributes) {
         String value = oldExif.getAttribute(attribute);
         if (value != null) { newExif.setAttribute(attribute, value); }
      }

      Queryable.from(customParams).forEachR(arg -> newExif.setAttribute(arg.first, arg.second));

      newExif.saveAttributes();
   }
}

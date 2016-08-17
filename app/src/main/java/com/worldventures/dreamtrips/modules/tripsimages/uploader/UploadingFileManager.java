package com.worldventures.dreamtrips.modules.tripsimages.uploader;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.techery.spares.utils.ValidationUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import timber.log.Timber;


public class UploadingFileManager {

   private UploadingFileManager() {
   }

   public static String md5(final String s) {
      final String md5 = "MD5";
      try {
         // Create MD5 Hash
         MessageDigest digest = java.security.MessageDigest.getInstance(md5);
         digest.update(s.getBytes());
         byte messageDigest[] = digest.digest();

         // Create Hex String
         StringBuilder hexString = new StringBuilder();
         for (byte aMessageDigest : messageDigest) {
            String h = Integer.toHexString(0xFF & aMessageDigest);
            while (h.length() < 2) h = String.format("0%s", h);
            hexString.append(h);
         }
         return hexString.toString();

      } catch (NoSuchAlgorithmException e) {
         Timber.e(e, "");
      }
      return "";
   }

   public static String copyFileIfNeed(String filePath, Context context) {
      ValidationUtils.checkNotNull(filePath);

      String finalPath = null;

      Uri uri = Uri.parse(filePath);
      ValidationUtils.checkNotNull(uri);

      if (uri.getScheme() != null && uri.getScheme().startsWith("http")) {
         InputStream in = null;
         FileOutputStream out = null;

         try {
            String fileKey = filePath + new Date().toString();
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            ValidationUtils.checkNotNull(extension);
            String fileKeyHash = md5(fileKey);
            in = new URL(uri.toString()).openStream();
            File file = File.createTempFile(fileKeyHash, "." + extension, context.getFilesDir());
            out = new FileOutputStream(file, false);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
               out.write(buffer, 0, read);
            }
            out.flush();

            finalPath = "file://" + file.getAbsolutePath();

         } catch (IOException e) {
            Timber.e(e, "Problem on file copying");
         } finally {
            if (in != null) {
               try {
                  in.close();
               } catch (IOException e) {
                  Timber.e(e, "Problem on file copying");
               }
            }
            if (out != null) {
               try {
                  out.close();
               } catch (IOException e) {
                  Timber.e(e, "Problem on file copying");
               }
            }
         }

      } else {
         finalPath = filePath;
      }

      return finalPath;
   }
}

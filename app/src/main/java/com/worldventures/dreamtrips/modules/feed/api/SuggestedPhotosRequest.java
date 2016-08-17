package com.worldventures.dreamtrips.modules.feed.api;

import android.os.Environment;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class SuggestedPhotosRequest extends SpiceRequest<ArrayList<PhotoGalleryModel>> {

   public static final int SUGGESTED_PHOTOS_COUNT = 15;

   public SuggestedPhotosRequest() {
      super((Class<ArrayList<PhotoGalleryModel>>) new ArrayList<PhotoGalleryModel>().getClass());
   }

   @Override
   public ArrayList<PhotoGalleryModel> loadDataFromNetwork() throws Exception {
      return getSuggestedPhotos();
   }

   private ArrayList<PhotoGalleryModel> getSuggestedPhotos() {
      ArrayList<PhotoGalleryModel> suggestedList = new ArrayList<>();
      Queryable.from(getLocalPhotos())
            .filter(DrawableUtil::isFileImage)
            .sort((lhs, rhs) -> lhs.lastModified() > rhs.lastModified() ? -1 : (lhs.lastModified() < rhs.lastModified() ? 1 : 0))
            .take(SUGGESTED_PHOTOS_COUNT)
            .forEachR(file -> suggestedList.add(new PhotoGalleryModel(file.getPath(), file.lastModified())));
      return suggestedList;
   }

   private List<File> getLocalPhotos() {
      ArrayList<File> localFiles = new ArrayList<>();
      // external storage photos
      localFiles.addAll(getListFiles(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            .getAbsolutePath())));
      // sdcard photos
      Queryable.from(getSdCardPaths()).forEachR(path -> localFiles.addAll(getListFiles(new File(path))));
      return localFiles;
   }

   private List<File> getListFiles(File parentDir) {
      ArrayList<File> inFiles = new ArrayList<>();
      File[] files = parentDir.listFiles();
      if (files == null) return inFiles;
      for (File file : files) {
         if (file.isDirectory()) {
            if (file.getName().startsWith(".")) continue;
            //
            inFiles.addAll(getListFiles(file));
         } else {
            inFiles.add(file);
         }
      }
      return inFiles;
   }

   private List<String> getSdCardPaths() {
      final HashSet<String> out = new HashSet<>();
      String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
      String s = "";
      try {
         final Process process = new ProcessBuilder().command("mount").redirectErrorStream(true).start();
         process.waitFor();
         final InputStream is = process.getInputStream();
         final byte[] buffer = new byte[1024];
         while (is.read(buffer) != -1) {
            s = s + new String(buffer);
         }
         is.close();
      } catch (final Exception e) {
         e.printStackTrace();
      }
      // parse output
      final String[] lines = s.split("\n");
      for (String line : lines) {
         if (!line.toLowerCase(Locale.US).contains("asec")) {
            if (line.matches(reg)) {
               String[] parts = line.split(" ");
               for (String part : parts) {
                  if (part.startsWith("/")) if (!part.toLowerCase(Locale.US).contains("vold")) out.add(part);
               }
            }
         }
      }
      return Queryable.from(out).toList();
   }
}

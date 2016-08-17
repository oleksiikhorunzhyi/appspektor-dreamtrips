package com.worldventures.dreamtrips.util;

import com.octo.android.robospice.request.SpiceRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Creates copy of source file
 */
public class CopyFileTask extends SpiceRequest<String> {

   private File sourceFile;
   private String copyFilePath;

   /**
    * @param sourceFile   - source file
    * @param copyFilePath - copy file path
    *                     For creating copy in same directory but other name you may use
    *                     new CopyFileTask(originalFile, originalFile.getParentFile() + "/" + "prefix_" + originalFile.getName())
    */
   public CopyFileTask(File sourceFile, String copyFilePath) {
      super((Class<String>) new String().getClass());
      this.sourceFile = sourceFile;
      this.copyFilePath = copyFilePath;
   }

   @Override
   public String loadDataFromNetwork() throws Exception {
      File copyFile = new File(copyFilePath);
      InputStream in = new FileInputStream(sourceFile);
      OutputStream out = new FileOutputStream(copyFile);

      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
         out.write(buf, 0, len);
      }
      in.close();
      out.close();
      return copyFile.getAbsolutePath();
   }
}

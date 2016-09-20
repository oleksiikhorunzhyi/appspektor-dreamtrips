package com.worldventures.dreamtrips.modules.common.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DownloadFileCommand extends Command<File> {

   private String urlLink;
   private File file;

   public DownloadFileCommand(File file, String urlLink) {
      this.file = file;
      this.urlLink = urlLink;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      InputStream input = null;
      OutputStream output = null;
      HttpURLConnection connection = null;
      try {
         URL url = new URL(urlLink);
         connection = (HttpURLConnection) url.openConnection();
         connection.connect();

         // expect HTTP 200 OK, so we don't mistakenly save error report
         // instead of the file
         if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new Exception("Server returned HTTP " + connection.getResponseCode()
                  + " " + connection.getResponseMessage());
         }

         int fileLength = connection.getContentLength();

         input = connection.getInputStream();
         output = new FileOutputStream(file);

         byte data[] = new byte[4096];
         long total = 0;
         int count;
         while ((count = input.read(data)) != -1) {
            if (isCanceled()) {
               input.close();
               file.delete();
               return;
            }
            total += count;
            if (fileLength > 0) {
               callback.onProgress((int) (total * 100 / fileLength));
            }
            output.write(data, 0, count);
         }
         callback.onSuccess(file);
      } finally {
         try {
            if (output != null) {
               output.close();
            }
            if (input != null) {
               input.close();
            }
         } catch (IOException ignored) {
         }

         if (connection != null) {
            connection.disconnect();
         }
      }
   }

   public String getUrl() {
      return urlLink;
   }

   public File getFile() {
      return file;
   }

   @Override
   protected void cancel() {
      setCanceled(true);
   }
}

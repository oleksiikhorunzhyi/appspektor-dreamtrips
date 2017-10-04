package com.worldventures.dreamtrips.core.janet.cache.storage;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TempSessionIdProviderImpl implements TempSessionIdProvider {

   private String tempSessionId = null;

   @Override
   public String getTempSessionId() throws IOException {
      if (tempSessionId == null) {
         File config = prepareConfigFile("config_x_session_id");

         StringBuilder text = new StringBuilder();

         if (config != null) {
            BufferedReader br = new BufferedReader(new FileReader(config));
            String line;

            while ((line = br.readLine()) != null) {
               text.append(line);
            }
            br.close();
         }

         tempSessionId = text.toString();
      }
      return tempSessionId;
   }

   private File prepareConfigFile(String name) throws IOException {
      File sdcard = Environment.getExternalStorageDirectory();

      File configDir = new File(sdcard, ".dt_qa_configs");
      if (!configDir.exists()) configDir.mkdir();

      File configFile = new File(configDir, name);
      if (!configFile.exists()) configFile.createNewFile();

      return configFile;
   }
}

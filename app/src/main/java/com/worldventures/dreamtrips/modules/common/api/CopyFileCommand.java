package com.worldventures.dreamtrips.modules.common.api;

import android.content.Context;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager;

public class CopyFileCommand extends Command<String> {

   private Context context;
   private String filePath;

   public CopyFileCommand(Context context, String filePath) {
      super(String.class);
      this.context = context;
      this.filePath = filePath;
   }

   @Override
   public String loadDataFromNetwork() throws Exception {
      return UploadingFileManager.copyFileIfNeed(filePath, context);
   }
}

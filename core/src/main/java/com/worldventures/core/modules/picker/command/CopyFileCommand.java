package com.worldventures.core.modules.picker.command;

import android.content.Context;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.service.UploadingFileManager;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CopyFileCommand extends Command<String> implements InjectableAction {
   @Inject UploadingFileManager uploadingFileManager;

   private String filePath;

   @Deprecated
   public CopyFileCommand(Context context, String filePath) {
      this(filePath);
   }

   public CopyFileCommand(String filePath) {
      this.filePath = filePath;
   }

   @Override
   protected void run(CommandCallback<String> callback) throws Throwable {
      try {
         callback.onSuccess(uploadingFileManager.copyFileIfNeed(filePath));
      } catch (Throwable ex) {
         callback.onFail(ex);
      }
   }
}

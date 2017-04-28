package com.worldventures.dreamtrips.modules.common.command;

import android.content.Context;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager;

import java.lang.ref.WeakReference;

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

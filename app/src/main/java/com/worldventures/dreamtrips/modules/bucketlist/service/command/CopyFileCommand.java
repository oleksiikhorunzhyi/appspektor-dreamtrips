package com.worldventures.dreamtrips.modules.bucketlist.service.command;

import android.content.Context;

import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager;

import java.lang.ref.WeakReference;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CopyFileCommand extends Command<String> {
   private WeakReference<Context> contextRef;

   private String filePath;

   public CopyFileCommand(Context context, String filePath) {
      this.contextRef = new WeakReference<>(context);
      this.filePath = filePath;
   }

   @Override
   protected void run(CommandCallback<String> callback) throws Throwable {
      try {
         if (contextRef.get() != null) {
            callback.onSuccess(UploadingFileManager.copyFileIfNeed(filePath, contextRef.get()));
         }
      } catch (Throwable ex) {
         callback.onFail(ex);
      }
   }
}
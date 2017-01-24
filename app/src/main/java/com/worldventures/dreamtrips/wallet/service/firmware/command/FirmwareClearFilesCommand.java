package com.worldventures.dreamtrips.wallet.service.firmware.command;

import java.io.File;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FirmwareClearFilesCommand extends Command<Void> {

   private String path;

   public FirmwareClearFilesCommand(String path) {
      this.path = path;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      deleteFile(new File(path));
      callback.onSuccess(null);
   }

   private void deleteFile(File fileOrDirectory) {
      if (fileOrDirectory.isDirectory())
         for (File child : fileOrDirectory.listFiles())
            deleteFile(child);
      fileOrDirectory.delete();
   }
}

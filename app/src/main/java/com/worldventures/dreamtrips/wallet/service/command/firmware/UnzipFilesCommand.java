package com.worldventures.dreamtrips.wallet.service.command.firmware;

import org.immutables.value.Value;

import java.io.File;
import java.io.IOException;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.exception.InvalidFirmwareException;
import io.techery.janet.smartcard.util.UnzipUtil;

@CommandAction
public class UnzipFilesCommand extends Command<UnzipFilesCommand.FirmwareBundle> {

   public static final String APP_NORDIC_FOLDER = "AppNordic";
   public static final String PUCK_ATMEL_FOLDER = "PuckAtmel";
   public static final String APP_ATMEL_FOLDER = "AppAtmel";
   public static final String BOOTLOADER_NORDIC_FOLDER = "BootloaderNordic";

   private final File archive;

   public UnzipFilesCommand(File archive) {
      this.archive = archive;
   }

   @Override
   protected void run(CommandCallback<FirmwareBundle> callback) throws Throwable {
      String firmwarePath = archive.getAbsolutePath();
      String unzippedPath = archive.getParent();
      try {
         UnzipUtil.unzip(firmwarePath, unzippedPath);
      } catch (IOException e) {
         throw new InvalidFirmwareException("Firmware zip failed to unarchive", e);
      }

      File appNordic, puckAtmel, appAtmel, booloaderNordic;
      try {
         appNordic = getFileInDir(unzippedPath + File.separator + APP_NORDIC_FOLDER);
         puckAtmel = getFileInDir(unzippedPath + File.separator + PUCK_ATMEL_FOLDER);
         appAtmel = getFileInDir(unzippedPath + File.separator + APP_ATMEL_FOLDER);
         booloaderNordic = getFileInDir(unzippedPath + File.separator + BOOTLOADER_NORDIC_FOLDER);
         FirmwareBundle firmwareBundle = ImmutableFirmwareBundle.builder()
               .appAtmel(appAtmel)
               .appNordic(appNordic)
               .puckAtmel(puckAtmel)
               .booloaderNordic(booloaderNordic)
               .build();
         callback.onSuccess(firmwareBundle);
      } catch (IllegalArgumentException e) {
         callback.onFail(new InvalidFirmwareException("Firmware zip bad format: some files are missing", e));
      }
   }

   private File getFileInDir(String path) throws IllegalArgumentException {
      File folder = new File(path);
      if (!folder.exists() || !folder.isDirectory()) {
         throw new IllegalArgumentException("Folder not found - " + folder.getName());
      }
      File[] files = folder.listFiles();
      if (files.length == 0) {
         throw new IllegalArgumentException("Folder has no files " + folder.getName());
      }
      return files[0];
   }

   @Value.Immutable
   public interface FirmwareBundle {

      File appNordic();

      File puckAtmel();

      File appAtmel();

      File booloaderNordic();
   }
}

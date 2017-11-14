package com.worldventures.wallet.service.firmware.command;

import org.immutables.value.Value;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.exception.InvalidFirmwareException;

@CommandAction
public class UnzipFirmwareCommand extends Command<UnzipFirmwareCommand.FirmwareBundle> {

   private static final int UNZIP_BUFFER_SIZE = 4096;

   private static final String APP_NORDIC_FOLDER = "AppNordic";
   private static final String PUCK_ATMEL_FOLDER = "PuckAtmel";
   private static final String APP_ATMEL_FOLDER = "AppAtmel";
   private static final String BOOTLOADER_NORDIC_FOLDER = "BootloaderNordic";

   private final File firmwareArchive;

   public UnzipFirmwareCommand(File firmwareArchive) {
      this.firmwareArchive = firmwareArchive;
   }

   @Override
   protected void run(CommandCallback<FirmwareBundle> callback) throws Throwable {
      String firmwarePath = firmwareArchive.getAbsolutePath();
      String unzippedPath = firmwareArchive.getParent();
      try {
         unzip(firmwarePath, unzippedPath);
      } catch (IOException e) {
         callback.onFail(new InvalidFirmwareException("Firmware zip failed to unarchive", e));
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

   private void unzip(String zipFilePath, String destDirectory) throws IOException {
      File destDir = new File(destDirectory);
      if (!destDir.exists()) {
         destDir.mkdirs();
      }
      ZipInputStream zipIn = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFilePath), UNZIP_BUFFER_SIZE));

      File entryFile, entryParentFile;
      String entryDestination;

      ZipEntry entry;
      while ((entry = zipIn.getNextEntry()) != null) {
         if (!entry.isDirectory()) {
            entryDestination = destDirectory;
            entryFile = new File(entryDestination, entry.getName());

            entryParentFile = entryFile.getParentFile();
            if (entryParentFile != null) {
               entryParentFile = new File(entryDestination, entryParentFile.getName());
               entryParentFile.mkdirs();
               entryDestination = entryParentFile.getPath();
            }

            BufferedOutputStream bos = new BufferedOutputStream(
                  new FileOutputStream(new File(entryDestination, entryFile.getName())));
            byte[] bytesIn = new byte[UNZIP_BUFFER_SIZE];
            int read;
            try {
               while ((read = zipIn.read(bytesIn)) != -1) {
                  bos.write(bytesIn, 0, read);
               }
               zipIn.closeEntry();
            } finally {
               bos.flush();
               bos.close();
            }
         }
         zipIn.closeEntry();
      }
      zipIn.close();
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
   interface FirmwareBundle {

      File appNordic();

      File puckAtmel();

      File appAtmel();

      File booloaderNordic();
   }
}

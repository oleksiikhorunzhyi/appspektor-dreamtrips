package com.worldventures.dreamtrips.social.ui.background_uploading.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileSplitter {

   public final static int CHUNK_SIZE = 1024 * 1024 * 5; // the maximum size of each file "chunk" generated, in bytes

   private final File externalCacheDir;

   public FileSplitter(File externalCacheDir) {
      this.externalCacheDir = externalCacheDir;
   }

   public static int computeChunkCount(File file) {
      long fileSize = file.length();
      int chunkCount = (int) (fileSize / CHUNK_SIZE);
      if (fileSize % CHUNK_SIZE > 0) {
         chunkCount++;
      }
      return chunkCount;
   }

   public List<File> split(File file, int fromChunk) throws IOException {
      int chunkCount = computeChunkCount(file);
      byte[] readingBuffer = new byte[CHUNK_SIZE];
      List<File> chunks = new ArrayList<>(chunkCount);

      BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
      long skipped = in.skip(fromChunk * CHUNK_SIZE);
      if (skipped == 0) {
         throw new EOFException();
      }
      for (int subFile = fromChunk; subFile < chunkCount; subFile++) {
         File chunk = new File(externalCacheDir + file.getName() + "." + subFile);

         chunks.add(chunk);

         BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(chunk));
         int readCount = in.read(readingBuffer);
         out.write(readingBuffer, 0, readCount);
         out.close();
      }
      in.close();

      return chunks;
   }

}

package com.worldventures.dreamtrips.modules.background_uploading.model.video;

import java.util.List;

public class VideoUploadUrls {

   private String completeUrl;
   private String abortUrl;
   private List<VideoChunkUploadUrl> chunkUrls;

   public VideoUploadUrls() {
   }

   public String getCompleteUrl() {
      return completeUrl;
   }

   public void setCompleteUrl(String completeUrl) {
      this.completeUrl = completeUrl;
   }

   public String getAbortUrl() {
      return abortUrl;
   }

   public void setAbortUrl(String abortUrl) {
      this.abortUrl = abortUrl;
   }

   public List<VideoChunkUploadUrl> getChunkUrls() {
      return chunkUrls;
   }

   public void setChunkUrls(List<VideoChunkUploadUrl> chunkUrls) {
      this.chunkUrls = chunkUrls;
   }
}

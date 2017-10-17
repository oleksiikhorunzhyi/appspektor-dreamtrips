package com.worldventures.dreamtrips.modules.config.model;

public class Configuration {

   private final UpdateRequirement updateRequirement;
   private final VideoRequirement videoRequirement;

   public Configuration() {
      videoRequirement = new VideoRequirement();
      updateRequirement = new UpdateRequirement();
   }

   public Configuration(UpdateRequirement updateRequirement, VideoRequirement videoRequirement) {
      this.updateRequirement = updateRequirement;
      this.videoRequirement = videoRequirement;
   }

   public UpdateRequirement getUpdateRequirement() {
      return updateRequirement;
   }

   public VideoRequirement getVideoRequirement() {
      return videoRequirement;
   }
}

package com.worldventures.dreamtrips.modules.feed.model;

import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.ArrayList;
import java.util.List;

public class CreatePhotoPostEntity {

   private String description;
   private Location location;
   private List<Attachment> attachments;

   public CreatePhotoPostEntity() {
      attachments = new ArrayList<>();
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getDescription() {
      return description;
   }

   public void setLocation(Location location) {
      this.location = location;
   }

   public Location getLocation() {
      return location;
   }

   public void addAttachment(Attachment attachment) {
      attachments.add(attachment);
   }

   public List<Attachment> getAttachments() {
      return attachments;
   }

   public static class Attachment {

      private String uid;

      public Attachment(String uid) {
         this.uid = uid;
      }

      public String getUid() {
         return uid;
      }
   }
}

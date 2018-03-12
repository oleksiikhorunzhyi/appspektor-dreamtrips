package com.worldventures.dreamtrips.social.ui.feed.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.core.model.Location;
import com.worldventures.dreamtrips.social.ui.feed.model.feed.hashtag.Hashtag;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class TextualPost extends BaseFeedEntity {

   private String description;

   private List<FeedEntityHolder> attachments = new ArrayList<>();

   private Location location;

   private List<Hashtag> hashtags = new ArrayList<>();

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public List<FeedEntityHolder> getAttachments() {
      return attachments;
   }

   public void setAttachments(List<FeedEntityHolder> attachments) {
      this.attachments = attachments;
   }

   public List<Hashtag> getHashtags() {
      return hashtags;
   }

   public void setHashtags(List<Hashtag> hashtags) {
      this.hashtags = hashtags;
   }

   @NotNull
   public Location getLocation() {
      return location != null ? location : new Location();
   }

   public void setLocation(Location location) {
      this.location = location;
   }

   @Override
   public String place() {
      return location != null ? location.getName() : "";
   }

   @Override
   public String getOriginalText() {
      return description;
   }

   @Override
   public boolean contentSame(FeedEntity feedEntity) {
      if (feedEntity == null || getClass() != feedEntity.getClass()) {
         return false;
      }
      if (!super.equals(feedEntity)) {
         return false;
      }

      if (!super.contentSame(feedEntity)) {
         return false;
      }

      TextualPost that = (TextualPost) feedEntity;

      if (description != null ? !description.equals(that.description) : that.description != null) {
         return false;
      }
      if (attachments != null ? !attachments.equals(that.attachments) : that.attachments != null) {
         return false;
      }
      if (location != null ? !location.equals(that.location) : that.location != null) {
         return false;
      }
      return hashtags != null ? hashtags.equals(that.hashtags) : that.hashtags == null;
   }
}

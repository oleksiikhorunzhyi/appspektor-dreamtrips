package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.UidItem;

public class ItemFlaggedEvent {

   private UidItem entity;

   private int flagReasonId;
   private String nameOfReason;

   public ItemFlaggedEvent(UidItem entity, int flagReasonId, String nameOfReason) {
      this.entity = entity;
      this.flagReasonId = flagReasonId;
      this.nameOfReason = nameOfReason;
   }

   public UidItem getEntity() {
      return entity;
   }

   public int getFlagReasonId() {
      return flagReasonId;
   }

   public String getNameOfReason() {
      return nameOfReason;
   }
}

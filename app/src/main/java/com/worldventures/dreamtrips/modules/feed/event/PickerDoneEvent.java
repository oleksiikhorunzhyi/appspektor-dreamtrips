package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;

public class PickerDoneEvent {
   private MediaAttachment mediaAttachment;

   public PickerDoneEvent(MediaAttachment mediaAttachment) {
      this.mediaAttachment = mediaAttachment;
   }

   public PickerDoneEvent() {
   }

   public MediaAttachment getMediaAttachment() {
      return mediaAttachment;
   }
}

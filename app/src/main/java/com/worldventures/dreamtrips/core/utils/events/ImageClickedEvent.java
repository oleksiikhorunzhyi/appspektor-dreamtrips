package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.core.ui.fragment.ImagePathHolder;

public class ImageClickedEvent {

   public final ImagePathHolder image;

   public ImageClickedEvent(ImagePathHolder image) {
      this.image = image;
   }
}

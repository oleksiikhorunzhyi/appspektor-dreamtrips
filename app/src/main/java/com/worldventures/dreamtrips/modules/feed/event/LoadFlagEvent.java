package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;

public class LoadFlagEvent {
   private Flaggable flaggableView;

   public LoadFlagEvent(Flaggable flaggableView) {
      this.flaggableView = flaggableView;
   }

   public Flaggable getFlaggableView() {
      return flaggableView;
   }
}

package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.event.MapInfoReadyAction;
import com.worldventures.dreamtrips.modules.dtl.event.ShowMapInfoAction;

import io.techery.janet.ActionPipe;

public class PresentationInteractor {

   private final ActionPipe<MapInfoReadyAction> mapPopupReadyPipe;
   private final ActionPipe<ShowMapInfoAction> showMapInfoPipe;

   public PresentationInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.mapPopupReadyPipe = sessionActionPipeCreator.createPipe(MapInfoReadyAction.class);
      this.showMapInfoPipe = sessionActionPipeCreator.createPipe(ShowMapInfoAction.class);
   }

   public ActionPipe<MapInfoReadyAction> mapPopupReadyPipe() {
      return mapPopupReadyPipe;
   }

   public ActionPipe<ShowMapInfoAction> showMapInfoPipe() {
      return showMapInfoPipe;
   }
}

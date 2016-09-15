package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class FilterDataInteractor {

   private final ActionPipe<FilterDataAction> filterDataPipe;

   public FilterDataInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      filterDataPipe = sessionActionPipeCreator.createPipe(FilterDataAction.class, Schedulers.io());
   }

   public ActionPipe<FilterDataAction> filterDataPipe() {
      return filterDataPipe;
   }
}

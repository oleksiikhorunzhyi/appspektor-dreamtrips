package com.messenger.api;

import com.worldventures.dreamtrips.api.messenger.TranslateTextHttpAction;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class TranslationInteractor {

   private final ActionPipe<TranslateTextHttpAction> translatePipe;

   @Inject
   public TranslationInteractor(Janet janet) {
      translatePipe = janet.createPipe(TranslateTextHttpAction.class, Schedulers.io());
   }

   public ActionPipe<TranslateTextHttpAction> translatePipe() {
      return translatePipe;
   }
}

package com.worldventures.dreamtrips.modules.dtl_flow.parts.common;

import org.jetbrains.annotations.NotNull;

import io.techery.janet.operationsubscriber.view.SuccessView;
import rx.functions.Action1;

public class ActionSuccessView<T> implements SuccessView<T> {

   private final Action1<T> onSuccessAction;

   public ActionSuccessView(@NotNull Action1<T> onSuccessAction) {
      this.onSuccessAction = onSuccessAction;
   }

   @Override
   public void showSuccess(T action) {
      onSuccessAction.call(action);
   }

   @Override
   public boolean isSuccessVisible() {
      return false;
   }

   @Override
   public void hideSuccess() {
      // ignore
   }
}

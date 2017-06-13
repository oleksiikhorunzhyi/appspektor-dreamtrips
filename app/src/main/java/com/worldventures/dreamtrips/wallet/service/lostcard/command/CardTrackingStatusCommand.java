package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public class CardTrackingStatusCommand extends Command<Boolean> implements InjectableAction {

   @Inject LostCardRepository lostCardRepository;

   private final Func1<Boolean, Boolean> func;

   private CardTrackingStatusCommand(Func1<Boolean, Boolean> func) {
      this.func = func;
   }

   public static CardTrackingStatusCommand fetch() {
      return new CardTrackingStatusCommand(new FetchStatusFunc());
   }

   public static CardTrackingStatusCommand save(boolean status) {
      return new CardTrackingStatusCommand(new SaveStatusFunc(status));
   }

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
      boolean cachedValue = lostCardRepository.isEnableTracking();
      boolean newValue = func.call(cachedValue);

      if (newValue ^ cachedValue) {
         lostCardRepository.saveEnabledTracking(newValue);
      }
      callback.onSuccess(newValue);
   }

   private static class SaveStatusFunc implements Func1<Boolean, Boolean> {

      private final boolean status;

      private SaveStatusFunc(boolean status) {
         this.status = status;
      }

      @Override
      public Boolean call(Boolean aBoolean) {
         return status;
      }
   }

   private static class FetchStatusFunc implements Func1<Boolean, Boolean> {

      @Override
      public Boolean call(Boolean aBoolean) {
         return aBoolean;
      }
   }
}

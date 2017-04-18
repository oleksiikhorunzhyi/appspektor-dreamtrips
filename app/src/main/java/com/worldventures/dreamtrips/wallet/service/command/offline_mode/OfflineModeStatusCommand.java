package com.worldventures.dreamtrips.wallet.service.command.offline_mode;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.RecordsStorage;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public class OfflineModeStatusCommand extends Command<Boolean> implements InjectableAction {

   @Inject RecordsStorage recordsStorage;

   private final Func1<Boolean, Boolean> func;

   private OfflineModeStatusCommand(Func1<Boolean, Boolean> func) {
      this.func = func;
   }

   public static OfflineModeStatusCommand fetch() {
      return new OfflineModeStatusCommand(new FetchStatusFunc());
   }

   public static OfflineModeStatusCommand save(boolean status) {
      return new OfflineModeStatusCommand(new SaveStatusFunc(status));
   }

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
      boolean storedValue = recordsStorage.readOfflineModeState();
      boolean newValue = func.call(storedValue);

      if (newValue ^ storedValue) {
         recordsStorage.saveOfflineModeState(newValue);
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

package com.worldventures.wallet.service.command.record;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.service.command.CachedValueCommand;
import com.worldventures.wallet.service.command.wizard.DummyRecordCreator;
import com.worldventures.wallet.util.WalletFeatureHelper;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public final class DefaultRecordIdCommand extends CachedValueCommand<String> implements InjectableAction {

   @Inject WalletFeatureHelper featureHelper;

   public static DefaultRecordIdCommand set(String recordId) {
      return new DefaultRecordIdCommand(cache -> recordId);
   }

   public static DefaultRecordIdCommand fetch() {
      return new DefaultRecordIdCommand(cache -> cache);
   }

   private DefaultRecordIdCommand(Func1<String, String> operationFunc) {
      super(operationFunc);
   }

   @Override
   protected void run(CommandCallback<String> callback) throws Throwable {
      if (featureHelper.isSampleCardMode()) {
         callback.onSuccess(DummyRecordCreator.defaultRecordId());
         return;
      }
      super.run(callback);
   }
}

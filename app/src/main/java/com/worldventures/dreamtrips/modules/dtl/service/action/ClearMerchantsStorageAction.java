package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.domain.storage.FullMerchantStorage;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ClearMerchantsStorageAction extends Command<Void> implements InjectableAction {

   @Inject FullMerchantStorage fullMerchantStorage;

   public static ClearMerchantsStorageAction clear() {
      return new ClearMerchantsStorageAction();
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      fullMerchantStorage.clearMemory();
   }
}

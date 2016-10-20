package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.service.storage.FullMerchantStorage;
import com.worldventures.dreamtrips.modules.dtl.service.storage.MerchantsStorage;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ClearMerchantsStorageAction extends Command<Void> implements InjectableAction {

   @Inject MerchantsStorage merchantsStorage;
   @Inject FullMerchantStorage fullMerchantStorage;

   public static ClearMerchantsStorageAction clear() {
      return new ClearMerchantsStorageAction();
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      merchantsStorage.clearMemory();
      fullMerchantStorage.clearMemory();
   }
}

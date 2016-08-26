package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardStorage;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.meta.SetMetaDataPairAction;

@CommandAction
public class SetupSmartCardNameCommand extends Command<Void> implements InjectableAction, CachedAction<SmartCard> {
   private static final String CARD_NAME_KEY = "card_name";

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;

   private final String cardName;
   private final String cardId;
   private SmartCard smartCard;

   public SetupSmartCardNameCommand(String cardName, String cardId) {
      this.cardName = cardName;
      this.cardId = cardId;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      WalletValidateHelper.validateCardNameOrThrow(cardName);

      janet.createPipe(SetMetaDataPairAction.class)
            .createObservableResult(new SetMetaDataPairAction(CARD_NAME_KEY, cardName))
            .doOnNext(action -> updateCashedSmartCard())
            .subscribe(action -> callback.onSuccess(null), callback::onFail);
   }

   private void updateCashedSmartCard() {
      smartCard = ImmutableSmartCard.builder().from(smartCard).cardName(cardName).build();
   }

   public String getCardId() {
      return cardId;
   }

   @Override
   public SmartCard getCacheData() {
      return smartCard;
   }

   @Override
   public void onRestore(ActionHolder holder, SmartCard cache) {
      this.smartCard = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle bundle = new CacheBundleImpl();
      bundle.put(SmartCardStorage.CARD_ID_PARAM, cardId);

      return ImmutableCacheOptions.builder()
            .params(bundle)
            .restoreFromCache(true)
            .sendAfterRestore(false)
            .saveToCache(true)
            .build();
   }
}

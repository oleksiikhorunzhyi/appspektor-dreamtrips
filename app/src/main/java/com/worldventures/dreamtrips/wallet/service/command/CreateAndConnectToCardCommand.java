package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.Provision;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;

@CommandAction
public class CreateAndConnectToCardCommand extends Command<Void> implements InjectableAction, CachedAction<SmartCard> {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;

   private String code;
   private SmartCard smartCard;

   public CreateAndConnectToCardCommand(String code) {
      this.code = code;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      WalletValidateHelper.validateSCIdOrThrow(code);
      janet.createPipe(CreateCardHttpAction.class)
            .createObservable(new CreateCardHttpAction(code))
            .compose(new ActionStateToActionTransformer<>())
            .map(httpAction -> createSmartCard(httpAction.getResponse()))
            .flatMap(smartCard -> smartCardInteractor.connectActionPipe()
                  .createObservableResult(new ConnectSmartCardCommand(smartCard)))
            // TODO: add next modification operation in this place.
            .doOnNext(command -> this.smartCard = command.getResult())
            .subscribe(connectCommand -> callback.onSuccess(null), callback::onFail);
   }

   public SmartCard createSmartCard(Provision provision) {
      return ImmutableSmartCard.builder()
            .deviceName(provision.memberId())
            .deviceAddress(provision.userSecret())
            .smartCardId(code)
            .cardStatus(SmartCard.CardStatus.DRAFT)
            .build();
   }

   public String getCode() {
      return code;
   }

   @Override
   public SmartCard getCacheData() {
      return smartCard;
   }

   @Override
   public void onRestore(ActionHolder holder, SmartCard cache) {
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .restoreFromCache(false)
            .saveToCache(true)
            .build();
   }
}
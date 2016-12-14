package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class UpdateSmartCardPropertiesCommand extends Command<SmartCard> implements SmartCardModifier, InjectableAction, CachedAction<SmartCard> {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      janet.createPipe(FetchCardPropertiesCommand.class)
            .createObservableResult(new FetchCardPropertiesCommand())
            .map(Command::getResult)
            .flatMap(properties ->
                  janet.createPipe(GetActiveSmartCardCommand.class)
                        .createObservableResult(new GetActiveSmartCardCommand())
                        .map(command -> ImmutableSmartCard.builder().from(command.getResult())
                              .sdkVersion(properties.sdkVersion())
                              .firmWareVersion(properties.firmWareVersion())
                              .batteryLevel(properties.batteryLevel())
                              .lock(properties.lock())
                              .stealthMode(properties.stealthMode())
                              .disableCardDelay(properties.disableCardDelay())
                              .clearFlyeDelay(properties.clearFlyeDelay())
                              .build()
                        )
            )
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public SmartCard getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, SmartCard cache) {
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .restoreFromCache(false)
            .build();
   }
}

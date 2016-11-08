package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class UpdateSmartCardConnectionStatus extends Command<SmartCard> implements InjectableAction, SmartCardModifier, CachedAction<SmartCard> {

   @Inject SmartCardInteractor interactor;
   @Inject SnappyRepository snappyRepository;

   private SmartCard.ConnectionStatus status;
   private SmartCard smartCard;

   public UpdateSmartCardConnectionStatus(SmartCard.ConnectionStatus status) {
      this.status = status;
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      interactor.activeSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand())
            .map(cardCommand -> ImmutableSmartCard.copyOf(cardCommand.getResult()).withConnectionStatus(status))
            .doOnNext(updatedSmartCard -> smartCard = updatedSmartCard)
            .subscribe(callback::onSuccess, callback::onFail);
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

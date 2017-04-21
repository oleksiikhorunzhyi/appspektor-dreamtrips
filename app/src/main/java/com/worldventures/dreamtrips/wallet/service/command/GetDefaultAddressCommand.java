package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchAndStoreDefaultAddressInfoCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class GetDefaultAddressCommand extends Command<AddressInfo> implements InjectableAction {

   @Inject SnappyRepository snappyRepository;
   @Inject @Named(JANET_WALLET) Janet janet;

   @Override
   protected void run(CommandCallback<AddressInfo> callback) throws Throwable {
      Observable.fromCallable(() -> snappyRepository.readDefaultAddress())
            .compose(new NonNullFilter<>())
            .switchIfEmpty(janet.createPipe(FetchAndStoreDefaultAddressInfoCommand.class)
                  .createObservableResult(new FetchAndStoreDefaultAddressInfoCommand())
                  .map(command -> snappyRepository.readDefaultAddress()))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}

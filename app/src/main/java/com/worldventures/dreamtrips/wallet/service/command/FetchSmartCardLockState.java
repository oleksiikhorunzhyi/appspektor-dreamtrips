package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import io.techery.janet.smartcard.action.lock.GetLockDeviceStatusAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class FetchSmartCardLockState extends Command<SmartCard> implements InjectableAction, SmartCardModifier {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SnappyRepository snappyRepository;
   @Inject SmartCardInteractor smartCardInteractor;

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      janet.createPipe(GetLockDeviceStatusAction.class)
            .createObservableResult(new GetLockDeviceStatusAction())
            .flatMap(it -> fetchActiveSmartCard().map(smartCard -> ImmutableSmartCard.builder()
                  .from(smartCard)
                  .lock(it.locked)
                  .build()))
            .doOnNext(snappyRepository::saveSmartCard)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<SmartCard> fetchActiveSmartCard() {
      return smartCardInteractor.activeSmartCardPipe()
            .createObservable(new GetActiveSmartCardCommand())
            .compose(new ActionStateToActionTransformer<>())
            .map(Command::getResult);
   }
}

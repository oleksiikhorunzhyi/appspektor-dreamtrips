package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.smart_card.location.CreateSmartCardLocationHttpAction;
import com.worldventures.dreamtrips.api.smart_card.location.model.ImmutableSmartCardLocationBody;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationBody;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.ImmutableWalletLocation;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository;
import com.worldventures.dreamtrips.wallet.util.WalletLocationsUtil;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.CancelException;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

@CommandAction
public class PostLocationCommand extends Command<Void> implements InjectableAction{

   @Inject Janet janet;
   @Inject LostCardRepository locationRepository;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject MapperyContext mapperyContext;
   private final PublishSubject<Void> commandPublishSubject;

   public PostLocationCommand() {
      this.commandPublishSubject = PublishSubject.create();
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      final WalletLocation walletLocation = WalletLocationsUtil.getLatestLocation(locationRepository.getWalletLocations());
      if (walletLocation != null && walletLocation.postedAt() != null) {
         callback.onSuccess(null);
         return;
      }
      Observable.merge(postLocations(), commandPublishSubject)
            .flatMap(aVoid -> wipeRedundantLocations())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> postLocations() {
      return observeActiveSmartCard()
            .flatMap(activeSmartCardCommand -> observeLocationsPost(activeSmartCardCommand.getResult().smartCardId()))
            .map(createSmartCardLocationHttpAction -> (Void) null);
   }

   private Observable<ActiveSmartCardCommand> observeActiveSmartCard() {
      return smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand());
   }

   private Observable<CreateSmartCardLocationHttpAction> observeLocationsPost(String smartCardId) {
      return janet.createPipe(CreateSmartCardLocationHttpAction.class, Schedulers.io())
            .createObservableResult(new CreateSmartCardLocationHttpAction(Long.parseLong(smartCardId),
                  prepareRequestBody(locationRepository.getWalletLocations())));

   }

   private Observable<Void> wipeRedundantLocations() {
      final WalletLocation lastLocation = Queryable.from(locationRepository.getWalletLocations())
            .sort((smartCardLocation1, smartCardLocation2)
                  -> smartCardLocation1.createdAt().compareTo(smartCardLocation2.createdAt()))
            .last();
      final WalletLocation postedLocation = ImmutableWalletLocation.builder()
            .from(lastLocation)
            .postedAt(Calendar.getInstance().getTime())
            .build();
      locationRepository.saveWalletLocations(Collections.singletonList(postedLocation));
      return Observable.just(null);
   }

   private SmartCardLocationBody prepareRequestBody(List<WalletLocation> locations) {
      return ImmutableSmartCardLocationBody.builder()
            .locations(mapperyContext.convert(locations, SmartCardLocation.class))
            .build();
   }

   @Override
   protected void cancel() {
      commandPublishSubject.onError(new CancelException());
   }
}

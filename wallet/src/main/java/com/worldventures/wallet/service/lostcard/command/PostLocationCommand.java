package com.worldventures.wallet.service.lostcard.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.api.smart_card.location.CreateSmartCardLocationHttpAction;
import com.worldventures.dreamtrips.api.smart_card.location.model.ImmutableSmartCardLocationBody;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationBody;
import com.worldventures.wallet.domain.entity.SmartCard;
import com.worldventures.wallet.domain.entity.lostcard.ImmutableWalletLocation;
import com.worldventures.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.lostcard.LostCardRepository;
import com.worldventures.wallet.util.WalletLocationsUtil;

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
public class PostLocationCommand extends Command<Void> implements InjectableAction {

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
      final List<WalletLocation> savedLocations = Collections.unmodifiableList(locationRepository.getWalletLocations());
      final WalletLocation walletLocation = WalletLocationsUtil.getLatestLocation(savedLocations);
      if (walletLocation == null || walletLocation.postedAt() != null) {
         callback.onSuccess(null);
         return;
      }
      Observable.merge(postLocations(savedLocations), commandPublishSubject)
            .flatMap(aVoid -> wipeRedundantLocations(savedLocations))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> postLocations(List<WalletLocation> locations) {
      return observeActiveSmartCard()
            .flatMap(smartCard -> observeLocationsPost(locations, smartCard))
            .map(action -> (Void) null);
   }

   private Observable<SmartCard> observeActiveSmartCard() {
      return smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand())
            .map(Command::getResult);
   }

   private Observable<CreateSmartCardLocationHttpAction> observeLocationsPost(List<WalletLocation> locations, SmartCard smartCard) {
      return janet.createPipe(CreateSmartCardLocationHttpAction.class, Schedulers.io())
            .createObservableResult(new CreateSmartCardLocationHttpAction(Long.parseLong(smartCard.smartCardId()),
                  prepareRequestBody(locations)));

   }

   private Observable<Void> wipeRedundantLocations(List<WalletLocation> locations) {
      final WalletLocation lastLocation = Queryable.from(locations)
            .sort((location1, location2) -> location1.createdAt().compareTo(location2.createdAt()))
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

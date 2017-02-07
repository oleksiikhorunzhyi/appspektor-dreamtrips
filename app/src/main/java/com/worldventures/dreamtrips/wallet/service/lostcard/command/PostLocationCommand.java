package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.smart_card.location.CreateSmartCardLocationHttpAction;
import com.worldventures.dreamtrips.api.smart_card.location.model.ImmutableSmartCardLocationBody;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationBody;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.SystemPropertiesProvider;
import com.worldventures.dreamtrips.wallet.service.lostcard.SCLocationRepository;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.CancelException;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class PostLocationCommand extends Command<Void> implements InjectableAction{

   @Inject @Named(JANET_API_LIB) Janet janet;
   @Inject SCLocationRepository locationRepository;
   @Inject SystemPropertiesProvider propertiesProvider;
   private final PublishSubject<Void> commandPublishSubject;

   public PostLocationCommand() {
      this.commandPublishSubject = PublishSubject.create();
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      if (locationRepository.getSmartCardLocations().size() < 1) {
         callback.onSuccess(null);
         return;
      }
      Observable.merge(postLocations(), commandPublishSubject)
            .subscribe((result) -> {
               wipeRedundantLocations();
               callback.onSuccess(result);
            }, callback::onFail);
   }

   private Observable<Void> postLocations() {
      return janet.createPipe(CreateSmartCardLocationHttpAction.class, Schedulers.io())
         .createObservableResult(new CreateSmartCardLocationHttpAction(Long.parseLong(propertiesProvider.deviceId()),
               prepareRequestBody(locationRepository.getSmartCardLocations())))
         .map(createSmartCardLocationHttpAction -> (Void) null);
   }

   private void wipeRedundantLocations() {
      final SmartCardLocation lastLocation = Queryable.from(locationRepository.getSmartCardLocations())
            .sort((smartCardLocation1, smartCardLocation2)
                  -> smartCardLocation1.createdAt().compareTo(smartCardLocation2.createdAt()))
            .first();
      locationRepository.saveSmartCardLocations(Collections.singletonList(lastLocation));
   }

   private SmartCardLocationBody prepareRequestBody(List<SmartCardLocation> locations) {
      return ImmutableSmartCardLocationBody.builder()
            .locations(locations)
            .build();
   }

   @Override
   protected void cancel() {
      commandPublishSubject.onError(new CancelException());
   }
}

package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import com.worldventures.dreamtrips.api.smart_card.location.CreateSmartCardLocationHttpAction;
import com.worldventures.dreamtrips.api.smart_card.location.model.ImmutableSmartCardLocationBody;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationBody;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.SystemPropertiesProvider;
import com.worldventures.dreamtrips.wallet.service.lostcard.SCLocationRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class PostLocationCommand extends Command<Void> implements InjectableAction{

   @Inject @Named(JANET_API_LIB) Janet janet;
   @Inject SCLocationRepository locationRepository;
   @Inject SystemPropertiesProvider propertiesProvider;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      //TODO : Implement JobScheduler and send command to it
      janet.createPipe(CreateSmartCardLocationHttpAction.class, Schedulers.io())
            .createObservableResult(new CreateSmartCardLocationHttpAction(Long.parseLong(propertiesProvider.deviceId()),
                  prepareRequestBody(Collections.singletonList(locationRepository.getSmartCardLocation()))))
            .map(createSmartCardLocationHttpAction -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private SmartCardLocationBody prepareRequestBody(List<SmartCardLocation> locations) {
      return ImmutableSmartCardLocationBody.builder()
            .locations(locations)
            .build();
   }
}

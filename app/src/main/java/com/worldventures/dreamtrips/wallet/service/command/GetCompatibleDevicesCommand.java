package com.worldventures.dreamtrips.wallet.service.command;


import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.api.smart_card.user_association.GetCompatibleDevicesHttpAction;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class GetCompatibleDevicesCommand extends Command<List<Device>> implements InjectableAction {

   @Inject @Named(JANET_API_LIB) Janet janet;

   @Override
   protected void run(CommandCallback<List<Device>> callback) throws Throwable {
      List<Device> allDevices = new ArrayList<>();
      final int[] page = {1};
      final int pageSize = 20;
      final boolean[] hasMore = {true};
      ActionPipe<GetCompatibleDevicesHttpAction> pipe = janet.createPipe(GetCompatibleDevicesHttpAction.class);
      while (hasMore[0]) {
         pipe.createObservableResult(new GetCompatibleDevicesHttpAction(page[0], pageSize))
               .map(action -> action.response())
               .toBlocking()
               .subscribe(
                     devices -> {
                        allDevices.addAll(devices);
                        if (devices.size() < pageSize) hasMore[0] = false;
                        page[0]++;
                     },
                     t -> {
                        hasMore[0] = false;
                        callback.onFail(t);
                     }
               );
      }
      callback.onSuccess(allDevices);
   }
}

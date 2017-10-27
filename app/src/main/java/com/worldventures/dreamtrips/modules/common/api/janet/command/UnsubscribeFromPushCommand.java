package com.worldventures.dreamtrips.modules.common.api.janet.command;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.iid.InstanceID;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.service.NewDreamTripsHttpService;
import com.worldventures.dreamtrips.api.push_notifications.UnsubscribeFromPushNotificationsHttpAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import java.io.IOException;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@CommandAction
public class UnsubscribeFromPushCommand extends Command<Void> implements InjectableAction {

   @Inject Janet janet;
   @Inject Context context;
   @Inject SnappyRepository snappyRepository;
   @Inject SessionHolder appSessionHolder;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      final String pushToken = snappyRepository.getGcmRegToken();
      final String sessionToken = appSessionHolder.get().get().apiToken();
      if (TextUtils.isEmpty(pushToken)) callback.onSuccess(null);
      else {
         UnsubscribeFromPushNotificationsHttpAction unsubscribeFromPushAction = new UnsubscribeFromPushNotificationsHttpAction(pushToken);
         unsubscribeFromPushAction.setAuthorizationHeader(NewDreamTripsHttpService.getAuthorizationHeader(sessionToken));
         janet.createPipe(UnsubscribeFromPushNotificationsHttpAction.class, Schedulers.io())
               .createObservableResult(unsubscribeFromPushAction)
               .doOnNext(action -> {
                  try {
                     InstanceID.getInstance(context).deleteInstanceID();
                  } catch (IOException e) {
                     Timber.e(e, "Failed to delete instance ID");
                  }
               })
               .subscribe(action -> callback.onSuccess(null), callback::onFail);
      }
   }
}

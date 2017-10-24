package com.worldventures.dreamtrips.core.api.uploadery;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.uploadery.UploadSmartCardImageHttpAction;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.util.HttpUploaderyException;
import com.worldventures.dreamtrips.wallet.util.MaltyPartImageBodyCreator;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SmartCardUploaderyCommand extends BaseUploadImageCommand<UploadSmartCardImageHttpAction> implements InjectableAction {

   @Inject Janet janet;
   @Inject SessionHolder<UserSession> userSessionHolder;
   @Inject MaltyPartImageBodyCreator creator;

   private final String photoUri;
   private final String smartCardId;

   public SmartCardUploaderyCommand(String smartCardId, String photoUri) {
      this.smartCardId = smartCardId;
      this.photoUri = photoUri;
   }

   @Override
   protected void run(CommandCallback<UploadSmartCardImageHttpAction> callback) throws Exception {
      final String username = userSessionHolder.get().get().getUsername();
      creator.createBody(photoUri)
            .flatMap(body -> janet.createPipe(UploadSmartCardImageHttpAction.class)
                  .createObservableResult(new UploadSmartCardImageHttpAction(
                        BuildConfig.UPLOADERY_API_URL,
                        username,
                        smartCardId,
                        body))
            ).subscribe(callback::onSuccess, throwable -> callback.onFail(new HttpUploaderyException(throwable)));
   }
}

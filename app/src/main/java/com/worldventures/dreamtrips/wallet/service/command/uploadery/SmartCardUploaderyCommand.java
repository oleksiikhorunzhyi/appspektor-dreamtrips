package com.worldventures.dreamtrips.wallet.service.command.uploadery;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.uploadery.UploadSmartCardImageHttpAction;
import com.worldventures.dreamtrips.core.api.uploadery.BaseUploadImageCommand;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.HttpUploaderyException;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;
import com.worldventures.dreamtrips.wallet.util.MaltyPartImageBodyCreator;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SmartCardUploaderyCommand extends BaseUploadImageCommand<UploadSmartCardImageHttpAction> implements InjectableAction {

   @Inject Janet janet;
   @Inject WalletSocialInfoProvider socialInfoProvider;
   @Inject MaltyPartImageBodyCreator creator;

   private final String photoUri;
   private final String smartCardId;

   public SmartCardUploaderyCommand(String smartCardId, String photoUri) {
      this.smartCardId = smartCardId;
      this.photoUri = photoUri;

   }

   @Override
   protected void run(CommandCallback<UploadSmartCardImageHttpAction> callback) throws Exception {
      final String username = socialInfoProvider.username();
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

package com.worldventures.wallet.service.command.uploadery;

import com.worldventures.core.modules.infopages.StaticPageProviderConfig;
import com.worldventures.core.service.UriPathProvider;
import com.worldventures.core.service.command.BaseUploadImageCommand;
import com.worldventures.core.utils.HttpUploaderyException;
import com.worldventures.dreamtrips.api.uploadery.UploadSmartCardImageHttpAction;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.service.WalletSocialInfoProvider;
import com.worldventures.wallet.util.MaltyPartImageBodyCreator;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SmartCardUploaderyCommand extends BaseUploadImageCommand<UploadSmartCardImageHttpAction> implements InjectableAction {

   @Inject Janet janet;
   @Inject WalletSocialInfoProvider socialInfoProvider;
   @Inject MaltyPartImageBodyCreator creator;
   @Inject UriPathProvider uriPathProvider;
   @Inject StaticPageProviderConfig staticPageProviderConfig;

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
                        staticPageProviderConfig.uploaderyUrl(),
                        username,
                        smartCardId,
                        body))
            ).subscribe(callback::onSuccess, throwable -> callback.onFail(new HttpUploaderyException(throwable)));
   }

   @Override
   public UriPathProvider getUriPathProvider() {
      return uriPathProvider;
   }
}

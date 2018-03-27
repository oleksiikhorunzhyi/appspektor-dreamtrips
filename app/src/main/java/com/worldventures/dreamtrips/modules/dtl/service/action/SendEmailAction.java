package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.SendEmailHttpAction;
import com.worldventures.janet.injection.InjectableAction;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class SendEmailAction extends Command implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject SessionHolder sessionHolder;

   private final String merchantId;
   private final String transactionId;
   private final String imageRoute;

   public SendEmailAction(String merchantId, String transactionId, String imageRoute) {
      this.merchantId = merchantId;
      this.transactionId = transactionId;
      this.imageRoute = imageRoute;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      callback.onProgress(0);

      janet.createPipe(SendEmailHttpAction.class)
            .createObservableResult(new SendEmailHttpAction(merchantId, transactionId, imageRoute,
                  sessionHolder.get().get().username(), sessionHolder.get().get().legacyApiToken()))
            .subscribe(sendEmailHttpAction -> {
               new File(imageRoute).delete();
               callback.onSuccess(sendEmailHttpAction);
            }, callback::onFail);
   }
}

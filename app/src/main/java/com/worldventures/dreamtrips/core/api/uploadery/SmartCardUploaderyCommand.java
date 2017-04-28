package com.worldventures.dreamtrips.core.api.uploadery;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.uploadery.UploadSmartCardImageHttpAction;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.util.HttpUploaderyException;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class SmartCardUploaderyCommand extends BaseUploadImageCommand<UploadSmartCardImageHttpAction> implements InjectableAction {

   @ForApplication @Inject Context context;
   @Inject Janet janet;
   @Inject SessionHolder<UserSession> userSessionHolder;

   private final File file;
   private final String smartCardId;

   public SmartCardUploaderyCommand(String smartCardId, File file) {
      this.smartCardId = smartCardId;
      this.file = file;
   }

   @Override
   protected void run(CommandCallback<UploadSmartCardImageHttpAction> callback) throws Exception{
      final String username = userSessionHolder.get().get().getUsername();
      janet.createPipe(UploadSmartCardImageHttpAction.class, Schedulers.io())
            .createObservableResult(new UploadSmartCardImageHttpAction(BuildConfig.UPLOADERY_API_URL, username, smartCardId, file))
            .subscribe(callback::onSuccess, throwable -> callback.onFail(new HttpUploaderyException(throwable)));
   }

}

package com.worldventures.dreamtrips.core.api.uploadery;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.api.uploadery.UploadSmartCardImageHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.util.HttpUploaderyException;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionState;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;
import rx.schedulers.Schedulers;

@CommandAction
public class SmartCardSimpleUploaderyCommand extends BaseUploadImageCommand<UploadSmartCardImageHttpAction> implements InjectableAction {

   @ForApplication @Inject Context context;
   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject SessionHolder<UserSession> userSessionHolder;

   private final String fileUri;
   private final String smartcardId;

   public SmartCardSimpleUploaderyCommand(String smartcardId, String fileUri) {
      this.smartcardId = smartcardId;
      this.fileUri = fileUri;
   }

   @Override
   protected void run(CommandCallback<UploadSmartCardImageHttpAction> callback) {
      getFileObservable(context, fileUri)
            .flatMap(this::upload)
            .compose(nextAction())
            .subscribe(callback::onSuccess, throwable -> callback.onFail(new HttpUploaderyException(throwable)));
   }

   protected Observable<ActionState<UploadSmartCardImageHttpAction>> upload(File file) {
      try {
         String userId = userSessionHolder.get().get().getUsername();
         UploadSmartCardImageHttpAction action = new UploadSmartCardImageHttpAction(BuildConfig.UPLOADERY_API_URL, userId, smartcardId, file);
         return janet.createPipe(UploadSmartCardImageHttpAction.class)
               .createObservable(action);
      } catch (IOException e) {
         return Observable.error(e);
      }
   }


   private Observable.Transformer<ActionState<UploadSmartCardImageHttpAction>, UploadSmartCardImageHttpAction> nextAction() {
      return uploadImageActionObservable -> uploadImageActionObservable.compose(new ActionStateToActionTransformer<>());
   }

}

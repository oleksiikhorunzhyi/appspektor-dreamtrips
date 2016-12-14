package com.worldventures.dreamtrips.core.api.uploadery;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.api.uploadery.UploadImageHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetUploaderyModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionState;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

@CommandAction
public abstract class UploaderyImageCommand<T> extends BaseUploadImageCommand<T> implements InjectableAction {

   @ForApplication @Inject Context context;
   @Inject @Named(JanetUploaderyModule.JANET_UPLOADERY) Janet janet;
   @Inject StaticPageProvider staticPageProvider;

   private final String fileUri;

   public UploaderyImageCommand(String fileUri) {
      this.fileUri = fileUri;
   }

   @Override
   protected void run(CommandCallback<T> callback) {
      getFileObservable(context, fileUri).flatMap(this::upload)
            .doOnNext(action -> {
               if (action.status == ActionState.Status.PROGRESS) callback.onProgress(action.progress);
            })
            .compose(nextAction())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public String getFileUri() {
      return fileUri;
   }

   protected Observable<ActionState<UploadImageHttpAction>> upload(File file) {
      String uploaderyUrl = staticPageProvider.getUploaderyUrl();
      try {
         return janet.createPipe(UploadImageHttpAction.class, Schedulers.io())
               .createObservable(new UploadImageHttpAction(uploaderyUrl, file));
      } catch (IOException e) {
         return Observable.error(e);
      }
   }

   protected abstract Observable.Transformer<ActionState<UploadImageHttpAction>, T> nextAction();
}

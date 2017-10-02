package com.worldventures.dreamtrips.core.api.uploadery;

import android.content.Context;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.modules.infopages.StaticPageProvider;
import com.worldventures.core.service.UriPathProvider;
import com.worldventures.core.utils.HttpUploaderyException;
import com.worldventures.dreamtrips.api.uploadery.UploadImageHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetUploaderyModule;

import java.io.File;

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
   @Inject UriPathProvider uriPathProvider;

   private final String fileUri;

   public UploaderyImageCommand(String fileUri) {
      this.fileUri = fileUri;
   }

   @Override
   protected void run(CommandCallback<T> callback) {
      getFileObservable(fileUri).flatMap(this::upload)
            .doOnNext(action -> {
               if (action.status == ActionState.Status.PROGRESS) callback.onProgress(action.progress);
            })
            .compose(nextAction())
            .subscribe(callback::onSuccess, throwable -> callback.onFail(new HttpUploaderyException(throwable)));

   }

   public String getFileUri() {
      return fileUri;
   }

   protected Observable<ActionState<UploadImageHttpAction>> upload(File file) {
      String uploaderyUrl = staticPageProvider.getUploaderyUrl();
      return janet.createPipe(UploadImageHttpAction.class, Schedulers.io())
            .createObservable(new UploadImageHttpAction(uploaderyUrl, file));
   }

   @Override
   public UriPathProvider getUriPathProvider() {
      return uriPathProvider;
   }

   protected abstract Observable.Transformer<ActionState<UploadImageHttpAction>, T> nextAction();
}

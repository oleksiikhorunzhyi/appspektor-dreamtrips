package com.worldventures.dreamtrips.core.api.uploadery;

import android.content.Context;
import android.net.Uri;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.delegate.system.UriPathProvider;

import java.io.File;
import java.net.URISyntaxException;

import javax.inject.Inject;

import io.techery.janet.Command;
import rx.Observable;

public abstract class BaseUploadImageCommand<T> extends Command<T> implements InjectableAction {
   @Inject UriPathProvider uriPathProvider;

   Observable<File> getFileObservable(String filePath) {
      return Observable.create(subscriber -> {
         if (!subscriber.isUnsubscribed()) {
            try {
               String path = uriPathProvider.getPath(Uri.parse(filePath));
               if (path != null) subscriber.onNext(new File(path));
               else subscriber.onError(new NullPointerException("Path cannot be null"));

            } catch (URISyntaxException e) {
               subscriber.onError(e);
            }
         }
      });
   }
}
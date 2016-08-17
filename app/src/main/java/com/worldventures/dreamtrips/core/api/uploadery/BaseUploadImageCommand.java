package com.worldventures.dreamtrips.core.api.uploadery;

import android.content.Context;
import android.net.Uri;

import com.worldventures.dreamtrips.core.utils.FileUtils;

import java.io.File;
import java.net.URISyntaxException;

import io.techery.janet.Command;
import rx.Observable;

public abstract class BaseUploadImageCommand<T> extends Command<T> {
   Observable<File> getFileObservable(Context context, String filePath) {
      return Observable.create(subscriber -> {
         if (!subscriber.isUnsubscribed()) {
            try {
               String path = FileUtils.getPath(context, Uri.parse(filePath));
               if (path != null) subscriber.onNext(new File(path));
               else subscriber.onError(new IllegalArgumentException());

            } catch (URISyntaxException e) {
               subscriber.onError(e);
            }
         }
      });
   }
}
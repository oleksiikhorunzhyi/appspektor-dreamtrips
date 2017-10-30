package com.worldventures.core.service.command;

import android.net.Uri;

import com.worldventures.core.service.UriPathProvider;
import com.worldventures.janet.injection.InjectableAction;

import java.io.File;

import io.techery.janet.Command;
import rx.Observable;

public abstract class BaseUploadImageCommand<T> extends Command<T> implements InjectableAction {

   protected Observable<File> getFileObservable(String filePath) {
      return Observable.fromCallable(() -> {
         final String path = getUriPathProvider().getPath(Uri.parse(filePath));
         if (path == null) {
            throw new NullPointerException("Path cannot be null");
         }
         return new File(path);
      });
   }

   public abstract UriPathProvider getUriPathProvider();
}

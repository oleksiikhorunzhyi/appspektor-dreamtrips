package com.worldventures.dreamtrips.social.ui.tripsimages.service.delegate;

import com.worldventures.core.ui.util.permission.PermissionDispatcher;
import com.worldventures.core.ui.util.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DownloadImageCommand;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.functions.Action2;

import static com.worldventures.core.ui.util.permission.PermissionConstants.WRITE_EXTERNAL_STORAGE;

public class DownloadImageDelegate {

   private TripImagesInteractor tripImagesInteractor;
   private PermissionDispatcher permissionDispatcher;

   public DownloadImageDelegate(TripImagesInteractor tripImagesInteractor, PermissionDispatcher permissionDispatcher) {
      this.tripImagesInteractor = tripImagesInteractor;
      this.permissionDispatcher = permissionDispatcher;
   }

   public void downloadImage(String url, Observable.Transformer viewStopper, Action2<DownloadImageCommand, Throwable> onError) {
      permissionDispatcher.requestPermission(WRITE_EXTERNAL_STORAGE, false)
            .compose(viewStopper)
            .subscribe(new PermissionSubscriber().onPermissionGrantedAction(() -> sendCommand(url, viewStopper, onError)));
   }

   private void sendCommand(String url, Observable.Transformer viewStopper, Action2<DownloadImageCommand, Throwable> onError) {
      tripImagesInteractor.downloadImageActionPipe()
            .createObservable(new DownloadImageCommand(url))
            .compose(bindIoToMain(viewStopper))
            .subscribe(new ActionStateSubscriber<DownloadImageCommand>()
                  .onFail(onError::call));
   }

   private <T> Observable.Transformer<T, T> bindIoToMain(Observable.Transformer stopper) {
      return input -> input.compose(new IoToMainComposer<>()).compose(stopper);
   }

}

package com.worldventures.dreamtrips.social.ui.tripsimages.service.delegate

import com.worldventures.core.ui.util.permission.PermissionConstants.WRITE_EXTERNAL_STORAGE
import com.worldventures.core.ui.util.permission.PermissionDispatcher
import com.worldventures.core.ui.util.permission.PermissionSubscriber
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DownloadImageCommand
import io.techery.janet.helper.ActionStateSubscriber
import rx.Observable
import rx.functions.Action2

class DownloadImageDelegate(private val tripImagesInteractor: TripImagesInteractor, private val permissionDispatcher: PermissionDispatcher) {

   fun downloadImage(url: String, viewStopper: Observable.Transformer<Any, Any>,
                     onError: Action2<DownloadImageCommand, Throwable>) {
      permissionDispatcher.requestPermission(WRITE_EXTERNAL_STORAGE, false)
            .compose(castComposer(viewStopper))
            .subscribe(PermissionSubscriber().onPermissionGrantedAction { sendCommand(url, viewStopper, onError) })
   }

   private fun sendCommand(url: String, viewStopper: Observable.Transformer<Any, Any>, onError: Action2<DownloadImageCommand, Throwable>) {
      tripImagesInteractor.downloadImageActionPipe
            .createObservable(DownloadImageCommand(url))
            .compose(castComposer(viewStopper))
            .subscribe(ActionStateSubscriber<DownloadImageCommand>().onFail(onError::call))
   }

   @Suppress("UnsafeCast")
   private fun <T> castComposer(stopper: Observable.Transformer<Any, Any>): Observable.Transformer<T, T> =
         stopper as Observable.Transformer<T, T>
}

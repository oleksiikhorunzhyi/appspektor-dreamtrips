package com.worldventures.dreamtrips.social.ui.tripsimages.service.delegate

import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.BaseMediaCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.TripImagesCommandFactory
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs
import rx.Observable
import rx.Subscription
import rx.subjects.PublishSubject
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class MediaRefresher(private val tripImagesInteractor: TripImagesInteractor,
                     private val tripImagesCommandFactory: TripImagesCommandFactory) {
   val lastPhotos = ArrayList<BaseMediaEntity<*>>()
   val newPhotosObservable = PublishSubject.create<List<BaseMediaEntity<*>>>()
   private var activeRefreshSubscription: Subscription? = null

   fun startRefreshing(refreshAfter: Observable<BaseMediaEntity<*>>, args: TripImagesArgs) {
      stopRefreshing()
      activeRefreshSubscription = Observable.interval(0, REFRESH_INTERVAL_SEC.toLong(), TimeUnit.SECONDS)
            .flatMap { refreshAfter }
            .flatMap { newestMedia -> getRefreshCommandObservable(args, newestMedia) }
            .map { it.result }
            .doOnNext { photos ->
               lastPhotos.clear()
               lastPhotos.addAll(photos)
               newPhotosObservable.onNext(ArrayList<BaseMediaEntity<*>>(photos))
            }
            .subscribe()
   }

   private fun getRefreshCommandObservable(args: TripImagesArgs, newestMedia: BaseMediaEntity<*>): Observable<BaseMediaCommand> =
         tripImagesInteractor
            .baseTripImagesPipe
            .createObservableResult(tripImagesCommandFactory.provideRefreshCommand(args, newestMedia))

   fun stopRefreshing() {
      activeRefreshSubscription?.let {
         if (!it.isUnsubscribed) {
            it.unsubscribe()
         }
      }

      lastPhotos.clear()
   }

   companion object {

      private val REFRESH_INTERVAL_SEC = 30
   }
}

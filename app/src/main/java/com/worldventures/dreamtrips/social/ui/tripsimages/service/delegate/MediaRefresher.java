package com.worldventures.dreamtrips.social.ui.tripsimages.service.delegate;

import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.BaseMediaCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.TripImagesCommandFactory;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

public class MediaRefresher {

   private static final int REFRESH_INTERVAL_SEC = 30;

   private TripImagesInteractor tripImagesInteractor;
   private TripImagesCommandFactory tripImagesCommandFactory;

   private Subscription activeRefreshSubscription;
   private List<BaseMediaEntity> lastPhotos = new ArrayList<>();
   private PublishSubject<List<BaseMediaEntity>> newPhotosSubject = PublishSubject.create();

   public MediaRefresher(TripImagesInteractor tripImagesInteractor, TripImagesCommandFactory tripImagesCommandFactory) {
      this.tripImagesInteractor = tripImagesInteractor;
      this.tripImagesCommandFactory = tripImagesCommandFactory;
   }

   public void startRefreshing(Observable<BaseMediaEntity> refreshAfter, TripImagesArgs args) {
      stopRefreshing();
      activeRefreshSubscription = Observable.interval(0, REFRESH_INTERVAL_SEC, TimeUnit.SECONDS)
            .flatMap(timer -> refreshAfter)
            .flatMap(newestMedia -> tripImagesInteractor
                  .baseTripImagesCommandActionPipe()
                  .createObservableResult(tripImagesCommandFactory.provideRefreshCommand(args, newestMedia)))
            .map(BaseMediaCommand::getResult)
            .doOnNext(photos -> {
               lastPhotos.clear();
               lastPhotos.addAll(photos);
               newPhotosSubject.onNext(photos);
            })
            .subscribe();
   }

   public void stopRefreshing() {
      if (activeRefreshSubscription != null && !activeRefreshSubscription.isUnsubscribed()) {
         activeRefreshSubscription.unsubscribe();
         activeRefreshSubscription = null;
      }
      lastPhotos.clear();
   }

   public Observable<List<BaseMediaEntity>> getNewPhotosObservable() {
      return newPhotosSubject;
   }

   public List<BaseMediaEntity> getLastPhotos() {
      return lastPhotos;
   }
}
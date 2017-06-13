package com.worldventures.dreamtrips.modules.tripsimages.service.delegate;

import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetMembersPhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.ImmutablePaginationParams;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

public class MemberImagesRefresher {

   private static final int REFRESH_INTERVAL_SEC = 30;

   private PublishSubject<List<Photo>> newPhotosSubject = PublishSubject.create();
   private TripImagesInteractor tripImagesInteractor;
   private Subscription activeRefreshSubscription;
   private List<Photo> lastPhotos = new ArrayList<>();

   public MemberImagesRefresher(TripImagesInteractor tripImagesInteractor) {
      this.tripImagesInteractor = tripImagesInteractor;
   }

   public void startRefreshing(Observable<Photo> refreshAfter, int pageSize) {
      stopRefreshing();
      activeRefreshSubscription = Observable.interval(0, REFRESH_INTERVAL_SEC, TimeUnit.SECONDS)
            .flatMap(timer -> refreshAfter)
            .flatMap(photo -> {
               return tripImagesInteractor.getMembersPhotosPipe()
                     .createObservableResult(new GetMembersPhotosCommand(ImmutablePaginationParams.builder()
                           .after(photo.getCreatedAt()).perPage(pageSize).build()));
            })
            .map(GetMembersPhotosCommand::getResult)
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

   public Observable<List<Photo>> getNewPhotosObservable() {
      return newPhotosSubject;
   }

   public List<Photo> getLastPhotos() {
      return lastPhotos;
   }
}

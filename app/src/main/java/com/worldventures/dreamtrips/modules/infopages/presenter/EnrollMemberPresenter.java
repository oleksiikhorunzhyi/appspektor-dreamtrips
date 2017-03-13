package com.worldventures.dreamtrips.modules.infopages.presenter;

import android.location.Location;

import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;

public class EnrollMemberPresenter extends AuthorizedStaticInfoPresenter {

   @Inject LocationDelegate locationDelegate;
   @Inject StaticPageProvider staticPageProvider;

   public EnrollMemberPresenter(String url) {
      super(url);
   }

   @Override
   public void load() {
      doWithLocation()
            .subscribe(location -> {
               updateUrlWithLocation(location);
               super.load();
            }, e -> super.load());
   }

   @Override
   protected void reload() {
      doWithLocation()
            .subscribe(location -> {
               updateUrlWithLocation(location);
               super.reload();
            }, e -> super.reload());
   }

   private void updateUrlWithLocation(Location location) {
      url = staticPageProvider.getEnrollWithLocation(location.getLatitude(), location.getLongitude());
   }

   private Observable<Location> doWithLocation() {
      return Observable.merge(
            Observable.timer(2, TimeUnit.SECONDS).flatMap(unit -> Observable.error(new IllegalStateException())),
            locationDelegate.requestLocationUpdate())
            .take(1)
            .compose(bindViewToMainComposer());
   }
}

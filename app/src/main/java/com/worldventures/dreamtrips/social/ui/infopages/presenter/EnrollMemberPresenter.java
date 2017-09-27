package com.worldventures.dreamtrips.social.ui.infopages.presenter;

import android.location.Location;

import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.social.ui.infopages.StaticPageProvider;

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

   // FIXME: 7/5/17 locationDelegate.requestLocationUpdate() holds reference to the context even after unSubscribe
   // Bug is known (https://github.com/mcharmas/Android-ReactiveLocation/pull/142) but the library seems to be abandoned.
   private Observable<Location> doWithLocation() {
      return Observable.merge(
            Observable.timer(2, TimeUnit.SECONDS).flatMap(unit -> Observable.error(new IllegalStateException())),
            locationDelegate.requestLocationUpdate())
            .take(1)
            .compose(bindViewToMainComposer());
   }
}

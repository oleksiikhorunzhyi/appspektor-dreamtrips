package com.worldventures.dreamtrips.modules.infopages.presenter;

import android.location.Location;

import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;

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
      url = staticPageProvider.fillWithLocation(url, location.getLatitude(), location.getLongitude());
   }

   private Observable<Location> doWithLocation() {
      return locationDelegate.requestLocationUpdate()
            .take(1)
            .compose(bindViewToMainComposer());
   }
}

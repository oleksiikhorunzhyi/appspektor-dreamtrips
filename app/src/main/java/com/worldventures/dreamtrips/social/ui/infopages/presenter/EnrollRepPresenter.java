package com.worldventures.dreamtrips.social.ui.infopages.presenter;

import android.location.Location;

import com.worldventures.core.modules.infopages.StaticPageProvider;
import com.worldventures.dreamtrips.social.ui.infopages.util.PermissionLocationDelegate;
import com.worldventures.dreamtrips.social.ui.util.PermissionUIComponent;

import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public class EnrollRepPresenter extends AuthorizedStaticInfoPresenter<EnrollRepPresenter.View> {

   @Inject StaticPageProvider staticPageProvider;
   @Inject PermissionLocationDelegate permissionLocationDelegate;

   public EnrollRepPresenter(String url) {
      super(url);
   }

   @Override
   public void takeView(EnrollRepPresenter.View view) {
      permissionLocationDelegate.setNeedRationalAction(view::showPermissionExplanationText);
      super.takeView(view);
   }

   @Override
   public void load() {
      permissionLocationDelegate.setLocationObtainedAction(location -> {
         updateUrlWithLocation(location);
         super.load();
      });
      permissionLocationDelegate.requestPermission(true, bindView());
   }

   @Override
   protected void reload() {
      permissionLocationDelegate.setLocationObtainedAction(location -> {
         updateUrlWithLocation(location);
         super.reload();
      });
      permissionLocationDelegate.requestPermission(true, bindView());
   }

   private void updateUrlWithLocation(@Nullable Location location) {
      if (location == null) {
         return;
      }
      url = staticPageProvider.getEnrollWithLocation(location.getLatitude(), location.getLongitude());
   }

   public void recheckPermissionAccepted(boolean recheckAccepted) {
      permissionLocationDelegate.recheckPermissionAccepted(recheckAccepted, bindView());
   }

   public interface View extends AuthorizedStaticInfoPresenter.View, PermissionUIComponent {

   }

}
